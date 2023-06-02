let map;
let markers = [];
let rows = [];
let count = 0;
let mapAutocomplete;
let cityAutocomplete;
let homeAutocomplete;

function initialize() {
    displayCitySelector();
    // initMap();
    initAutocomplete();
    addRow("1");
    addRow("2");
    addRow("3");
    addRow("4");
    addRow("5");
    addRow("6");
    addRow("7");
    addRow("8");
    addRow("9");
    addRow("10");
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
            fields:['geometry','name']
        });
    homeAutocomplete.addListener('place_changed', onPlaceChangedHome);

    mapAutocomplete = new google.maps.places.Autocomplete(
        document.getElementById("mapSearchBox"),
        {
            fields:['geometry','name']
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
        addMarker(place.geometry.location, place.name);
        addRow(place.name);
    }
    mapSearchBox.value = "";
}

function addMarker(position, name) {
    console.log("Adding marker");
    const marker = new google.maps.Marker({
        position,
        map,
        title:name,
        label: count.toString()
    });
    markers.push(marker);
}

function addRow(name) {
    let row = $("" +
        "<tr>" +
        "<td id='label'><strong>" + count + "</strong></td>" +
        "<td id='name'>" + name + "</td>" +
        "<td><input type='number' min='0' max='240' value='0' id='durationBox'></td>" +
        "<td><input type='button' id='deleteButton' value='✘'</td>" +
        "</tr>");
    row.find("#deleteButton").click(function() {
        let label = $(this).parent().siblings("#label").text();
        console.log(label);
        removePlace(parseInt(label)-1);
    });
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
        rows[i].find("#label").html("<strong>"+ (i+1) + "</strong>");
    }
}