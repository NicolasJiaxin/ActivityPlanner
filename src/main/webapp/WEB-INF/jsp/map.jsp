<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <link href="webjars/bootstrap/5.1.3/css/bootstrap.min.css" rel="stylesheet">
    <link href="css/map.css" rel="stylesheet">
    <title>Activity Planner</title>
</head>
<body style="background-color:rgba(43,45,76,0.79);">
    <div class="lightbox">
        <h1 style="display: none; z-index: 30" class="mb-4">Enter your city</h1>
        <input id="citySearchBox" type="text" style="display: none" class="searchBox">
        <h2 style="display: none; z-index: 30" class="mt-5 mb-4">Enter where you will stay</h2>
        <input id="homeSearchBox" type="text" style="display: none" class="searchBox">
    </div>

    <div id="mapContainer">
        <div id="map"></div>
        <input id="mapSearchBox" placeholder="Enter a place" type="text" class="searchBox">
    </div>

    <div class="container-fluid pt-4 px-5">
        <div class="row">
            <div class="col-4">
                HI
            </div>
            <div class="col">
                <h2 style="color: floralwhite; text-align:center">Places</h2>
<%--                <div class="table-wrapper">--%>
                <table class="table table-dark" id="placesTable">
                    <thead>
                        <tr>
                            <th class="label">Label</th>
                            <th class="name">Name</th>
                            <th class="duration">Duration of visit</th>
                            <th class="delete"></th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                        </tr>
                    </tbody>
                </table>
<%--                </div>--%>
            </div>
        </div>
    </div>

    <script src="js/map.js"></script>
    <script async src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDw1_NBRgD0_BROwJu7LqUdbV9ZYzW5uVQ&libraries=places&callback=initialize"></script>

    <script src="webjars/bootstrap/5.1.3/js/bootstrap.min.js"></script>
    <script src="webjars/jquery/3.6.0/jquery.min.js"></script>
    </body>
</html>