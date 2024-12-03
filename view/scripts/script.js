const tasksEndpoint = "http://localhost:8080/task/user";

function hideLoader() {
  document.getElementById("loading").style.display = "none";
}

//Para preenxer as tarefas
function show(tasks) {
  let tab = `<thead>
              <th scope="col">#</th>
              <th scope="col">Description</th>
            </thead>`;

  for (let task of tasks) {
    tab += `
            <tr>
                <td scope="row">${task.id}</td>
                <td>${task.description}</td>
            </tr>
        `;
  }

  document.getElementById("tasks").innerHTML = tab;
}
//fim

//
async function getTasks() {
  let key = "Authorization"; //pega authorization e salva no localStorage.
  const response = await fetch(tasksEndpoint, {
    method: "GET",
    headers: new Headers({
      Authorization: localStorage.getItem(key),
    }),
  });

  var data = await response.json();
  console.log(data);
  if (response) hideLoader();
  show(data);
}

document.addEventListener("DOMContentLoaded", function (event) {
  if (!localStorage.getItem("Authorization"))
    window.location = "/view/login.html";
});

getTasks();