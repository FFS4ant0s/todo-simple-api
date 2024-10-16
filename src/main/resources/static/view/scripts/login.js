async function login() {
    let username = document.getElementById("username").value;
    let password = document.getElementById("password").value;

    console.log(username, password);

    const response = await fetch("http://localhost:8080/login", {
        method: "POST",
        headers: new Headers({
            "Content-Type": "application/json; charset=utf8",
            Accept: "application/json",
        }),
        body: JSON.stringify({
            username: username,
            password: password,
        }),
    });

    let key = "Authorization";

    if (response.ok) {
        let token = response.headers.get(key);
        window.localStorage.setItem(key, token); // Armazena o token
        showToast("#okToast");
    } else {
        showToast("#errorToast");
    }

    // Redireciona para a página index após o login
    window.setTimeout(function () {
        window.location = "/todosimple/src/main/resources/static/view/index.html"; // Use um caminho relativo
    }, 2000);
}

// Função para realizar requisições autenticadas
async function fetchWithAuth(url, options = {}) {
    const token = window.localStorage.getItem("Authorization");

    if (token) {
        options.headers = {
            ...options.headers,
            "Authorization": token // Adiciona o token ao cabeçalho
        };
    }

    return await fetch(url, options);
}

function showToast(id) {
    var toastElList = [].slice.call(document.querySelectorAll(id));
    var toastList = toastElList.map(function (toastEl) {
        return new bootstrap.Toast(toastEl);
    });
    toastList.forEach((toast) => toast.show());
}
