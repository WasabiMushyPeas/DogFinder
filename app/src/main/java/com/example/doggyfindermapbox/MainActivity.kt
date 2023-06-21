package com.example.doggyfindermapbox

import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.locationcomponent.location
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

var mapView: MapView? = null


// Location text view and button
private var locationTextViewUserLat: TextView? = null
private var locationTextViewUserLong: TextView? = null
private var locationTextViewDogLat: TextView? = null
private var locationTextViewDogLong: TextView? = null
private var locationTextViewDistance: TextView? = null
private var locationButton: Button? = null

// Location variables
private var fusedLocationProviderClient: FusedLocationProviderClient? = null
private var currentLocation: Location? = null
private var dogLocation: Location? = null

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize location views and button variables


        findViewById<Button>(R.id.download_button)
            .setOnClickListener {
                onButtonShowPopupWindowClick(findViewById(R.id.download_button))
            }



        mapView = findViewById(R.id.mapView)
        mapView?.getMapboxMap()?.loadStyleUri(
            Style.SATELLITE_STREETS,
            // After the style is loaded, initialize the Location component.
            object : Style.OnStyleLoaded {
                override fun onStyleLoaded(style: Style) {
                    mapView?.location?.updateSettings {
                        enabled = true
                        pulsingEnabled = true
                    }
                }
            }
        )
    }

    private fun onButtonUpdateLocationClick(locationButton: Button) {
        // Check android version
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            // Check if permission to access location is granted
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // If permission not granted, request permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
            } else {
                // If permission granted, get location
                // Check if GPS is enabled
                val locationManager =
                    getSystemService(LOCATION_SERVICE) as LocationManager
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    // Set current location to location variable
                    currentLocation =
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

                    // Update location text views
                    locationTextViewUserLat?.text = currentLocation?.latitude.toString()
                    locationTextViewUserLong?.text = currentLocation?.longitude.toString()

                    var currentLocation =
                        Point.fromLngLat(currentLocation?.longitude!!, currentLocation?.latitude!!)

                    // Set camera position to current location
                    val cameraPosition = CameraOptions.Builder()
                        .center(currentLocation)
                        .zoom(15.0)
                        .build()
                    mapView?.getMapboxMap()?.setCamera(cameraPosition)

//                    // Calculate distance between user and dog
//                    var distance = distance(currentLocation.latitude(), currentLocation.longitude(), dogLocation?.latitude!!, dogLocation?.longitude!!)

//                    // Update distance text view
//                    locationTextViewDistance?.text = (distance.toString() + " meters")
                } else {
                    // Send toast to user to enable GPS
                    Toast.makeText(this, "Please enable GPS", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }


    fun onButtonShowPopupWindowClick(view: View?) {

        // inflate the layout of the popup window
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.popup_window, null)

        // create the popup window
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true // lets taps outside the popup also dismiss it
        val popupWindow = PopupWindow(popupView, width, height, focusable)

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

        // Initialize location views and button variables
        locationTextViewUserLat = popupView.findViewById(R.id.location_text_view_user_lat)
        locationTextViewUserLong = popupView.findViewById(R.id.location_text_view_user_long)
        locationTextViewDogLat = popupView.findViewById(R.id.location_text_view_dog_lat)
        locationTextViewDogLong = popupView.findViewById(R.id.location_text_view_dog_long)
        locationTextViewDistance = popupView.findViewById(R.id.location_text_view_distance)
        locationButton = popupView.findViewById(R.id.location_button)

        // Set location button on click listener
        locationButton?.setOnClickListener {
            onButtonUpdateLocationClick(locationButton!!)
        }


        // dismiss the popup window when touched
        popupView.setOnTouchListener { v, event ->
            popupWindow.dismiss()
            true
        }

    }


    // Distance in meters between two lat/long points
    fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist =
            sin(Math.toRadians(lat1)) * sin(Math.toRadians(lat2)) + cos(
                Math.toRadians(lat1)
            ) * cos(Math.toRadians(lat2)) * cos(Math.toRadians(theta))
        dist = acos(dist)
        dist = Math.toDegrees(dist)
        dist *= 60 * 1.1515
        dist *= 1609.344
        return dist
    }


}