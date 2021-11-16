const IMG_BOOK_COVER = document.getElementById('book-cover');
let H6_AVAILABILITY_LABEL;


const INPUT_BOOK_TITLE = document.getElementById('book-title');
const INPUT_BOOK_AUTHORS = document.getElementById('book-authors');
const INPUT_BOOK_PUBLISHER = document.getElementById('book-publisher');
const INPUT_BOOK_PUBLISH_YEAR = document.getElementById('book-publish-year');
const INPUT_BOOK_GENRES = document.getElementById('book-genres');
const INPUT_BOOK_PAGE_COUNT = document.getElementById('book-page-count');
const INPUT_BOOK_ISBN = document.getElementById('book-isbn');
const INPUT_BOOK_TOTAL_AMOUNT = document.getElementById('book-total-amount');
const TEXTAREA_BOOK_DESCRIPTION = document.getElementById('book-description');

const DIV_BOOK_DATA_COLUMN = document.getElementById('book-data-column');
const DIV_MANAGE_BUTTONS_GROUP = document.getElementById('manage-buttons-group');
const BUTTON_ADD_BORROW = document.getElementById('add-borrow-button');

let borrowsTable = null;

function renderPage() {
    if (bookInfo[BOOK_COVER_URL_PROPERTY] != null) {
        IMG_BOOK_COVER.src = bookInfo[BOOK_COVER_URL_PROPERTY];
    }

    INPUT_BOOK_TITLE.value = bookInfo[BOOK_TITLE_PROPERTY];
    INPUT_BOOK_AUTHORS.value = bookInfo[BOOK_AUTHORS_PROPERTY];
    INPUT_BOOK_PUBLISHER.value = bookInfo[BOOK_PUBLISHER_PROPERTY];
    INPUT_BOOK_PUBLISH_YEAR.value = bookInfo[BOOK_PUBLISH_YEAR_PROPERTY];
    INPUT_BOOK_GENRES.value = bookInfo[BOOK_GENRES_PROPERTY];
    INPUT_BOOK_PAGE_COUNT.value = bookInfo[BOOK_PAGE_COUNT_PROPERTY];
    INPUT_BOOK_ISBN.value = bookInfo[BOOK_ISBN_PROPERTY];
    INPUT_BOOK_TOTAL_AMOUNT.value = bookInfo[BOOK_TOTAL_AMOUNT_PROPERTY];
    TEXTAREA_BOOK_DESCRIPTION.value = bookInfo[BOOK_DESCRIPTION_PROPERTY];

    dbBorrows.forEach(borrow => {
        if (borrow.returnDate === NOT_RETURNED_STATUS_STUB) {
            addDueDate(dbDueDates, borrow.dueDate);
        }
        addBorrowToTable(borrow, true)
    });

    updateAvailabilityInfo();
}

function updateAvailabilityInfo() {
    if (H6_AVAILABILITY_LABEL == null) {
        H6_AVAILABILITY_LABEL = document.createElement('h6');
        H6_AVAILABILITY_LABEL.className = 'rounded';
        H6_AVAILABILITY_LABEL.id = 'availability-label';
        DIV_BOOK_DATA_COLUMN.insertBefore(H6_AVAILABILITY_LABEL, DIV_BOOK_DATA_COLUMN.firstChild);
    }
    if (remainingAmount > 0) {
        H6_AVAILABILITY_LABEL.innerHTML = `<i class="fa fa-check"></i>&nbsp;Available (${remainingAmount} out of ${totalAmount})`;
        H6_AVAILABILITY_LABEL.style.background = 'var(--bs-green)';
        BUTTON_ADD_BORROW.classList.toggle('disabled', false);
    } else {
        H6_AVAILABILITY_LABEL.innerHTML = `<i class="fa fa-ban"></i>&nbsp;Unavailable (expected on ${getDateAvailable()})`;
        H6_AVAILABILITY_LABEL.style.background = 'var(--bs-red)';
        BUTTON_ADD_BORROW.classList.toggle('disabled', true);
    }
}

function getDateAvailable() {
    if (dbDueDates.length !== 0 && nonDbDueDates.length !== 0) {
        return dbDueDates[0] < nonDbDueDates[0] ? dbDueDates[0] : nonDbDueDates[0];
    } else if (dbDueDates.length === 0 && nonDbDueDates.length === 0) {
        return "the next century :)";
    } else if (nonDbDueDates.length === 0) {
        return dbDueDates[0];
    } else if (dbDueDates.length === 0) {
        return nonDbDueDates[0];
    }
}

INPUT_BOOK_TOTAL_AMOUNT.onchange = () => {
    let booksInUse = totalAmount - remainingAmount;
    let newTotalAmount = Math.min(100, +INPUT_BOOK_TOTAL_AMOUNT.value);
    totalAmount = Math.max(newTotalAmount, booksInUse);
    remainingAmount = totalAmount - booksInUse;
    remainingAmountField.value = remainingAmount;
    INPUT_BOOK_TOTAL_AMOUNT.value = totalAmount;
    updateAvailabilityInfo();
}

function renderBorrowsTable() {
    borrowsTable = document.createElement('table');
    borrowsTable.id = 'borrows-table';
    borrowsTable.classList.add('table');

    const tableHeader = borrowsTable.createTHead();
    const headerRow = tableHeader.insertRow();

    const readerNameHeader = document.createElement('th');
    readerNameHeader.style.width = '25%';
    readerNameHeader.textContent = 'Reader name';
    headerRow.appendChild(readerNameHeader);

    const borrowDateHeader = document.createElement('th');
    borrowDateHeader.style.width = '25%';
    borrowDateHeader.textContent = 'Borrow date';
    headerRow.appendChild(borrowDateHeader);

    const dueDateHeader = document.createElement('th');
    dueDateHeader.style.width = '25%';
    dueDateHeader.textContent = 'Due date';
    headerRow.appendChild(dueDateHeader);

    const returnDateHeader = document.createElement('th');
    returnDateHeader.style.width = '25%';
    returnDateHeader.textContent = 'Return date';
    headerRow.appendChild(returnDateHeader);

    borrowsTable.createTBody();

    const tableWrapper = document.createElement('div');
    tableWrapper.classList.add('table-responsive');
    tableWrapper.id = 'borrows-table';
    tableWrapper.appendChild(borrowsTable);
    DIV_BOOK_DATA_COLUMN.insertBefore(tableWrapper, DIV_MANAGE_BUTTONS_GROUP);
}

function addBorrowToTable(borrow, isFromDB) {
    if (borrowsTable == null) {
        renderBorrowsTable();
    }
    const tableBody = borrowsTable.getElementsByTagName('tbody')[0];
    const borrowRow = tableBody.insertRow(0);
    borrowRow.name = borrow[BORROW_ID_PROPERTY];//todo remove???

    const nameCell = borrowRow.insertCell();
    nameCell.classList.add('text-center');

    const nameButton = document.createElement('button');
    nameButton.className = "btn btn-outline-primary borrow-btn";
    nameButton.style.width = '80%';
    nameButton.type = "button";
    nameButton.setAttribute('data-bs-target', '#modal-1');
    nameButton.setAttribute('data-bs-toggle', 'modal');
    if (isFromDB) {
        nameButton.value = borrow[BORROW_ID_PROPERTY];
    } else {
        nameButton.value = JSON.stringify(borrow);
    }
    nameButton.setAttribute('onclick', `fillModal(this.value, ${isFromDB})`);
    nameButton.textContent = borrow[BORROW_READER_NAME_PROPERTY];
    nameCell.appendChild(nameButton);

    const borrowDateCell = borrowRow.insertCell();
    borrowDateCell.classList.add('text-center');
    borrowDateCell.textContent = borrow[BORROW_BORROW_DATE_PROPERTY];

    const dueDateCell = borrowRow.insertCell();
    dueDateCell.classList.add('text-center');
    dueDateCell.textContent = borrow[BORROW_DUE_DATE_PROPERTY];

    const returnDateCell = borrowRow.insertCell();
    returnDateCell.classList.add('text-center');
    returnDateCell.textContent = borrow[BORROW_RETURN_DATE_PROPERTY];
}

function redrawTable() {
    if (borrowsTable == null) {
        renderBorrowsTable();
    } else {
        let tableBody = borrowsTable.getElementsByTagName('tbody')[0];
        clearChildren(tableBody);
    }
    dbBorrows.forEach(borrow => addBorrowToTable(borrow, true));
    let toSaveBorrows = FORM_BOOK_EDIT.querySelectorAll('input[name="borrow"]');
    toSaveBorrows.forEach(borrowInput => {
        let borrow = JSON.parse(borrowInput.value);
        if (!borrow.hasOwnProperty('id')) {
            addBorrowToTable(borrow, false);
        }
    });
}

function previewCover() {
    const file = document.querySelector('input[type=file]').files[0];
    const reader = new FileReader();

    reader.onloadend = function () {
        IMG_BOOK_COVER.src = reader.result;
    }

    if (file) {
        reader.readAsDataURL(file);
    }
}