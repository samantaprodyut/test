
(function () {
    const searchInput = document.getElementById('studentSearch');
    const table = document.getElementById('studentsTable');

    if (!searchInput || !table) {
        return;
    }

    const tbody = table.querySelector('tbody');

    searchInput.addEventListener('input', function () {
        const q = this.value.trim().toLowerCase();

        Array.from(tbody.rows).forEach(row => {
            const regNo = (row.cells[1].innerText || '').toLowerCase();
            const name = (row.cells[2].innerText || '').toLowerCase();

            if (!q || regNo.includes(q) || name.includes(q)) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });
    });
})();