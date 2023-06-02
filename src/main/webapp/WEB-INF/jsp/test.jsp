<style>
    #mapContainer{
        width:100%;
        height:50%;
        background-color: #ffdede;
        border-bottom: 7px double black;
    }
    #map {
        width: 100%;
        height: 100%;
    }
    #searchBox{
        position:absolute;
        top:43%;
        left:15px;
        z-index:10;
        border-radius: 15px;
        border:2px solid rgba(184, 184, 184, 0.65);
        padding:5px;
        padding-left:10px;
        width:25%;
        font-size:16px;
    }
    table {
        border: 6px solid #000000;
    }
    td {
        text-align: center;
    }
    th {
        border: 3px solid #ffffff;
    }
    #durationBox {
        width: 50px;
    }
    #deleteButton {
        background-color: darkred;
        border:none;
        border-radius: 10px;
        color: silver;
    }
</style>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <link href="webjars/bootstrap/5.1.3/css/bootstrap.min.css" rel="stylesheet">
    <title>Activity Planner</title>
</head>
<body style="background-color:rgba(43,45,76,0.79);">
    <div id="mapContainer">
        <div id="map"></div>
        <input id="searchBox" placeholder="Enter a place" type="text">
    </div>

    <div class="container-fluid pt-4 px-5">
        <div class="row">
            <div class="col-4">
                HI
            </div>
            <div class="col">
                <h2 style="color: floralwhite; text-align:center">Places</h2>
                <table class="table table-dark">
                    <thead>
                        <tr>
                            <th style="width: 70px; text-align: center">Label</th>
                            <th style="width: 70%; text-align: center">Name</th>
                            <th style="width: 150px; text-align: center">Duration of visit</th>
                            <th style="width:40px"></th>
                        </tr>
                    </thead>
                    <tbody>

                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <script src="js/test.js"></script>
    <script async src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDw1_NBRgD0_BROwJu7LqUdbV9ZYzW5uVQ&libraries=places&callback=initialize"></script>

    <script src="webjars/bootstrap/5.1.3/js/bootstrap.min.js"></script>
    <script src="webjars/jquery/3.6.0/jquery.min.js"></script>
    </body>
</html>