package com.example.doggyfindermapbox

import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.location.FusedLocationProviderClient
import com.mapbox.bindgen.Value
import com.mapbox.common.NetworkRestriction
import com.mapbox.common.TileDataDomain
import com.mapbox.common.TileRegionLoadOptions
import com.mapbox.common.TileStore
import com.mapbox.common.TileStoreOptions
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.GlyphsRasterizationMode
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.OfflineManager
import com.mapbox.maps.Style
import com.mapbox.maps.StylePackLoadOptions
import com.mapbox.maps.TilesetDescriptorOptions
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.location
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

var mapView: MapView? = null


// Location text view, button, and Edit Text variables
private var locationTextViewUserLat: TextView? = null
private var locationTextViewUserLong: TextView? = null
private var locationTextViewDogLat: TextView? = null
private var locationTextViewDogLong: TextView? = null
private var locationTextViewDistance: TextView? = null
private var inputEditTextZoom: EditText? = null
private var inputEditTextLat: EditText? = null
private var inputEditTextLong: EditText? = null
private var locationButton: Button? = null
private var downloadButton: Button? = null

// Location variables
private var fusedLocationProviderClient: FusedLocationProviderClient? = null
private var currentLocation: Location? = null
private var dogLocation: Location? = null
val annotationApi = mapView?.annotations
val circleAnnotationManager = mapView?.let { annotationApi?.createCircleAnnotationManager(it) }


// offline map variables
private var downloadPoint: Point = Point.fromLngLat(139.769305, 35.682027)
private const val TILE_REGION_ID = "CurrentRegion"
private const val STYLE_PACK_METADATA = "my-sat-style-pack"
private const val TILE_REGION_METADATA = "my-sat-tile-region"


//val stylePackLoadOptions = StylePackLoadOptions.Builder()
//    .glyphsRasterizationMode(GlyphsRasterizationMode.IDEOGRAPHS_RASTERIZED_LOCALLY)
//    .metadata(Value(STYLE_PACK_METADATA))
//    .build()




class MainActivity : AppCompatActivity() {

//    //more offline map variables
//    val offlineManager: OfflineManager = OfflineManager(MapInitOptions.getDefaultResourceOptions(this))
//
//    val tilesetDescriptor = offlineManager.createTilesetDescriptor(
//        TilesetDescriptorOptions.Builder()
//            .styleURI(Style.SATELLITE_STREETS)
//            .minZoom(5)
//            .maxZoom(16)
//            .build()
//    )
//
//    val tileRegionLoadOptions = TileRegionLoadOptions.Builder()
//        .geometry(Point.fromLngLat(-31.952854, 115.857342))
//        .networkRestriction(NetworkRestriction.NONE)
//        .build()
//
//    val tileStore = TileStore.create().also {
//        // Set default access token for the created tile store instance
//        it.setOption(
//            TileStoreOptions.MAPBOX_ACCESS_TOKEN,
//            TileDataDomain.MAPS,
//            Value(getString(R.string.mapbox_access_token))
//        )
//    }


    // Download offline map

//    // Download style pack
//    val stylePackCancelable = offlineManager.loadStylePack(
//        Style.OUTDOORS,
//        // Build Style pack load options
//        stylePackLoadOptions,
//        { progress ->
//            // Handle the download progress
//            // send toast message
//Toast.makeText(
//                this,
//                "Style pack download progress: ${progress}%",
//                Toast.LENGTH_LONG
//            ).show()
//        },
//        { expected ->
//            if (expected.isValue) {
//                expected.value?.let { stylePack ->
//                    // Style pack download finished successfully
//                    // send toast message
//                    Toast.makeText(
//                        this,
//                        "Style pack downloaded successfully",
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//            }
//            expected.error?.let {
//                // Handle errors that occurred during the style pack download.
//                // send toast message
//                Toast.makeText(
//                    this,
//                    "Style pack download failed",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        }
//    )





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set dog location to random lat and long
        dogLocation = Location("")
        dogLocation?.latitude = -31.952854
        dogLocation?.longitude = 115.857342

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
                    locationTextViewDogLat?.text = dogLocation?.latitude.toString()
                    locationTextViewDogLong?.text = dogLocation?.longitude.toString()

                    var currentLocation =
                        Point.fromLngLat(currentLocation?.longitude!!, currentLocation?.latitude!!)

                    // Set camera position to current location
                    val cameraPosition = CameraOptions.Builder()
                        .center(currentLocation)
                        .zoom(15.0)
                        .build()
                    mapView?.getMapboxMap()?.setCamera(cameraPosition)

                    // add a marker at dog location=
                    val circleAnnotationOptions: CircleAnnotationOptions = CircleAnnotationOptions()
                        // Define a geographic coordinate.
                        .withPoint(Point.fromLngLat(dogLocation?.longitude!!, dogLocation?.latitude!!))
                        // Style the circle that will be added to the map.
                        .withCircleRadius(5.0)
                        .withCircleColor("#bd93f9")
                        .withCircleStrokeWidth(1.0)
                        .withCircleStrokeColor("#44475a")
                    // Add the circle to the map.
                    circleAnnotationManager?.create(circleAnnotationOptions)


                    // Calculate distance between user and dog
                    var distance = distance(currentLocation.latitude(), currentLocation.longitude(), dogLocation?.latitude!!, dogLocation?.longitude!!)

                    // Update distance text view
                    locationTextViewDistance?.text = (distance.toString() + " meters")
                } else {
                    // Send toast to user to enable GPS
                    Toast.makeText(this, "Please enable GPS", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    // on button click, download offline map
    private fun onButtonDownloadClick(locationButton: Button) {
        // Check to see if the edit text inputs are empty
        if(inputEditTextZoom?.text?.isEmpty() == true || inputEditTextLat?.text?.isEmpty() == true || inputEditTextLong?.text?.isEmpty() == true){
            // Send toast to user to enter values
            Toast.makeText(this, "Please enter values", Toast.LENGTH_SHORT).show()
        } else {
            // Set zoom, lat and long to edit text values
            var zoom = inputEditTextZoom?.text.toString().toDouble()
            var lat = inputEditTextLat?.text.toString().toDouble()
            var long = inputEditTextLong?.text.toString().toDouble()

            downloadPoint = Point.fromLngLat(long, lat)

//            // Download tile region
//            val tileRegionCancelable = tileStore.loadTileRegion(
//                TILE_REGION_ID,
//                TileRegionLoadOptions.Builder()
//                    .geometry(downloadPoint)
//                    .descriptors(listOf(tilesetDescriptor))
//                    .metadata(Value(TILE_REGION_METADATA))
//                    .acceptExpired(true)
//                    .networkRestriction(NetworkRestriction.NONE)
//                    .build(),
//                { progress ->
//                    // Handle the download progress
//                    // send toast message
//                    Toast.makeText(
//                        this,
//                        "Tile region download progress: ${progress}%",
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//            ) { expected ->
//                if (expected.isValue) {
//                    // Tile region download finishes successfully
//                    // send toast message
//                    Toast.makeText(
//                        this,
//                        "Tile region downloaded successfully",
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//            }

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
        inputEditTextZoom = popupView.findViewById(R.id.zoom_input)
        inputEditTextLat = popupView.findViewById(R.id.lat_input)
        inputEditTextLong = popupView.findViewById(R.id.long_input)
        downloadButton = popupView.findViewById(R.id.download_button)
        locationButton = popupView.findViewById(R.id.location_button)

        // Set location button on click listener
        locationButton?.setOnClickListener {
            onButtonUpdateLocationClick(locationButton!!)
        }

        // Set download button on click listener
        downloadButton?.setOnClickListener{
            onButtonDownloadClick(downloadButton!!)
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