<style>
    #map{
        width:100%;
        height:400px;
    }
</style>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
    <head>
        <link href="webjars/bootstrap/5.1.3/css/bootstrap.min.css" rel="stylesheet">
        <title>Activity Planner</title>
    </head>
    <body>
        <div id="map"></div>

        <div class="container-fluid">
            <div class="row">
                <div class="col-4">
                    <h3>
                    <form method="post" model>New Place</h3>
                    <form:form method="post" modelAttribute="place">
                        <fieldset class="mb-3">
                            <form:label path="name">Name</form:label>
                            <form:input type="text" path="name"/>
                        </fieldset>
                        <fieldset class="mb-3">
                            <form:label path="latitude">Latitude</form:label>
                            <form:input type="text" path="latitude"/>
                        </fieldset>
                        <fieldset class="mb-3">
                            <form:label path="longitude">Longitude</form:label>
                            <form:input type="text" path="longitude"/>
                        </fieldset>
                        <fieldset class="mb-3">
                            <form:label path="visitDuration">Duration of visit</form:label>
                            <form:input type="text" path="visitDuration"/>
                        </fieldset>
                        <form:input type="hidden" path="id"/>

                        <input type="submit" value="Add" class="btn btn-success">
                    </form:form>
                </div>
                <div class="col-lg">
                    <h3>Places</h3>
                    <table class="table">
                        <thead><tr>
                            <th>id</th>
                            <th>Name</th>
                            <th>Latitude</th>
                            <th>Longitude</th>
                            <th>Duration of visit</th>
                            <th></th>
                        </tr></thead>
                        <tbody>
                            <c:forEach items="${placesList}" var="p">
                                <tr>
                                    <td>${p.id}</td>
                                    <td>${p.name}</td>
                                    <td>${p.latitude}</td>
                                    <td>${p.longitude}</td>
                                    <td>${p.visitDuration}</td>
                                    <td><a href="" class="btn btn-warning">Delete</a></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>

        </div>

        <script src="js/test.js"></script>
        <script async src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDw1_NBRgD0_BROwJu7LqUdbV9ZYzW5uVQ&callback=initMap"></script>

        <script src="webjars/bootstrap/5.1.3/js/bootstrap.min.js"></script>
        <script src="webjars/jquery/3.6.0/jquery.min.js"></script>
    </body>
</html>