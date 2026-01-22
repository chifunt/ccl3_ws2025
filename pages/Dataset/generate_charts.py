import pandas as pd
import matplotlib.pyplot as plt
from pathlib import Path

base = Path('/Users/abbey/ccl3_ws2025-1/pages')
data_dir = base / 'Dataset'
img_dir = base / 'images'
img_dir.mkdir(exist_ok=True)

sus_path = data_dir / 'System Usability Scale (SUS) Questionnaire (Responses) - Form responses 1.csv'

sus = pd.read_csv(sus_path)

# Compute SUS scores
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
sus = sus.assign(SUS_Score=sus_score)

# Demographics
age_counts = sus['What is your age group'].value_counts().sort_index()
exp_counts = sus['What is your experience with the harmonica?'].value_counts().sort_index()
app_counts = sus['Have you used music learning or tab-based apps before?'].value_counts().sort_index()

# Task ratings
cols = [
    'Find a tab titled Amazing Grace and open it.',
    'Show only Medium difficulty tabs and sort them by newest.',
    'Mark any tab as a favourite and show only your favourite tabs.',
    'Create a new tab with a title, key, difficulty, tags, and at least two lines of notes. Save it.',
    'Edit the tab you created, then try to leave without saving.',
    'Start practice mode and move between different lines.'
]

task_df = sus[cols].apply(pd.to_numeric, errors='coerce')

# Map ratings to outcome buckets
# 5 = Success, 4 = Partial, <=3 = Fail
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

# Plot settings
bg = '#191724'
surface = '#1f1d2e'
text = '#e0def4'
muted = '#908caa'
accent = '#9ccfd8'
accent2 = '#c4a7e7'
gold = '#f6c177'
rose = '#ebbcba'
pine = '#31748f'

plt.rcParams.update({
    'font.size': 11,
    'text.color': text,
    'axes.labelcolor': text,
    'axes.edgecolor': muted,
    'xtick.color': muted,
    'ytick.color': muted,
    'axes.titleweight': 'bold',
})

# Demographics chart
fig, axes = plt.subplots(3, 1, figsize=(8, 10), constrained_layout=True)
fig.patch.set_facecolor(bg)

for ax in axes:
    ax.set_facecolor(surface)
    for spine in ax.spines.values():
        spine.set_color(muted)
    ax.grid(axis='y', color='#403d52', alpha=0.4, linestyle='--', linewidth=0.6)

axes[0].bar(age_counts.index, age_counts.values, color=accent)
axes[0].set_title('Age Group')
axes[0].set_ylabel('Count')

axes[1].bar(exp_counts.index, exp_counts.values, color=accent2)
axes[1].set_title('Harmonica Experience')
axes[1].set_ylabel('Count')

axes[2].bar(app_counts.index, app_counts.values, color=gold)
axes[2].set_title('App Experience')
axes[2].set_ylabel('Count')

for ax in axes:
    ax.tick_params(axis='x', labelrotation=15)

fig.savefig(img_dir / 'demographics.png', dpi=200)
plt.close(fig)

# SUS scores chart
fig, ax = plt.subplots(figsize=(8, 4.5), constrained_layout=True)
fig.patch.set_facecolor(bg)
ax.set_facecolor(surface)
ax.grid(axis='y', color='#403d52', alpha=0.4, linestyle='--', linewidth=0.6)

scores = sus['SUS_Score']
ax.bar(range(1, len(scores) + 1), scores, color=rose)
mean_score = scores.mean()
ax.axhline(mean_score, color=gold, linewidth=2, linestyle='-')
ax.text(len(scores) + 0.2, mean_score, f'Mean {mean_score:.2f}', color=gold, va='center')

ax.set_title('SUS Scores per Participant')
ax.set_xlabel('Participant')
ax.set_ylabel('SUS Score (0–100)')

fig.savefig(img_dir / 'sus_scores.png', dpi=200)
plt.close(fig)

# Task outcomes chart
fig, ax = plt.subplots(figsize=(8, 4.8), constrained_layout=True)
fig.patch.set_facecolor(bg)
ax.set_facecolor(surface)
ax.grid(axis='y', color='#403d52', alpha=0.4, linestyle='--', linewidth=0.6)

x = range(len(outcomes_df.index))
ax.bar(x, outcomes_df['Success'], label='Success', color=accent)
ax.bar(x, outcomes_df['Partial'], bottom=outcomes_df['Success'], label='Partial', color=gold)
ax.bar(x, outcomes_df['Fail'], bottom=outcomes_df['Success'] + outcomes_df['Partial'], label='Fail', color=pine)

ax.set_xticks(list(x))
ax.set_xticklabels(outcomes_df.index)
ax.set_title('Task Outcomes (Derived from Task Ratings)')
ax.set_ylabel('Count')
ax.legend(frameon=False, labelcolor=text)

fig.savefig(img_dir / 'task_outcomes.png', dpi=200)
plt.close(fig)

print('Charts written to', img_dir)
