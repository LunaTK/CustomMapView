# CustomMapView
Custom map view for Android

# Features
* Rotate
* Zoom
* Scale
* Add Marker
* Marker Detail

# Example
```kotlin
private fun initMapView() {
    val currentLocationLayer = CurrentLocationLayer().apply {
        locationProvider = object: CurrentLocationProvider {
            val loc = Location(1500f, 1500f)
            override fun getLocation(): Location = loc
        }
    }
    val markerLayer = MarkerLayer().apply {
        addMarker(Marker(1600f, 1600f, "Dummy Destination 1"))
        addMarker(Marker(1550f, 1700f, "Dummy Destination 2"))
        onPopupClickListener = {
            logi("Popup touched : $it")
            dismissPopup()
        }
    }

    mapView = binding.mapview
    mapView.setMapImage(R.drawable.map1)

    mapView.addLayer(currentLocationLayer)
    mapView.addLayer(markerLayer)
}
```

# License
[MIT License](LICENSE.md)

This project is based on [onlylemi/MapView](https://github.com/onlylemi/MapView)
