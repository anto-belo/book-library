const searchForm = document.getElementById('search-form');
const searchButton = document.getElementById('search-button');
const bookTitleInput = document.getElementById('book-title');
const bookAuthorsInput = document.getElementById('book-authors');
const bookGenresInput = document.getElementById('book-genres');
const bookDescriptionTextarea = document.getElementById('book-description');

bookTitleInput.oninput = () => updateSearchButtonState();
bookAuthorsInput.oninput = () => updateSearchButtonState();
bookGenresInput.oninput = () => updateSearchButtonState();
bookDescriptionTextarea.oninput = () => updateSearchButtonState();
searchButton.onclick = () => searchForm.submit();

function updateSearchButtonState() {
    let isAnyFiltersSpecified =
        bookTitleInput.value.trim() !== ''
        || bookAuthorsInput.value.trim() !== ''
        || bookGenresInput.value.trim() !== ''
        || bookDescriptionTextarea.value.trim() !== '';
    searchButton.classList.toggle('disabled', !isAnyFiltersSpecified);
}