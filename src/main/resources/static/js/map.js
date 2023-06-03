let map;
let homeMarker;
let markers = [];
let rows = [];
let count = 0;
let mapAutocomplete;
let cityAutocomplete;
let homeAutocomplete;

const bounds = 0.3; // ~About 30 km (each side)
const defaultDuration = 0;

class Place {
    constructor(id, name, lat, lng, visitDuration) {
        this.id = id;
        this.name = name;
        this.latitude = lat;
        this.longitude = lng;
        this.visitDuration = visitDuration;
    }
}

function initialize() {
    setupButtons();
    displayCitySelector();
    initMap();
    initAutocomplete();

    // addRow(1,"1",defaultDuration);
    // addRow(2,"2",defaultDuration);
    // addRow(3,"3",defaultDuration);
    // addRow(4,"4",defaultDuration);
    // addRow(5,"5",defaultDuration);
    // addRow(6,"6",defaultDuration);
    // addRow(7,"7",defaultDuration);
    // addRow(8,"8",defaultDuration);
    // addRow(9,"9",defaultDuration);
    // addRow(10,"10",defaultDuration);
}

function setupButtons() {
    // Add delegate events for delete buttons of each row
    $("tbody").on("click", "#deleteButton", function() {
        let label = $(this).parent().siblings(".label").text();
        console.log(label);
        removePlace(parseInt(label)-1);
    });

    $("#submitButton").click(function(e) {
        let homePlace = new Place(
            0,
            homeMarker.getTitle(),
            homeMarker.getPosition().lat(),
            homeMarker.getPosition.lng(),
            0
        )
        let places = [homePlace];
        for (let i = 0; i < count; i++) {
            let p = new Place(
                i+1,
                rows[i].find(".name").text(),
                markers[i].getPosition().lat(),
                markers[i].getPosition().lng(),
                rows[i].find("#durationBox").val()
            );
            places.push(p);

        }
        console.log(JSON.stringify(places));
        $.ajax({
            type: "POST",
            url: "/compute?days=1",
            data: JSON.stringify(places),
            success: function(data) {
                console.log(data);
            },
            error: function(e) {
                alert("Error " + e);
            },
            contentType: "application/json; charset=utf-8"
        });
    });
}

function displayCitySelector() {
    let lightbox = $(".lightbox");
    lightbox.fadeIn(700);
    lightbox.children("#citySearchBox,h1").each(function() {
        $(this).delay(500).fadeIn(1000);
    });
}

function displayHomeSelector() {
    $(".lightbox").children("#homeSearchBox,h2").each(function() {
        $(this).delay(200).fadeIn(1000);
    });
}

function hideLightBox() {
    $(".lightbox").delay(200).fadeOut(1000);
}

function initMap() {
    let pos = {lat: 45.47539111255855, lng: -73.40919636088881};
    map = new google.maps.Map(document.getElementById("map"));
}

function initAutocomplete() {
    cityAutocomplete = new google.maps.places.Autocomplete(
        document.getElementById("citySearchBox"),
        {
            fields:['geometry','name'],
            types:['(cities)']
        });
    cityAutocomplete.addListener('place_changed', onPlaceChangedCity);

    homeAutocomplete = new google.maps.places.Autocomplete(
        document.getElementById("homeSearchBox"),
        {
            fields:['geometry','name'],
            strictBounds: true
        });
    homeAutocomplete.addListener('place_changed', onPlaceChangedHome);

    mapAutocomplete = new google.maps.places.Autocomplete(
        document.getElementById("mapSearchBox"),
        {
            fields:['geometry','name'],
            strictBounds: true
        });
    mapAutocomplete.addListener('place_changed', onPlaceChangedMap);
}

function onPlaceChangedCity() {
    let place = cityAutocomplete.getPlace();
    let citySearchBox = document.getElementById("citySearchBox");

    if (!place.geometry) {
        console.log("Invalid")
        citySearchBox.placeholder = "Please select a valid city";
        citySearchBox.style.boxShadow = "0 0 10px #666";
        citySearchBox.style.border = "2px solid red";
    } else {
        console.log(place.name);
        citySearchBox.placeholder = "";
        citySearchBox.style.boxShadow = "none";
        citySearchBox.style.border = "2px solid rgba(123, 123, 123, 0.65)";
        let location = place.geometry.location;
        homeAutocomplete.setBounds({
            north: location.lat() + bounds,
            south: location.lat() - bounds,
            east: location.lng() + bounds,
            west: location.lng() - bounds
        })
        displayHomeSelector();
    }
}

function onPlaceChangedHome() {
    let place = homeAutocomplete.getPlace();
    let homeSearchBox = document.getElementById("citySearchBox");

    if (!place.geometry) {
        console.log("Invalid")
        homeSearchBox.placeholder = "Please select a valid place";
        homeSearchBox.style.boxShadow = "0 0 10px #666";
        homeSearchBox.style.border = "2px solid red";
    } else {
        console.log(place.name);
        homeSearchBox.placeholder = "";
        homeSearchBox.style.boxShadow = "none";
        homeSearchBox.style.border = "2px solid rgba(123, 123, 123, 0.65)";
        map.setCenter(place.geometry.location);
        map.setZoom(12);
        homeMarker = new google.maps.Marker({
            position: place.geometry.location,
            map:map,
            title:"Staying at: " + place.name
        });
        let location = place.geometry.location;
        mapAutocomplete.setBounds({
            north: location.lat() + bounds,
            south: location.lat() - bounds,
            east: location.lng() + bounds,
            west: location.lng() - bounds
        })
        hideLightBox();
    }
}

function onPlaceChangedMap() {
    var place = mapAutocomplete.getPlace();
    var mapSearchBox = document.getElementById("mapSearchBox");

    if (!place.geometry) {
        console.log("Invalid")
        mapSearchBox.placeholder = "Please select a valid place";
        mapSearchBox.style.boxShadow = "0 0 10px #666";
        mapSearchBox.style.border = "2px solid red";
    } else {
        console.log(place.name);
        mapSearchBox.placeholder = "Enter a place";
        mapSearchBox.style.boxShadow = "none";
        mapSearchBox.style.border = "2px solid rgba(123, 123, 123, 0.65)";
        count++;
        addMarker(place.geometry.location, place.name, count.toString());
        addRow(count, place.name, defaultDuration);
    }
    mapSearchBox.value = "";
}

function addMarker(position, title, label) {
    console.log("Adding marker");
    const marker = new google.maps.Marker({
        position,
        map,
        title,
        label
    });
    markers.push(marker);
}

function addRow(label, name, duration) {
    let row = $("" +
        "<tr>" +
        "<td class='label'><strong>" + label + "</strong></td>" +
        "<td class='name'>" + name + "</td>" +
        "<td class='duration'><input type='number' min='0' max='240' value=" + duration +" id='durationBox'></td>" +
        "<td class='delete'><input type='button' id='deleteButton' value='✘'</td>" +
        "</tr>");
    $("tbody").append(row);
    rows.push(row);
}

function removePlace(index) {
    count--;
    removeMarker(index);
    removeRow(index);
    reindexMarkers(index);
    reindexTable(index);
}

function removeMarker(index) {
    markers[index].setMap(null);
    markers.splice(index,1);
}

function reindexMarkers(index) {
    for (let i = index; i < count; i++) {
        markers[i].setLabel((i+1).toString());
    }
}

function removeRow(index) {
   rows.splice(index,1)[0].remove();
}

function reindexTable(index) {
    for (let i = index; i < count; i++) {
        rows[i].find(".label").html("<strong>"+ (i+1) + "</strong>");
    }
}