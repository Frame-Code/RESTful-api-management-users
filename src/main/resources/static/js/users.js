const d = document;
// Call the dataTables jQuery plugin
$(document).ready(function () {
    loadUsers();
    $('#usersTable').DataTable();
});

async function deleteUser(id) {
    if(!confirm('Do yo want to delete the user selected?')) {
        return;
    }
    
    const response = await fetch(`http://localhost:8080/api/users/${id}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        }
    });
    
    if(!response.ok) {
        throw new Error(`Http error! status: ${response.status}`)
    }
    
    await alert("User deleted");
    await d.location.reload();
}

async function loadUsers() {
    const token = localStorage.getItem('access_token');

    const response = await fetch('http://localhost:8080/api/users', {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    });

    const users = await response.json();

    let usersTable = d.querySelector('#usersTable tbody');
    for (const user of users) {
        usersTable.innerHTML += `
        <tr>
          <td>${user.id}</td>
          <td>${user.name + ' ' + user.lastName}</td>
          <td>${user.email}</td>
          <td>${user.phone}</td>
          <td>
            <a href="#" onclick="deleteUser(${user.id})" class="btn btn-danger btn-circle btn-sm">
                <i class="fas fa-trash"></i>
            </a>
          </td>
        </tr>
      `;
    }


}
