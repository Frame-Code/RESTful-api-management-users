const d = document;

d.querySelector("#btnLogin").addEventListener("click", login);

async function login() {
    const data = {
        email: d.querySelector("#exampleInputEmail").value,
        password: d.querySelector("#exampleInputPassword").value
    };

    const request = await fetch("http://localhost:8080/api/login", {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    });

    const response = await request.text();
    /*
     if(!response.ok) {
     throw new Error(`Http error, status: ${response.status}`);
     }*/

    if (response === "ok") {
        alert("Login succesfully");
        await window.location.replace("./users.html");
    } else {
        alert("User not found");
    }



}
