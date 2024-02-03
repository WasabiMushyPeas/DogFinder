package com.example.doggyfindermapbox


class dogPath {
    private var path = ArrayList<Array<Double>>()

    // constructor
    init {
        //readFileExternalStorage()
    }


//    private fun writeFileExternalStorage(path: ArrayList<Array<Double>>) {
//        val filename = "path.txt"
//        val file = File(Environment.getExternalStorageDirectory(), filename)
//        val fileOutputStream = FileOutputStream(file)
//        fileOutputStream.write(path.toString().toByteArray())
//        fileOutputStream.close()
//    }

//    private fun readFileExternalStorage() {
//        val filename = "path.txt"
//        val file = File(Environment.getExternalStorageDirectory(), filename)
//        val fileInputStream = file.inputStream()
//        val inputString = fileInputStream.readBytes().toString(Charsets.UTF_8)
//        val inputArray = inputString.split(", ")
//        for (i in 0 until inputArray.size step 2) {
//            path.add(arrayOf(inputArray[i].toDouble(), inputArray[i + 1].toDouble()))
//        }
//        removeDuplicates()
//    }

    fun addLocation(location: Array<Double>) {
        path.add(location)
        removeDuplicates()
        //writeFileExternalStorage(path)
    }

    fun getCurrentDogLocation(): Array<Double> {
        return path[path.size - 1]
    }

    fun setCurrentDogLocation(location: Array<Double>) {
        path[path.size - 1] = location
        removeDuplicates()
        //writeFileExternalStorage(path)
    }

    fun getPath(): ArrayList<Array<Double>> {
        // return the path excluding the current location
        return ArrayList(path.subList(0, path.size - 1))
    }

    private fun removeDuplicates() {
        val set = HashSet<Array<Double>>()
        val newList = ArrayList<Array<Double>>()
        for (arr in path) {
            if (set.add(arr)) {
                newList.add(arr)
            }
        }
        path = newList
    }
}