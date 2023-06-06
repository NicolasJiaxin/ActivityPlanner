<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <link href="webjars/bootstrap/5.1.3/css/bootstrap.min.css" rel="stylesheet">
    <link href="css/map.css" rel="stylesheet">
    <link rel="shortcut icon" type="image/png" th:href="@{/favicon.ico}"/>
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
            <div class="col-4" id="information">
                <h2 class="mb-4">Trip Information</h2>
                <p id="cityInfo" class="mb-3">Current city: </p>
                <p id="homeInfo" class="mb-3">Current stay: </p>
                Number of days
                <select id="numberDays" class="form-select-sm ms-5 mb-3"></select>

                <div id="minimizeOptions" class="mb-3">
                    Minimize:&nbsp&nbsp&nbsp
                    <div class="form-check form-check-inline">
                        <input class="form-check-input" type="radio" name="minimizeOptions" id="timeOption" checked>
                        <label class="form-check-label" for="timeOption">Time</label>
                    </div>
                    <div class="form-check form-check-inline">
                        <input class="form-check-input" type="radio" name="minimizeOptions" id="distanceOption">
                        <label class="form-check-label" for="distanceOption">Distance</label>
                    </div>
                </div>

                <div style="text-align: center" class="mt-1">
                    <input type="submit" class="btn btn-success" value="Submit" id="submitButton">
                </div>
                <div class="mt-3" id="showPlans"  style="display: none">
                    <h5><a id="showPlansButton" href="#plansTables">Show plans</a></h5>
                </div>
            </div>
            <div class="col">
                <h2 style="color: floralwhite; text-align:center">Places</h2>
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
            </div>
        </div>
    </div>

    <div id="plansDiv" class="container m-5" style="display: none">
        <h1 id="plansTables">Trip Plan</h1>
    </div>

    <script src="js/map.js"></script>
    <script async src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDw1_NBRgD0_BROwJu7LqUdbV9ZYzW5uVQ&libraries=places&callback=initialize"></script>

    <script src="webjars/bootstrap/5.1.3/js/bootstrap.min.js"></script>
    <script src="webjars/jquery/3.6.0/jquery.min.js"></script>
    </body>
</html>