const d = document;
// Call the dataTables jQuery plugin
$(document).ready(function () {
    loadUsers();
    $('#usersTable').DataTable();
});

function deleteUser(id) {
    alert(id);
}

async function loadUsers() {
    const response = await fetch('http://localhost:8080/api/users', {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    });

    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }

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