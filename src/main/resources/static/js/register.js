const d = document;

d.querySelector("#btnRegister").addEventListener("click", checkPassword);

function checkPassword() {
    if (d.querySelector("#inputPassword").value.trim() !== d.querySelector("#repeatPassword").value.trim()) {
        alert("Fields password are incorrect");
        return;
    }

    checkFields();
}

function checkFields() {
    if(d.querySelector("#inpFirstName").value == "" ||
       d.querySelector("#impLastName").value == ""||
       d.querySelector("#impEmail").value == "" ||
       d.querySelector("#impPhone").value == "" ||
       d.querySelector("#inputPassword").value == "") {
       alert("Can not be empty field");
       return;
    }

    if(!/^[0-9]+$/.test(d.querySelector("#impPhone").value)) {
        alert("Invalid phone number");
        return;
    }

    if(!/^[a-zA-Z]+$/.test(d.querySelector("#inpFirstName").value) ||
        !/^[a-zA-Z]+$/.test(d.querySelector("#impLastName").value)) {
        alert("Invalid name or lastname fields");
        return;
    }

    register();
}


async function register() {
    const userData = {
        name: d.querySelector("#inpFirstName").value.trim(),
        lastName: d.querySelector("#impLastName").value.trim(),
        email: d.querySelector("#impEmail").value.trim(),
        phone: d.querySelector("#impPhone").value.trim(),
        password: d.querySelector("#inputPassword").value.trim(),
        rolesRegister: {
            roleListName: ["USER"]
        }
    };
    
    const request = await fetch("http://localhost:8080/auth/sign-up", {
        method: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(userData)
    });

    if(!request.ok) {
        if(request.status == 409) {
            alert(`Error: ${await request.text()}`)
        }
        throw new Error(`Http error! status ${request.status}`);
    }

    const response = await request.json();

    d.cookie = `access_token=${response.access_token}; Path=/; SameSite=Lax`;
    d.cookie = `refresh_token=${response.refresh_token}; Path=/; SameSite=Lax`;
    localStorage.setItem("user_name", response.user_name);

    await alert("User created successfully");
    await window.location.replace("http://localhost:8080/index.html");
   
}