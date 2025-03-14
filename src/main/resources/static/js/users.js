const d = document;
// Call the dataTables jQuery plugin
$(document).ready(function () {
    init();
    searchUsers();
    $('#usersTable').DataTable();
    $("#editUserModal").modal("hide");
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

async function editUser(id) {
    d.querySelector("#editUserModal").style.display = 'flex';
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

async function getInfoUser(id) {
    const request = await fetch(`http://localhost:8080/api/users/${id}`, {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-type': 'application/json'
        }
    });

    if(!request.ok) {
        alert("Error loading data of the user");
        console.log("Http error");
    }

    const response = await request.json();

    d.querySelector("#inpFirstName").value = response.name;
    d.querySelector("#impLastName").value = response.lastName;
    d.querySelector("#impEmail").value = response.email;
    d.querySelector("#impPhone").value = response.phone;

    d.querySelector("#impUser").checked = false;
    d.querySelector("#impAdmin").checked = false;
    d.querySelector("#impDeveloper").checked = false;
    d.querySelector("#impInvited").checked = false;

    for(const role of response.roles) {
        if(role == "ADMIN") {
            d.querySelector("#impAdmin").checked = true;
        }

        if(role == "DEVELOPER") {
           d.querySelector("#impDeveloper").checked = true;
        }

        if(role == "USER") {
           d.querySelector("#impUser").checked = true;
        }

        if(role == "INVITED") {
           d.querySelector("#impInvited").checked = true;
        }
    }


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
                <a href="#editUserModal" onclick="getInfoUser(${user.id})" class="btn btn-info btn-circle btn-sm" data-toggle="modal" data-target="#editUserModal">
                    <i class="fa-solid fa-user-pen"></i>
                </a>
              </td>
            </tr>
          `;
        }
    }
}

