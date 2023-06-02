function initialize() {
    // initMap();
    // initAutocomplete();
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

let map;
let markers = [];
let rows = [];
let count = 0;

function initMap() {
    let pos = {lat: 45.47539111255855, lng: -73.40919636088881};
    map = new google.maps.Map(document.getElementById("map"), {zoom:12, center:pos});
}

function initAutocomplete() {
    autocomplete = new google.maps.places.Autocomplete(
        document.getElementById("searchBox"),
        {
            fields:['geometry','name']
        });

    autocomplete.addListener('place_changed', onPlaceChanged);
}

function onPlaceChanged() {
    var place = autocomplete.getPlace();
    var searchBox = document.getElementById("searchBox");

    if (!place.geometry) {
        console.log("Invalid")
        searchBox.placeholder = "Please select a valid place";
        searchBox.style.boxShadow = "0 0 10px #666";
        searchBox.style.border = "2px solid rgba(255, 11, 11, 0.65)";
    } else {
        console.log(place.name);
        searchBox.placeholder = "Enter a place";
        searchBox.style.boxShadow = "none";
        searchBox.style.border = "2px solid rgba(123, 123, 123, 0.65)";
        count++;
        addMarker(place.geometry.location);
        addRow(place.name);
    }
    searchBox.value = "";
}

function addMarker(position) {
    console.log("Adding marker");
    const marker = new google.maps.Marker({
        position,
        map,
        label: count.toString()
    });
    markers.push(marker);
}

function addRow(name) {
    let row = $("" +
        "<tr>" +
        "<td id='label'>" + count + "</td>" +
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
        rows[i].find("#label").text(i+1);
    }
}
