package com.rocasspb.rsmonconverter.output.vbo

class LatLonDSMConverter {
    fun latLonToDSM(latOrLon: Double) : String {
        val value = Math.abs(latOrLon)
        val valDeg : Int = Math.floor(value).toInt()
        val valMin : Int = Math.floor((value - valDeg) * 60).toInt()
        val valSec : Int = (Math.round((value - valDeg - valMin.toDouble() / 60) * 3600 * 1000) / 1000).toInt()

        var result = if(latOrLon < 0) "-" else "+"
        result += valDeg * 60 + valMin
        result += "."
        result += valSec

        return result


    }
}