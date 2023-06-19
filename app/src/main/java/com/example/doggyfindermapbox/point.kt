package com.example.doggyfindermapbox

class point(var lat: Double, var long: Double, var time: String) {
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    lateinit var timestamp: String


    init {
        this.latitude = lat
        this.longitude = long
        this.timestamp = time
    }

    // Returns the latitude of the point
    fun getLat(): Double {
        return latitude
    }

    // Returns the longitude of the point
    fun getLong(): Double {
        return longitude
    }

    // Returns the timestamp of the point
    fun getTime(): String {
        return timestamp
    }

    // Returns the point as a string
    override fun toString(): String {
        return "Lat: " + latitude + " Long: " + longitude + " Time: " + timestamp
    }

    // set the latitude of the point
    fun setLat(lat: Double) {
        this.latitude = lat
    }

    // set the longitude of the point
    fun setLong(long: Double) {
        this.longitude = long
    }

    // set the timestamp of the point
    fun setTime(time: String) {
        this.timestamp = time
    }


}