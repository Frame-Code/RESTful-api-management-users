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
    console.log(response.access_token);
    console.log(response.refresh_token);

    d.cookie = `access_token=${response.access_token}; Path=/; SameSite=Lax`;
    d.cookie = `refresh_token=${response.refresh_token}; Path=/; SameSite=Lax`;
    window.location.replace("http://localhost:8080/users.html");
}
