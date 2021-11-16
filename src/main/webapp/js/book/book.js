const BOOK_ID_PARAMETER = 'bookId';

const BOOK_COVER_URL_PROPERTY = 'coverUrl';
const BOOK_TITLE_PROPERTY = 'title';
const BOOK_AUTHORS_PROPERTY = 'authors';
const BOOK_PUBLISHER_PROPERTY = 'publisher';
const BOOK_PUBLISH_YEAR_PROPERTY = 'publishYear';
const BOOK_GENRES_PROPERTY = 'genres';
const BOOK_PAGE_COUNT_PROPERTY = 'pageCount';
const BOOK_ISBN_PROPERTY = 'isbn';
const BOOK_TOTAL_AMOUNT_PROPERTY = 'totalAmount';
const BOOK_REMAINING_AMOUNT_PROPERTY = 'remainingAmount';
const BOOK_DESCRIPTION_PROPERTY = 'description';
const BOOK_BORROWS_PROPERTY = 'borrows';

const BORROW_ID_PROPERTY = 'id';
const BORROW_READER_NAME_PROPERTY = 'readerName';
const BORROW_READER_EMAIL_PROPERTY = 'readerEmail';
const BORROW_BORROW_DATE_PROPERTY = 'borrowDate';
const BORROW_FOR_MONTHS_PROPERTY = 'forMonths';
const BORROW_DUE_DATE_PROPERTY = 'dueDate';
const BORROW_RETURN_DATE_PROPERTY = 'returnDate';
const BORROW_COMMENT_PROPERTY = 'comment';
const BORROW_STATUS_PROPERTY = 'statusId';

const READER_NAME_PROPERTY = 'name';
const READER_EMAIL_PROPERTY = 'email';

const ERROR_500_PAGE = 'http://localhost:8080/bookman/controller?command=error_500_page';

let bookInfo;
let dbBorrows;
let totalAmount = 1;
let remainingAmount = 1;
let dbDueDates = [];
let nonDbDueDates = [];

let remainingAmountField;

window.addEventListener("DOMContentLoaded", () => {
    loadBookInfo().then(() => {
        remainingAmountField = document.createElement('input');
        remainingAmountField.type = 'hidden';
        remainingAmountField.name = 'book-remaining-amount';
        remainingAmountField.value = remainingAmount;
        FORM_BOOK_EDIT.appendChild(remainingAmountField);
    });
});

async function loadBookInfo() {
    let bookIdParam = getRequestParam(BOOK_ID_PARAMETER);
    if (bookIdParam == null || bookIdParam === '0') return;

    let query = await fetch(`http://localhost:8080/bookman/controller?command=get_book&${BOOK_ID_PARAMETER}=${bookIdParam}`);
    if (!query.ok) {
        location.replace(ERROR_500_PAGE);
        return;
    }

    bookInfo = await query.json();
    dbBorrows = bookInfo[BOOK_BORROWS_PROPERTY];
    totalAmount = bookInfo[BOOK_TOTAL_AMOUNT_PROPERTY];
    remainingAmount = bookInfo[BOOK_REMAINING_AMOUNT_PROPERTY];

    renderPage();
}

function findDBBorrowById(borrowId) {
    for (let i = 0; i < dbBorrows.length; i++) {
        if (dbBorrows[i][BORROW_ID_PROPERTY] === +borrowId) {
            return dbBorrows[i];
        }
    }
    return null;
}

function addDueDate(array, dueDate) {
    if (array.includes(dueDate)) return;
    array.push(dueDate);
    array.sort();
}

function removeDueDate(array, dueDate) {
    let index = array.indexOf(dueDate);
    if (index > -1) {
        array.splice(index, 1);
    }
}
