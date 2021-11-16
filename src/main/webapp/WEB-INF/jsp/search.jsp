<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html lang="en">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, shrink-to-fit=no">
    <title>Search over Bookman</title>
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
<div class="container" style="margin-bottom: 25px; max-width: 1000px;">
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
                    <li class="nav-item"><a class="nav-link active"
                                            href="${pageContext.request.contextPath}/controller?command=search_page"><i
                            class="fa fa-search"></i>&nbsp;Search</a></li>
                </ul>
            </div>
        </div>
    </nav>
    <div class="row">
        <div class="col">
            <h1 class="text-center">Search over the Bookman</h1>
            <form class="text-center" id="search-form"
                  action="${pageContext.request.contextPath}/controller?command=search"
                  method="post">
                <div class="input-group"><span
                        class="input-group-text">Book title</span><input
                        class="form-control" type="text" name="book-title" id="book-title"
                        value="${requestScope.bookTitle}">
                </div>
                <div class="input-group"><span class="input-group-text">Authors</span><input
                        class="form-control" type="text" name="book-authors" id="book-authors"
                        value="${requestScope.bookAuthors}"
                        pattern="[A-Za-z'-]{3,}( [A-Za-z'-]{3,}){0,3}(, [A-Za-z'-]{3,}( [A-Za-z'-]{3,}){0,3})*"
                        placeholder="John Doe, Ann May, ...">
                </div>
                <div class="input-group"><span class="input-group-text">Genres</span><input
                        class="form-control" type="text" name="book-genres" id="book-genres"
                        value="${requestScope.bookGenres}"
                        pattern="[a-z-]{3,40}( [a-z-]{3,40}){0,3}(, [a-z-]{3,40}( [a-z-]{3,40}){0,3})*"
                        placeholder="detective, novel, science fiction, ...">
                </div>
                <textarea class="form-control" name="book-description" id="book-description"
                          placeholder="Description">${requestScope.bookDescription}</textarea>
                <div class="btn-group d-lg-flex justify-content-lg-center" role="group">
                    <button class="btn btn-primary" type="submit" id="search-button"
                            style="margin-bottom: .5rem;"><i
                            class="fa fa-search"></i> Search
                    </button>
                </div>
            </form>
        </div>
    </div>
    <c:choose>
        <c:when test="${not empty requestScope.books}">
            <div class="row">
                <div class="col">
                    <div class="table-responsive border rounded shadow-lg">
                        <table class="table table-striped">
                            <thead>
                            <tr>
                                <th style="width: 35%;">Title</th>
                                <th style="width: 35%;">Authors</th>
                                <th style="width: 15%">Publish year</th>
                                <th>Available</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="book" items="${requestScope.books}">
                                <tr>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/controller?command=book_page&bookId=${book.id}">${book.title}</a>
                                    </td>
                                    <td>${book.authors}</td>
                                    <td class="publish-year-column">${book.publishYear}</td>
                                    <td class="available-copies-column">${book.remainingAmount}</td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </c:when>
        <c:when test="${requestScope.nothingFound}">
            <div class="row">
                <div class="col">
                    <p style="padding: .5rem"
                       class="lead text-center text-white bg-info border rounded">No books found!
                        Try changing the filter.</p>
                </div>
            </div>
        </c:when>
        <c:when test="${requestScope.noFilter}">
            <div class="row">
                <div class="col">
                    <p style="padding: .5rem"
                       class="lead text-center text-white bg-info border rounded">Filter parameters
                        not specified!</p>
                </div>
            </div>
        </c:when>
    </c:choose>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="js/bs-init.js"></script>
<%--<script src="js/search.js"></script>--%>
</body>

</html>