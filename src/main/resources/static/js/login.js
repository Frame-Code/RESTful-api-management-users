const d = document;

d.querySelector("#btnLogin").addEventListener("click", login);

async function login() {
    const data = {
        email: d.querySelector("#exampleInputEmail").value,
        password: d.querySelector("#exampleInputPassword").value
    };

    const request = await fetch("http://localhost:8080/auth/log-in", {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    });

    const response = await request.json();

    localStorage.setItem("access_token", response.access_token);
    localStorage.setItem("refresh_token", response.refresh_token);

    window.location.replace("./users.html");
}
