const url = "https:localhost:8080/task/user/1"

function hideLoader() {
    document.getElementById("loading").style.display = "nome"
}

function show(task) {
    let tab = `
    <thead>
        <th scope="col">#</th>
        <th scope="col">Description</th>
        <th scope="col">Username</th>
        <th scope="col">User Id</th>
    </thead>`

    for (let task of tasks) {
        tab += `
        <tr >
            <td scope="row"> ${tasks.id}</td>
            <td>${tasks.description}</td>
            <td>${tasks.user.username}</td>
            <td>${tasks.user.id}</td>
        </tr >`
    }

    document.getElementById("tasks").innerHTML = tab;
}

async function getAPI(url) {
    const response = await fetch(url, { method: "GET" });

    var data = await response.json();
    console.log
    if (response)
        hideLoader
    show(data);
}

getAPI(url);