const EMAILS_CONTAINER = document.getElementById('live-search-result');

READER_EMAIL_INPUT.oninput = function () {
    clearTimeout(searchTimer);
    if (READER_EMAIL_INPUT.value.trim().length < 3) {
        clearChildren(EMAILS_CONTAINER);
        return;
    }
    searchTimer = setTimeout(liveSearch, 300);
}

async function liveSearch() {
    let email = READER_EMAIL_INPUT.value.trim();
    let query = await fetch(`http://localhost:8080/bookman/controller?command=get_reader&email=${email}`);
    if (!query.ok) return;
    let readers = await query.json();

    clearChildren(EMAILS_CONTAINER);

    readers.forEach(reader => {
        let button = document.createElement('button');
        button.className = 'btn btn-outline-primary border rounded-pill';
        button.type = 'button';
        button.onclick = () => insertReaderEmailName(reader[READER_EMAIL_PROPERTY], reader[READER_NAME_PROPERTY]);
        button.textContent = reader[READER_EMAIL_PROPERTY];
        EMAILS_CONTAINER.appendChild(button);
    });
}

function insertReaderEmailName(email, name) {
    READER_EMAIL_INPUT.value = email;
    READER_NAME_INPUT.value = name;
    clearChildren(EMAILS_CONTAINER);
}