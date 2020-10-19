let users = [];

function renderUsers() {
  let list = '';
  for(let user of users) {
    let userCard = `
      <div class="user-card">
        <h4>name: ${user.firstname} ${user.lastname}</h4>
        <p>age: ${user.age}</p>
      </div>
    `;

    list += userCard;
  }

  const userList = document.querySelector('#user-list');
  userList.innerHTML = list;
}

async function submitForm(event) {
  event.preventDefault(); // prevents page reload

  const firstNameInput = document.querySelector('#input-firstname');
  const lastNameInput = document.querySelector('#input-lastname');
  const ageInput = document.querySelector('#input-age');
  
  let user = {
    firstname: firstNameInput.value,
    lastname: lastNameInput.value,
    age: +ageInput.value
  };

  let res = await fetch('/rest/users', {
    method: 'POST',
    headers: { 'content-type': 'application/json' },
    body: JSON.stringify(user)
  });
  res = await res.json();
  users.push(res);
  renderUsers();

  // clear form on submit
  firstNameInput.value = '';
  lastNameInput.value = '';
  ageInput.value = '';
}

async function getUsers() {
  let fetchedUsers = await fetch('/rest/users');
  fetchedUsers = await fetchedUsers.json();
  users = fetchedUsers;
  renderUsers();
}