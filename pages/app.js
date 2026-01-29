// Simple table sorting and filtering
document.addEventListener('DOMContentLoaded', () => {
  const root = document.documentElement;
  const themeToggle = document.getElementById('themeToggle');
  const prefersLight = window.matchMedia('(prefers-color-scheme: light)');

  const applyTheme = (theme, persist = true) => {
    root.setAttribute('data-theme', theme);
    if (persist) {
      localStorage.setItem('theme', theme);
    }
    if (themeToggle) {
      const isLight = theme === 'light';
      themeToggle.setAttribute('aria-pressed', String(isLight));
      themeToggle.querySelector('.theme-toggle__icon').textContent = isLight ? '◑' : '◐';
      themeToggle.querySelector('.theme-toggle__label').textContent = isLight ? 'Light' : 'Dark';
    }
  };

  const storedTheme = localStorage.getItem('theme');
  if (storedTheme) {
    applyTheme(storedTheme, false);
  } else {
    applyTheme(prefersLight.matches ? 'light' : 'dark', false);
  }

  if (themeToggle) {
    themeToggle.addEventListener('click', () => {
      const current = root.getAttribute('data-theme') || (prefersLight.matches ? 'light' : 'dark');
      applyTheme(current === 'light' ? 'dark' : 'light');
    });
  }

  if (window.ChartBoxPlot) {
    const { BoxPlotController, BoxAndWhiskers, ViolinController, Violin } = window.ChartBoxPlot;
    const components = [BoxPlotController, BoxAndWhiskers, ViolinController, Violin].filter(Boolean);
    if (components.length) {
      Chart.register(...components);
    }
  }

  const getVar = name => getComputedStyle(document.documentElement).getPropertyValue(name).trim();
  const chartColors = {
    text: getVar('--text'),
    muted: getVar('--muted'),
    surface: getVar('--surface'),
    foam: getVar('--foam'),
    iris: getVar('--iris'),
    gold: getVar('--gold'),
    rose: getVar('--rose'),
    pine: getVar('--pine')
  };

  const chartDefaults = {
    color: chartColors.text,
    font: { family: 'Inter, system-ui, -apple-system, Segoe UI, Roboto, Arial' }
  };

  const applyChartTheme = chart => {
    if (!chart) return;
    chart.options.color = chartDefaults.color;
    if (chart.options.scales) {
      Object.values(chart.options.scales).forEach(scale => {
        scale.ticks = scale.ticks || {};
        scale.ticks.color = chartColors.muted;
        scale.grid = scale.grid || {};
        scale.grid.color = 'rgba(64,61,82,0.4)';
      });
    }
    chart.update();
  };

  // JSON-driven charts
  fetch('Dataset/report_data.json')
    .then(res => res.json())
    .then(data => {
      const ageCtx = document.getElementById('ageChart');
      const expCtx = document.getElementById('experienceChart');
      const appCtx = document.getElementById('appExperienceChart');
      const susCtx = document.getElementById('susChart');
      const susBoxCtx = document.getElementById('susBoxplot');
      const outcomesCtx = document.getElementById('outcomesChart');
      const taskBoxCtx = document.getElementById('taskBoxplot');

      if (ageCtx) {
        const labels = Object.keys(data.demographics.age_group);
        const values = Object.values(data.demographics.age_group);
        new Chart(ageCtx, {
          type: 'bar',
          data: { labels, datasets: [{ label: 'Age Group', data: values, backgroundColor: chartColors.foam }] },
          options: {
            ...chartDefaults,
            plugins: { legend: { display: false } },
            scales: { y: { beginAtZero: true } }
          }
        });
      }

      if (expCtx) {
        const labels = Object.keys(data.demographics.harmonica_experience);
        const values = Object.values(data.demographics.harmonica_experience);
        new Chart(expCtx, {
          type: 'bar',
          data: { labels, datasets: [{ label: 'Harmonica Experience', data: values, backgroundColor: chartColors.iris }] },
          options: {
            ...chartDefaults,
            plugins: { legend: { display: false } },
            scales: { y: { beginAtZero: true } }
          }
        });
      }

      if (appCtx) {
        const labels = Object.keys(data.demographics.app_experience);
        const values = Object.values(data.demographics.app_experience);
        new Chart(appCtx, {
          type: 'bar',
          data: { labels, datasets: [{ label: 'App Experience', data: values, backgroundColor: chartColors.gold }] },
          options: {
            ...chartDefaults,
            plugins: { legend: { display: false } },
            scales: { y: { beginAtZero: true } }
          }
        });
      }

      if (susCtx) {
        const labels = data.sus_scores.map((_, idx) => `P${idx + 1}`);
        const meanLine = Array(labels.length).fill(data.sus_mean);
        new Chart(susCtx, {
          data: {
            labels,
            datasets: [
              {
                type: 'bar',
                label: 'SUS Score',
                data: data.sus_scores,
                backgroundColor: chartColors.rose
              },
              {
                type: 'line',
                label: 'Mean',
                data: meanLine,
                borderColor: chartColors.gold,
                borderWidth: 2,
                pointRadius: 0,
                tension: 0
              }
            ]
          },
          options: {
            ...chartDefaults,
            plugins: { legend: { labels: { color: chartColors.muted } } },
            scales: {
              y: {
                min: 80,
                max: 100,
                ticks: { stepSize: 5 }
              }
            }
          }
        });
      }

      if (susBoxCtx) {
        try {
          new Chart(susBoxCtx, {
            type: 'boxplot',
            data: {
              labels: ['SUS'],
              datasets: [{
                label: 'SUS Distribution',
                backgroundColor: 'rgba(196,167,231,0.25)',
                borderColor: chartColors.iris,
                medianColor: chartColors.gold,
                itemRadius: 3,
                outlierColor: chartColors.rose,
                data: [data.sus_scores]
              }]
            },
            options: {
              ...chartDefaults,
              plugins: { legend: { display: false } },
              scales: {
                y: {
                  min: 90,
                  max: 100,
                  ticks: { stepSize: 5 }
                }
              }
            }
          });
        } catch (err) {
          // Skip if boxplot plugin is unavailable
        }
      }

      if (outcomesCtx) {
        new Chart(outcomesCtx, {
          type: 'bar',
          data: {
            labels: data.task_outcomes.labels,
            datasets: [
              { label: 'Success', data: data.task_outcomes.success, backgroundColor: chartColors.foam },
              { label: 'Partial', data: data.task_outcomes.partial, backgroundColor: chartColors.gold },
              { label: 'Fail', data: data.task_outcomes.fail, backgroundColor: chartColors.pine }
            ]
          },
          options: {
            ...chartDefaults,
            plugins: { legend: { labels: { color: chartColors.muted } } },
            scales: { x: { stacked: true }, y: { stacked: true, beginAtZero: true } }
          }
        });
      }

      if (taskBoxCtx) {
        const labels = Object.keys(data.task_ratings);
        const values = labels.map(label => data.task_ratings[label].map(val => val * 20));
        try {
          new Chart(taskBoxCtx, {
            type: 'boxplot',
            data: {
              labels,
              datasets: [{
                label: 'Task Ratings',
                backgroundColor: 'rgba(156,207,216,0.2)',
                borderColor: chartColors.foam,
                medianColor: chartColors.gold,
                itemRadius: 2,
                outlierColor: chartColors.rose,
                data: values
              }]
            },
            options: {
              ...chartDefaults,
              plugins: { legend: { display: false } },
              scales: { y: { min: 70, max: 100, ticks: { stepSize: 5 } } }
            }
          });
        } catch (err) {
          // Skip if boxplot plugin is unavailable
        }
      }
    })
    .catch(() => {
      // If JSON fails to load, keep page usable without charts.
    });

  // Scroll reveal for sections
  const revealTargets = document.querySelectorAll('.section');
  revealTargets.forEach(section => section.classList.add('reveal'));

  const revealObserver = new IntersectionObserver(
    entries => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.classList.add('in-view');
          revealObserver.unobserve(entry.target);
        }
      });
    },
    { threshold: 0.12 }
  );

  revealTargets.forEach(section => revealObserver.observe(section));

  // Active TOC highlight
  const tocLinks = document.querySelectorAll('.toc a[href^="#"]');
  const sectionMap = Array.from(tocLinks)
    .map(link => document.querySelector(link.getAttribute('href')))
    .filter(Boolean);

  const tocObserver = new IntersectionObserver(
    entries => {
      entries.forEach(entry => {
        if (!entry.isIntersecting) return;
        tocLinks.forEach(link => link.classList.remove('is-active'));
        const activeLink = document.querySelector(`.toc a[href="#${entry.target.id}"]`);
        if (activeLink) activeLink.classList.add('is-active');
      });
    },
    { rootMargin: "-20% 0px -70% 0px", threshold: 0.1 }
  );

  sectionMap.forEach(section => tocObserver.observe(section));

  // Add a filter input above each table-wrap
  document.querySelectorAll('.table-wrap').forEach((wrap, index) => {
    if (wrap.classList.contains('no-filter')) {
      return;
    }
    const controls = document.createElement('div');
    controls.className = 'table-controls';

    const input = document.createElement('input');
    input.type = 'search';
    input.placeholder = 'Filter table...';
    input.className = 'table-filter';

    controls.appendChild(input);
    wrap.parentNode.insertBefore(controls, wrap);

    input.addEventListener('input', () => {
      const q = input.value.trim().toLowerCase();
      const rows = wrap.querySelectorAll('tbody tr');
      rows.forEach(row => {
        const text = row.textContent.toLowerCase();
        row.style.display = q === '' || text.includes(q) ? '' : 'none';
      });
    });
  });

  // Add click-to-sort on all tables
  document.querySelectorAll('table').forEach(table => {
    const headers = table.querySelectorAll('th');
    headers.forEach((th, colIndex) => {
      th.style.cursor = 'pointer';
      th.addEventListener('click', () => {
        const tbody = table.tBodies[0];
        if (!tbody) return;
        const rows = Array.from(tbody.querySelectorAll('tr'));
        const isNumeric = rows.every(r => {
          const cell = r.children[colIndex];
          if (!cell) return false;
          return /^[-+]?\d+(\.\d+)?$/.test(cell.textContent.trim());
        });

        const asc = !th.classList.contains('sort-asc');
        headers.forEach(h => h.classList.remove('sort-asc', 'sort-desc'));
        th.classList.add(asc ? 'sort-asc' : 'sort-desc');

        rows.sort((a, b) => {
          const A = (a.children[colIndex] && a.children[colIndex].textContent.trim()) || '';
          const B = (b.children[colIndex] && b.children[colIndex].textContent.trim()) || '';
          if (isNumeric) {
            return asc ? (parseFloat(A) - parseFloat(B)) : (parseFloat(B) - parseFloat(A));
          }
          return asc ? A.localeCompare(B) : B.localeCompare(A);
        });

        // Re-append rows in sorted order
        rows.forEach(r => tbody.appendChild(r));
      });
    });
  });
});
