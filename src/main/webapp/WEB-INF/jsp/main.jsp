<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html lang="en">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, shrink-to-fit=no">
    <title>Bookman Library System</title>
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
<div class="container" style="max-width: 1000px;">
    <nav class="navbar navbar-light navbar-expand-md">
        <div class="container-fluid"><a class="navbar-brand"
                                        href="${pageContext.request.contextPath}/controller?command=main_page&perPage=${requestScope.perPage}"
                                        style="font-family: 'Libre Baskerville', serif;font-size: 30px;"><i
                class="fas fa-book-reader"></i>&nbsp;Bookman</a>
            <button data-bs-toggle="collapse" class="navbar-toggler" data-bs-target="#navcol-1">
                <span class="visually-hidden">Toggle navigation</span><span
                    class="navbar-toggler-icon"></span></button>
            <div class="collapse navbar-collapse" id="navcol-1">
                <ul class="navbar-nav">
                    <li class="nav-item"><a class="nav-link active"
                                            href="${pageContext.request.contextPath}/controller?command=main_page&perPage=${requestScope.perPage}"><i
                            class="fa fa-home"></i>&nbsp;Home</a></li>
                    <li class="nav-item"><a class="nav-link"
                                            href="${pageContext.request.contextPath}/controller?command=search_page"><i
                            class="fa fa-search"></i>&nbsp;Search</a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>
    <div class="row">
        <div class="col">
            <div class="btn-toolbar justify-content-end" id="table-tools">
                <div class="btn-group" role="group">
                    <a class="btn btn-outline-success"
                       href="${pageContext.request.contextPath}/controller?command=book_page"
                       role="button"><i class="fa fa-plus"></i>&nbsp;Add Book
                    </a>
                </div>
                <div id="delete-books-button" class="btn-group" role="group">
                    <button class="btn btn-outline-danger" type="button"><i
                            class="fa fa-trash-o"></i>&nbsp;Delete selected
                    </button>
                </div>
                <div class="btn-group" role="group">
                    <c:choose>
                        <c:when test="${requestScope.availability eq 0}">
                            <a class="btn btn-outline-primary"
                               href="${pageContext.request.contextPath}/controller?command=main_page&page=1&perPage=${requestScope.perPage}&availability=1"
                               role="button"><i
                                    class="fa fa-check-circle"></i>&nbsp;Show available only
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a class="btn btn-outline-primary active"
                               href="${pageContext.request.contextPath}/controller?command=main_page&page=1&perPage=${requestScope.perPage}"
                               role="button"><i
                                    class="fa fa-check-circle"></i>&nbsp;Show available only
                            </a>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="btn-group" role="group">
                    <c:choose>
                        <c:when test="${requestScope.perPage == 20}">
                            <a class="btn btn-outline-primary"
                               href="${pageContext.request.contextPath}/controller?command=main_page&page=1&perPage=10&availability=${requestScope.availability}"
                               role="button"><i
                                    class="fa fa-th-list"></i>&nbsp;10
                            </a>
                            <a class="btn btn-outline-primary active"
                               href="${pageContext.request.contextPath}/controller?command=main_page&page=1&perPage=20&availability=${requestScope.availability}"
                               role="button"><i
                                    class="fa fa-th-list"></i>&nbsp;20
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a class="btn btn-outline-primary active"
                               href="${pageContext.request.contextPath}/controller?command=main_page&page=1&perPage=10&availability=${requestScope.availability}"
                               role="button"><i
                                    class="fa fa-th-list"></i>&nbsp;10
                            </a>
                            <a class="btn btn-outline-primary"
                               href="${pageContext.request.contextPath}/controller?command=main_page&page=1&perPage=20&availability=${requestScope.availability}"
                               role="button"><i
                                    class="fa fa-th-list"></i>&nbsp;20
                            </a>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="btn-group" role="group">
                    <c:choose>
                        <c:when test="${requestScope.currentPage == 1 and requestScope.lastPage == 1}">
                            <a class="btn disabled"
                               href="${pageContext.request.contextPath}/controller?command=main_page&page=1&perPage=${requestScope.perPage}&availability=${requestScope.availability}"
                               role="button"><i class="fa fa-chevron-left"></i>&nbsp;Previous page
                            </a>
                            <a class="btn disabled"
                               href="${pageContext.request.contextPath}/controller?command=main_page&page=1&perPage=${requestScope.perPage}&availability=${requestScope.availability}"
                               role="button"><i class="fa fa-chevron-right"></i>&nbsp;Next page
                            </a>
                        </c:when>
                        <c:when test="${requestScope.currentPage == 1}">
                            <a class="btn disabled"
                               href="${pageContext.request.contextPath}/controller?command=main_page&page=1&perPage=${requestScope.perPage}&availability=${requestScope.availability}"
                               role="button"><i class="fa fa-chevron-left"></i>&nbsp;Previous page
                            </a>
                            <a class="btn"
                               href="${pageContext.request.contextPath}/controller?command=main_page&page=${requestScope.currentPage + 1}&perPage=${requestScope.perPage}&availability=${requestScope.availability}"
                               role="button"><i class="fa fa-chevron-right"></i>&nbsp;Next page
                            </a>
                        </c:when>
                        <c:when test="${requestScope.currentPage == requestScope.lastPage}">
                            <a class="btn"
                               href="${pageContext.request.contextPath}/controller?command=main_page&page=${requestScope.currentPage - 1}&perPage=${requestScope.perPage}&availability=${requestScope.availability}"
                               role="button"><i class="fa fa-chevron-left"></i>&nbsp;Previous page
                            </a>
                            <a class="btn disabled"
                               href="${pageContext.request.contextPath}/controller?command=main_page&page=${requestScope.lastPage}&perPage=${requestScope.perPage}&availability=${requestScope.availability}"
                               role="button"><i class="fa fa-chevron-right"></i>&nbsp;Next page
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a class="btn"
                               href="${pageContext.request.contextPath}/controller?command=main_page&page=${requestScope.currentPage - 1}&perPage=${requestScope.perPage}&availability=${requestScope.availability}"
                               role="button"><i class="fa fa-chevron-left"></i>&nbsp;Previous page
                            </a>
                            <a class="btn"
                               href="${pageContext.request.contextPath}/controller?command=main_page&page=${requestScope.currentPage + 1}&perPage=${requestScope.perPage}&availability=${requestScope.availability}"
                               role="button"><i class="fa fa-chevron-right"></i>&nbsp;Next page
                            </a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col">
            <form id="books-table"
                  action="${pageContext.request.contextPath}/controller?command=delete_books"
                  method="post">
                <div class="table-responsive border rounded shadow-lg"
                     style="border-radius: 1rem!important;margin-bottom: 25px;">
                    <table class="table table-striped">
                        <thead>
                        <tr>
                            <th style="width: 5%;"><i class="fa fa-check"></i></th>
                            <th style="width: 35%;">Title</th>
                            <th style="width: 35%;">Authors</th>
                            <th style="width: 15%">Publish year</th>
                            <th>Available</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="book" items="${requestScope.books}">
                            <tr>
                                <td class="checkbox-column"><label>
                                    <input type="checkbox" name="selectedBook" value="${book.id}">
                                </label></td>
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
            </form>
            <p class="text-center">Page ${requestScope.currentPage}/${requestScope.lastPage}</p>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="js/bs-init.js"></script>
<script src="js/main.js"></script>
</body>

</html>