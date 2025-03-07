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

    if(!request.ok) {
        if(request.status == 404 || request.status == 401) {
            alert(await request.text());
        }
        throw new Error("Request error, status code: ", request.status);
    }

    const response = await request.json();

    d.cookie = `access_token=${response.access_token}; Path=/; SameSite=Lax`;
    d.cookie = `refresh_token=${response.refresh_token}; Path=/; SameSite=Lax`;
    localStorage.setItem("user_name", response.user_name);
    window.location.replace("http://localhost:8080/index.html");
}
