const d = document;
// Call the dataTables jQuery plugin
$(document).ready(function () {
    addUserName();
    loadUsers();
    $('#usersTable').DataTable();
});

function addUserName() {
    d.querySelector("#username").innerHTML = localStorage.getItem("user_name");
}

function logout() {
    document.cookie = "access_token=; Path=/; Max-Age=0";
    document.cookie = "refresh_token=; Path=/; Max-Age=0";
}

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
        if(response.status == 401) {
            alert("You can´t delete users");
        }
        throw new Error("Request error, status code: ", response.status);
    }
    
    await alert("User deleted");
    await d.location.reload();
}

async function loadUsers() {
    const response = await fetch('http://localhost:8080/api/users', {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        credential: 'include'
    });

    if(!response.ok) {
        if(response.status == 401) {
            alert("Your are not allowed to view the users list");
        }
        throw new Error("Request error, status code: ", response.status);
    }

    const users = await response.json();

    let usersTable = d.querySelector('#usersTable tbody');
    if(usersTable.textContent.trim() === "") {
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
}
