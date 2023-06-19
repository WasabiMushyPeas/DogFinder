package com.example.doggyfindermapbox

import com.example.doggyfindermapbox.point

class dogTrail(var points: ArrayList<point>){
    var trail: ArrayList<point> = ArrayList<point>()
    init {
        this.trail = points
    }

    // Returns the number of points in the trail
    fun getLength(): Int {
        return trail.size
    }

    // Returns the point at the given index
    fun getPoint(index: Int): point {
        return trail[index]
    }

    // Returns the trail as a string
    override fun toString(): String {
        var str: String = ""
        for (i in 0..trail.size-1) {
            str += trail[i].toString() + "\n"
        }
        return str
    }

    // Adds a point to the trail
    fun addPoint(point: point) {
        trail.add(point)
    }

    // Removes a point from the trail
    fun removePoint(index: Int) {
        trail.removeAt(index)
    }


    // Saves the trail to a file
    fun saveTrail() {
        // TODO
    }


}