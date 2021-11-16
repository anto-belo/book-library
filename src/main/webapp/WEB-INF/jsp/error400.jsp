<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false" %>
<html lang="en">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, shrink-to-fit=no">
    <title>Error 404 :(</title>
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
<div class="container" style="margin-top: 100px;"><i
        class="fa fa-ban d-lg-flex justify-content-lg-center flash animated infinite"
        style="font-size: 150px;color: var(--bs-gray-500);"></i>
    <h1 style="font-size: 80px;text-align: center;">Oops!</h1>
    <h1 style="text-align: center;">It looks like you have a 400 error :(</h1>
    <p class="text-center">Check your request or return to the&nbsp;<a
            href="${pageContext.request.contextPath}/controller?command=main">main page</a></p>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="js/bs-init.js"></script>
</body>

</html>