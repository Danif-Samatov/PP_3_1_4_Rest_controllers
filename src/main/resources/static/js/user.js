document.addEventListener('DOMContentLoaded', async () => {
    try {
        const user = await api('/api/user');
        document.getElementById('userRow').append(
            textCell(user.id),
            textCell(user.firstName),
            textCell(user.lastName),
            textCell(user.age),
            textCell(user.email),
            textCell(user.rolesAsString)
        );
    } catch (error) {
        setAlert('userError', error.message);
    }
});
