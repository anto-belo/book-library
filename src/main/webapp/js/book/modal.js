const MODAL_TITLE = document.getElementById('mod-title');
const SAVE_BORROW_BUTTON = document.getElementById('save-borrow-button');
const FORM_BOOK_EDIT = document.getElementById('book-edit-form');

const BORROW_DATE_INPUT_GROUP = document.getElementById('mod-gr-borrow-date');
const BORROW_STATUS_SELECT_GROUP = document.getElementById('mod-gr-status');

const READER_EMAIL_INPUT = document.getElementById('mod-email');
const READER_NAME_INPUT = document.getElementById('mod-name');
const BORROW_DATE_INPUT = document.getElementById('mod-borrow-date');
const FOR_MONTHS_SELECT = document.getElementById('mod-for-months');
const BORROW_STATUS_SELECT = document.getElementById('mod-status');
const COMMENT_TEXTAREA = document.getElementById('mod-comment');

const NOT_RETURNED_STATUS_STUB = '[Not returned]';
const RETURNED_STATUS = 1;

let searchTimer;
let borrowInEditId;

READER_EMAIL_INPUT.onchange = () => setTimeout(validBorrow, 100);
READER_NAME_INPUT.onchange = () => setTimeout(validBorrow, 100);

function fillModal(borrowData, isFromDb) {
    let borrow = isFromDb ? findDBBorrowById(borrowData) : JSON.parse(borrowData);

    clearChildren(EMAILS_CONTAINER);

    borrowInEditId = borrow[BORROW_ID_PROPERTY];

    MODAL_TITLE.innerHTML = '<i class="fa fa-pencil"></i> Edit borrow record';

    READER_EMAIL_INPUT.value = borrow[BORROW_READER_EMAIL_PROPERTY];
    READER_EMAIL_INPUT.readOnly = true;

    READER_NAME_INPUT.value = borrow[BORROW_READER_NAME_PROPERTY];
    READER_NAME_INPUT.readOnly = true;

    BORROW_DATE_INPUT_GROUP.classList.toggle('d-none', false);
    BORROW_DATE_INPUT.value = borrow[BORROW_BORROW_DATE_PROPERTY];

    FOR_MONTHS_SELECT.value = borrow[BORROW_FOR_MONTHS_PROPERTY];
    FOR_MONTHS_SELECT.setAttribute('disabled', '');

    BORROW_STATUS_SELECT_GROUP.classList.toggle('d-none', !isFromDb);
    let borrowStatus = borrow[BORROW_STATUS_PROPERTY];
    if (borrowStatus != null) {
        BORROW_STATUS_SELECT.value = borrowStatus;
        BORROW_STATUS_SELECT.setAttribute('disabled', '');
    } else {
        BORROW_STATUS_SELECT.removeAttribute('disabled');
    }

    COMMENT_TEXTAREA.value = borrow[BORROW_COMMENT_PROPERTY];
    COMMENT_TEXTAREA.classList.toggle('d-none', COMMENT_TEXTAREA.value === 'undefined'
        || COMMENT_TEXTAREA.value === '');
    COMMENT_TEXTAREA.setAttribute('disabled', '');

    if (borrow[BORROW_STATUS_PROPERTY] != null || borrowInEditId === undefined) {
        SAVE_BORROW_BUTTON.setAttribute('disabled', '');
    } else {
        SAVE_BORROW_BUTTON.removeAttribute('disabled');
    }
}

function clearModal() {
    MODAL_TITLE.innerHTML = '<i class="fa fa-book"></i> Add new borrow record';

    READER_EMAIL_INPUT.value = '';
    READER_EMAIL_INPUT.readOnly = false;
    borderField(READER_EMAIL_INPUT, false);

    READER_NAME_INPUT.value = '';
    READER_NAME_INPUT.readOnly = false;
    borderField(READER_NAME_INPUT, false);

    BORROW_DATE_INPUT_GROUP.classList.toggle('d-none', true);

    FOR_MONTHS_SELECT.selectedIndex = 0;
    FOR_MONTHS_SELECT.removeAttribute('disabled');

    BORROW_STATUS_SELECT_GROUP.classList.toggle('d-none', true);

    COMMENT_TEXTAREA.value = '';
    COMMENT_TEXTAREA.removeAttribute('disabled');

    SAVE_BORROW_BUTTON.setAttribute('disabled', '');
}


function validBorrow() {
    clearChildren(EMAILS_CONTAINER);
    let valid = true;
    SAVE_BORROW_BUTTON.setAttribute('disabled', '');
    if (!validateEmail(READER_EMAIL_INPUT.value.trim())) {
        borderField(READER_EMAIL_INPUT, true);
        valid = false;
    } else {
        borderField(READER_EMAIL_INPUT, false);
    }
    if (!validateName(READER_NAME_INPUT.value.trim())) {
        borderField(READER_NAME_INPUT, true);
        valid = false;
    } else {
        borderField(READER_NAME_INPUT, false);
    }
    if (valid) {
        SAVE_BORROW_BUTTON.removeAttribute('disabled');
    }
    return valid;
}

function validateEmail(email) {
    const re = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(String(email).toLowerCase());
}

function validateName(name) {
    const re = /^[A-Za-z'-]{3,}( [A-Za-z'-]{3,}){0,3}$/gm;
    return re.test(String(name));
}

function borderField(field, border) {
    if (border) {
        field.style.border = '2px solid red';
    } else {
        field.style.border = '';
    }
}

function saveBorrow() {
    if (borrowInEditId != null) {
        let borrow = {
            id: borrowInEditId,
            statusId: +BORROW_STATUS_SELECT.value,
            returnDate: dateNow()
        }

        let borrowInDb = findDBBorrowById(borrowInEditId);
        borrowInDb.statusId = borrow.statusId;
        borrowInDb.returnDate = borrow.returnDate;

        redrawTable();
        addBorrowFieldToForm(borrow);

        if (borrow.statusId === RETURNED_STATUS) {
            remainingAmount++;
        } else {
            totalAmount--;
            INPUT_BOOK_TOTAL_AMOUNT.value = totalAmount;
        }

        removeDueDate(dbDueDates, findDBBorrowById(borrowInEditId).dueDate);

        updateAvailabilityInfo();
        return;
    }

    let forMonthsValue = +FOR_MONTHS_SELECT.value;
    let borrow = {
        readerEmail: READER_EMAIL_INPUT.value,
        readerName: READER_NAME_INPUT.value,
        borrowDate: dateNow(),
        forMonths: forMonthsValue,
        dueDate: dateNow(forMonthsValue),
        returnDate: NOT_RETURNED_STATUS_STUB
    };

    let comment = COMMENT_TEXTAREA.value.trim();
    if (comment !== '') {
        borrow.comment = comment;
    }

    addBorrowToTable(borrow, false);
    addBorrowFieldToForm(borrow);

    remainingAmount--;
    addDueDate(nonDbDueDates, borrow.dueDate);

    updateAvailabilityInfo();
}

function addBorrowFieldToForm(borrow) {
    let borrowFormField = document.createElement('input');
    borrowFormField.type = 'hidden';
    borrowFormField.name = 'borrow';
    borrowFormField.value = JSON.stringify(borrow);
    FORM_BOOK_EDIT.appendChild(borrowFormField);
}
