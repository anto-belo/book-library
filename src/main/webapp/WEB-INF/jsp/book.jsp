<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html lang="en">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, shrink-to-fit=no">
    <title>book-library</title>
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet"
          href="https://fonts.googleapis.com/css?family=Libre+Baskerville&amp;display=swap">
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.12.0/css/all.css">
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <link rel="stylesheet" href="fonts/fontawesome5-overrides.min.css">
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/animate.css/3.5.2/animate.min.css">
    <link rel="stylesheet" href="css/styles.css">
</head>

<body>
<div class="container" style="margin-bottom: 25px;">
    <div class="modal fade" role="dialog" data-bs-backdrop="static" data-bs-keyboard="false"
         tabindex="-1" id="modal-1">
        <div class="modal-dialog modal-dialog-centered modal-lg" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title" id="mod-title">Edit borrow record</h4>
                </div>
                <div class="modal-body">
                    <div class="input-group"><span
                            class="input-group-text">Reader email</span><input
                            class="form-control" type="email" id="mod-email">
                    </div>
                    <div id="live-search-result"></div>
                    <div class="input-group"><span class="input-group-text">Reader name</span><input
                            class="form-control" type="text" id="mod-name">
                    </div>
                    <div class="input-group" id="mod-gr-borrow-date"><span
                            class="input-group-text">Borrow date</span><input
                            class="form-control" type="text" id="mod-borrow-date" readonly>
                    </div>
                    <div class="input-group"><span class="input-group-text">For months</span><select
                            class="form-select"
                            id="mod-for-months">
                        <option value="1" selected="">1</option>
                        <option value="2">2</option>
                        <option value="3">3</option>
                        <option value="6">6</option>
                        <option value="12">12</option>
                    </select></div>
                    <div class="input-group" id="mod-gr-status"><span class="input-group-text">Status</span><select
                            class="form-select" id="mod-status">
                        <option value="1" selected="">Returned</option>
                        <option value="2">Returned and damaged</option>
                        <option value="3">Lost</option>
                    </select></div>
                    <textarea class="form-control" placeholder="Comment" rows="3"
                              id="mod-comment"></textarea>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-light" type="button" data-bs-dismiss="modal">Discard
                    </button>
                    <button class="btn btn-primary" id="save-borrow-button" type="button"
                            data-bs-dismiss="modal"
                            onclick="saveBorrow()">Save
                    </button>
                </div>
            </div>
        </div>
    </div>
    <nav class="navbar navbar-light navbar-expand-md">
        <div class="container-fluid"><a class="navbar-brand"
                                        href="${pageContext.request.contextPath}/controller?command=main_page"
                                        style="font-family: 'Libre Baskerville', serif;font-size: 30px;"><i
                class="fas fa-book-reader"></i>&nbsp;Bookman</a>
            <button data-bs-toggle="collapse" class="navbar-toggler" data-bs-target="#navcol-1">
                <span class="visually-hidden">Toggle navigation</span><span
                    class="navbar-toggler-icon"></span></button>
            <div class="collapse navbar-collapse" id="navcol-1">
                <ul class="navbar-nav">
                    <li class="nav-item"><a class="nav-link"
                                            href="${pageContext.request.contextPath}/controller?command=main_page"><i
                            class="fa fa-home"></i>&nbsp;Home</a>
                    </li>
                    <li class="nav-item"><a class="nav-link"
                                            href="${pageContext.request.contextPath}/controller?command=search_page"><i
                            class="fa fa-search"></i>&nbsp;Search</a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>
    <form action="${pageContext.request.contextPath}/controller?command=update_book"
          method="post" enctype="multipart/form-data" id="book-edit-form">
        <div class="row">
            <div class="col-lg-3 offset-lg-0 text-center" id="book-cover-column">
                <img id="book-cover" src="img/default_wrapper.png" alt="Book cover">
                <label class="btn btn-primary" style="width: 100%; margin-bottom: .5rem;">
                    <input class="d-none" type="file" accept="image/png, image/jpeg"
                           name="book-cover"
                           onchange="previewCover()"><i
                        class="fa fa-image"></i>&nbsp;Load cover
                </label>
                <button class="btn btn-success" id="add-borrow-button" type="button"
                        onclick="clearModal()" data-bs-target="#modal-1" data-bs-toggle="modal"><i
                        class="fa fa-plus"></i>&nbsp;Add borrow
                </button>
            </div>
            <div class="col" id="book-data-column">
                <div class="input-group"><span class="input-group-text">Title</span><input
                        name="book-title" id="book-title"
                        class="form-control" type="text" required="">
                </div>
                <div class="input-group"><span class="input-group-text">Authors</span><input
                        name="book-authors" id="book-authors"
                        class="form-control" type="text" required="" placeholder="John Doe, Ann May, ..."
                        pattern="[A-Za-z'-]{3,}( [A-Za-z'-]{3,}){0,3}(, [A-Za-z'-]{3,}( [A-Za-z'-]{3,}){0,3})*">
                </div>
                <div class="input-group"><span class="input-group-text">Publisher</span><input
                        name="book-publisher" id="book-publisher"
                        class="form-control" type="text" required=""
                        pattern="[a-zA-z0-9.-]{3, 40}">
                </div>
                <div class="input-group"><span class="input-group-text">Publish year</span><input
                        name="book-publish-year" id="book-publish-year"
                        class="form-control" type="number" required="" min="1900">
                </div>
                <div class="input-group"><span class="input-group-text">Genres</span><input
                        name="book-genres" id="book-genres"
                        class="form-control" type="text" required=""
                        placeholder="detective, novel, ..."
                        pattern="[a-z-]{3,40}( [a-z-]{3,40}){0,3}(, [a-z-]{3,40}( [a-z-]{3,40}){0,3})*">
                </div>
                <div class="input-group"><span class="input-group-text">Page count</span><input
                        name="book-page-count" id="book-page-count"
                        class="form-control" type="number" required="" min="3">
                </div>
                <div class="input-group"><span class="input-group-text">ISBN</span><input
                        name="book-isbn" id="book-isbn"
                        class="form-control" type="text" required=""
                        placeholder="XXX-X-XXXX-XXXX-X"
                        pattern="[0-9]{3}-[0-9]-[0-9]{4}-[0-9]{4}-[0-9]">
                </div>
                <div class="input-group"><span class="input-group-text">Total amount</span><input
                        name="book-total-amount" id="book-total-amount"
                        class="form-control" type="number" required="" min="0" value="1" max="100">
                </div>
                <textarea name="book-description" id="book-description"
                          class="form-control" placeholder="Description" rows="5"></textarea>
                <div class="btn-group d-lg-flex justify-content-lg-center" role="group"
                     id="manage-buttons-group">
                    <a class="btn btn-outline-danger"
                       href="${pageContext.request.contextPath}/controller?command=main_page"
                       role="button"><i class="fa fa-remove"></i>&nbsp;Discard
                    </a>
                    <button id="apply-button" class="btn btn-outline-success" type="submit"><i
                            class="fa fa-check"></i>&nbsp;Apply
                    </button>
                </div>
            </div>
        </div>
    </form>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="js/bs-init.js"></script>
<script src="js/book/book.js"></script>
<script src="js/book/modal.js"></script>
<script src="js/book/util.js"></script>
<script src="js/book/live-search.js"></script>
<script src="js/book/view-controller.js"></script>
</body>

</html>
