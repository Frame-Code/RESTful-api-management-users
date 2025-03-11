const d = document;
// Call the dataTables jQuery plugin
$(document).ready(function () {
    init();
    searchUsers();
    $('#usersTable').DataTable();
});

function init() {
    d.querySelector("#username").innerHTML = localStorage.getItem("user_name");

    d.querySelector("#btnSearchUsers").addEventListener("click", searchUser);
    d.querySelector("#btnLogout").addEventListener("click", logout);
}

function logout() {
    document.cookie = "access_token=; Path=/; Max-Age=0";
    document.cookie = "refresh_token=; Path=/; Max-Age=0";
}

function unexpectedError(response_status) {
    alert("Unexpected error!");
    throw new Error(`Http error: ${response_status}`);
}

async function searchUser() {
    let value = d.querySelector("#inpSearch").value;
    const request = await fetch(`http://localhost:8080/api/users/search?value=${value}`, {
        method: 'GET',
        headers: {
            'Content-type': 'application/json'
        }
    });

    if(!request.ok) {
        if(!request.status == 401) {
            unexpectedError(request.status);
        }
        alert("You are not authorize to realize this action");
        return;

    }

    if(request.status == 204) {
        alert(`Users with the parameter ${value} has not founded`);
        return;
    }

    loadUsers(await request.json());
}

async function searchUsers() {
    const response = await fetch('http://localhost:8080/api/users', {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        credential: 'include'
    });

    if(!response.ok) {
        if(!response.status == 401) {
            unexpectedError(request.status);
        }
        alert("Your are not authorize to view the users list");
        return;
    }

    loadUsers(await response.json());
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
        if(!response.status == 401) {
            unexpectedError(request.status);
        }
        alert("You can´t delete users");
        return;
    }
    
    await alert("User deleted");
    await d.location.reload();
}

function loadUsers(usersJson) {
    if(usersJson.length != 0) {
        let usersTable = d.querySelector('#usersTable tbody');
        usersTable.textContent = "";
        for (const user of usersJson) {
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

