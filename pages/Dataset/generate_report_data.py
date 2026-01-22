import json
from pathlib import Path

import pandas as pd

base = Path('/Users/abbey/ccl3_ws2025-1/pages')
data_dir = base / 'Dataset'
output = data_dir / 'report_data.json'

sus_path = data_dir / 'System Usability Scale (SUS) Questionnaire (Responses) - Form responses 1.csv'

sus = pd.read_csv(sus_path)

sus_items = [
    'I think that I would like to use this system frequently.',
    'I found the system unnecessarily complex.',
    'I thought the system was easy to use.',
    'I think that I would need the support of a technical person to be able to use this system.',
    'I found the various functions in this system were well integrated.',
    'I thought there was too much inconsistency in this system.',
    'I would imagine that most people would learn to use this system very quickly.',
    'I found the system very cumbersome to use.',
    'I felt very confident using the system.',
    'I needed to learn a lot of things before I could get going with this system.'
]

sus_numeric = sus[sus_items].apply(pd.to_numeric, errors='coerce')
odd_idx = [0, 2, 4, 6, 8]
even_idx = [1, 3, 5, 7, 9]
odd_score = sus_numeric.iloc[:, odd_idx].apply(lambda x: x - 1)
even_score = sus_numeric.iloc[:, even_idx].apply(lambda x: 5 - x)

sus_score = (odd_score.sum(axis=1) + even_score.sum(axis=1)) * 2.5

age_counts = sus['What is your age group'].value_counts().sort_index()
exp_counts = sus['What is your experience with the harmonica?'].value_counts().sort_index()
app_counts = sus['Have you used music learning or tab-based apps before?'].value_counts().sort_index()

cols = [
    'Find a tab titled Amazing Grace and open it.',
    'Show only Medium difficulty tabs and sort them by newest.',
    'Mark any tab as a favourite and show only your favourite tabs.',
    'Create a new tab with a title, key, difficulty, tags, and at least two lines of notes. Save it.',
    'Edit the tab you created, then try to leave without saving.',
    'Start practice mode and move between different lines.'
]

task_df = sus[cols].apply(pd.to_numeric, errors='coerce')

outcome_labels = ['Success', 'Partial', 'Fail']

def classify(val):
    if pd.isna(val):
        return None
    if val >= 5:
        return 'Success'
    if val == 4:
        return 'Partial'
    return 'Fail'

outcome_counts = {}
for col in cols:
    outcomes = task_df[col].map(classify)
    counts = outcomes.value_counts().reindex(outcome_labels, fill_value=0)
    outcome_counts[col] = counts

outcomes_df = pd.DataFrame(outcome_counts).T
outcomes_df.index = ['T1', 'T2', 'T3', 'T4', 'T5', 'T6']

task_ratings = {}
for idx, col in enumerate(cols, start=1):
    key = f"T{idx}"
    task_ratings[key] = [int(x) for x in task_df[col].dropna().astype(int).tolist()]

payload = {
    'sus_scores': [round(float(x), 2) for x in sus_score.fillna(0)],
    'sus_mean': round(float(sus_score.mean()), 2),
    'demographics': {
        'age_group': age_counts.to_dict(),
        'harmonica_experience': exp_counts.to_dict(),
        'app_experience': app_counts.to_dict()
    },
    'task_outcomes': {
        'labels': list(outcomes_df.index),
        'success': outcomes_df['Success'].astype(int).tolist(),
        'partial': outcomes_df['Partial'].astype(int).tolist(),
        'fail': outcomes_df['Fail'].astype(int).tolist()
    },
    'task_ratings': task_ratings
}

with output.open('w', encoding='utf-8') as f:
    json.dump(payload, f, indent=2)

print('Wrote', output)
