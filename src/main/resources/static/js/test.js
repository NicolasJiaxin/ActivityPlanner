let map;
let v = 0;
function initMap() {
    let pos = {lat:45.47539111255855,lng:-73.40919636088881};
    map = new google.maps.Map(document.getElementById("map"), {zoom:12, center:pos});
}

function addMarker() {
    v++;
    alert(v);
    let lat = document.getElementById("lat");
    let lng = document.getElementById("lng");
    let pos = {lat:parseFloat(lat.value), lng:parseFloat(lng.value)};
    lat.value = "";
    lng.value = "";
    let marker = new google.maps.Marker({position:pos, map:map});
}