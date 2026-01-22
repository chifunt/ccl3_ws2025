// Simple table sorting and filtering
document.addEventListener('DOMContentLoaded', () => {
  // Add a filter input above each table-wrap
  document.querySelectorAll('.table-wrap').forEach((wrap, index) => {
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
