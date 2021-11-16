window.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("books-table");

    document.getElementById("delete-books-button").addEventListener("click", function () {
        form.submit();
    });
});