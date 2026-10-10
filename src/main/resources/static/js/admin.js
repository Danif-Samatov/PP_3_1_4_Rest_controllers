let allRoles = [];

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('newUserForm').addEventListener('submit', onCreate);
    document.getElementById('editForm').addEventListener('submit', onUpdate);
    document.getElementById('deleteForm').addEventListener('submit', onDelete);
    loadAll();
});

async function loadAll() {
    try {
        const [users, roles] = await Promise.all([
            api('/api/admin/users'),
            api('/api/admin/roles')
        ]);
        allRoles = roles;
        if (!document.getElementById('newRoles').options.length) {
            fillRoles(document.getElementById('newRoles'), []);
        }
        renderTable(users);
        setAlert('pageError', '');
    } catch (error) {
        setAlert('pageError', error.message);
    }
}

function renderTable(users) {
    const tbody = document.getElementById('usersBody');
    const table = document.getElementById('usersTable');
    const empty = document.getElementById('emptyUsers');
    tbody.replaceChildren();
    if (!users.length) {
        table.classList.add('d-none');
        empty.classList.remove('d-none');
        return;
    }
    empty.classList.add('d-none');
    table.classList.remove('d-none');
    users.forEach(user => tbody.appendChild(renderRow(user)));
}

function renderRow(user) {
    const row = document.createElement('tr');
    row.append(
        textCell(user.id),
        textCell(user.firstName),
        textCell(user.lastName),
        textCell(user.age),
        textCell(user.email),
        textCell(user.rolesAsString),
        actionCell('Edit', 'btn btn-info', () => openEdit(user)),
        actionCell('Delete', 'btn btn-danger', () => openDelete(user))
    );
    return row;
}

function actionCell(label, className, onClick) {
    const cell = document.createElement('td');
    const button = document.createElement('button');
    button.type = 'button';
    button.className = className;
    button.textContent = label;
    button.addEventListener('click', onClick);
    cell.appendChild(button);
    return cell;
}

function fillRoles(select, selectedIds) {
    const selected = new Set((selectedIds || []).map(String));
    select.replaceChildren();
    allRoles.forEach(role => {
        const option = document.createElement('option');
        option.value = String(role.id);
        option.textContent = role.shortName;
        option.selected = selected.has(option.value);
        select.appendChild(option);
    });
}

function readUserForm(prefix) {
    const roleSelect = document.getElementById(prefix + 'Roles');
    return {
        firstName: document.getElementById(prefix + 'FirstName').value.trim(),
        lastName: document.getElementById(prefix + 'LastName').value.trim(),
        age: Number(document.getElementById(prefix + 'Age').value),
        email: document.getElementById(prefix + 'Email').value.trim(),
        password: document.getElementById(prefix + 'Password').value,
        roleIds: Array.from(roleSelect.selectedOptions).map(option => Number(option.value))
    };
}

function fillUserFields(prefix, user) {
    document.getElementById(prefix + 'Id').value = user.id;
    document.getElementById(prefix + 'FirstName').value = user.firstName;
    document.getElementById(prefix + 'LastName').value = user.lastName;
    document.getElementById(prefix + 'Age').value = user.age;
    document.getElementById(prefix + 'Email').value = user.email;
    fillRoles(document.getElementById(prefix + 'Roles'), user.roleIds);
}

function openEdit(user) {
    setAlert('editError', '');
    fillUserFields('edit', user);
    document.getElementById('editPassword').value = '';
    $('#editModal').modal('show');
}

function openDelete(user) {
    setAlert('deleteError', '');
    fillUserFields('delete', user);
    $('#deleteModal').modal('show');
}

async function onCreate(event) {
    event.preventDefault();
    const form = event.currentTarget;
    if (!form.reportValidity()) {
        return;
    }
    await submitForm(form, 'newUserError', async () => {
        await api('/api/admin/users', {method: 'POST', body: readUserForm('new')});
        form.reset();
        document.getElementById('usersTab').click();
        await loadAll();
    });
}

async function onUpdate(event) {
    event.preventDefault();
    const form = event.currentTarget;
    if (!form.reportValidity()) {
        return;
    }
    const id = document.getElementById('editId').value;
    await submitForm(form, 'editError', async () => {
        await api('/api/admin/users/' + id, {method: 'PUT', body: readUserForm('edit')});
        $('#editModal').modal('hide');
        await loadAll();
    });
}

async function onDelete(event) {
    event.preventDefault();
    const id = document.getElementById('deleteId').value;
    await submitForm(event.currentTarget, 'deleteError', async () => {
        await api('/api/admin/users/' + id, {method: 'DELETE'});
        $('#deleteModal').modal('hide');
        await loadAll();
    });
}

async function submitForm(form, alertId, action) {
    const button = form.querySelector('button[type="submit"]');
    button.disabled = true;
    setAlert(alertId, '');
    try {
        await action();
    } catch (error) {
        setAlert(alertId, error.message);
    } finally {
        button.disabled = false;
    }
}
