const d = document;

d.querySelector("#btnRegister").addEventListener("click", checkPassword());

function checkPassword() {
    if (!d.querySelector("#inputPassword").value === d.querySelector("#repeatPassword").value) {
        alert("Fields password are incorrect");
        return;
    }

    register();
}

async function register() {
    const userData = {
        name: d.querySelector("#inpFirstName").value,
        lastName: d.querySelector("#impLastName").value,
        email: d.querySelector("#impEmail").value,
        phone: d.querySelector("#impPhone").value,
        password: d.querySelector("#nputPassword").value
    };
    
    const response = await fetch("http://localhost:8080/api/users", {
        method: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(userData)
    });
    
    if(!response.ok) {
        throw new Error(`Http error! status ${response.status}`);
    }
    
    await alert("User created succesfully");
    
}