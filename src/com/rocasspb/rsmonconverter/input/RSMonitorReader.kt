package com.rocasspb.rsmonconverter.input

import com.rocasspb.rsmonconverter.DataLine
import com.rocasspb.rsmonconverter.logging.InputDataLogger
import com.rocasspb.rsmonconverter.logging.NullDataLogger
import java.io.ByteArrayInputStream
import java.util.*

class RSMonitorReader : InputDataReader<DataLine> {
    companion object {
        private const val LINE_LENGTH = 183

        private const val INDEX_SPEED = 0x17
        private const val INDEX_TEMP_INTAKE = 0x1d
        private const val INDEX_TEMP_COOLANT = 0x22
        private const val INDEX_TEMP_OIL = 0x27
        private const val INDEX_TEMP_GEARBOX = 0x2c
        private const val INDEX_TEMP_CLUTCH = 0x31

        private const val INDEX_THROTTLE = 0x36
        private const val INDEX_BRAKE = 0x42
        private const val INDEX_STEERING = 0x47

        private const val INDEX_RPM = 0x4B

        private const val INDEX_GPS_LAT = 0x5c
        private const val INDEX_GPS_LON = 0x60

        private const val INDEX_TEMP_EXTERNAL = 0x93

        private const val INDEX_GEAR = 0x98

        private const val INDEX_REL_TIME = 0xA9

        private const val MAGIC_NUMBER = 6 //wut? ))
        private const val SPEED_CORRECTION_MAGIC_NUMBER = 122 //wut? ))
    }

    private var dataLogger: InputDataLogger<DataLine> = NullDataLogger()

    override fun readFromBytes(bytes: ByteArrayInputStream): List<DataLine> {
        val lineBytes = ByteArray(LINE_LENGTH)
        val resList = LinkedList<DataLine>()

        //todo this is temporary until we are not counting time in reality. We assume updates in 10Hz
        var prevGpsLat = 0.0
        var prevGpsLon = 0.0

        while (bytes.available() > LINE_LENGTH) {
            bytes.read(lineBytes)

            dataLogger.logData(lineBytes)

            val throttlePercent = shortFromBigEndian(lineBytes, INDEX_THROTTLE) / 10
            val brakePressure =  0.01 * shortFromBigEndian(lineBytes, INDEX_BRAKE)
            var steeringAngle = shortFromBigEndian(lineBytes, INDEX_STEERING) / 10
            if(Math.abs(steeringAngle) == 3276) //sometimes there is 0x0800 instead of 0. Can't figure out, why
                steeringAngle = 0

            val gear = lineBytes[INDEX_GEAR]
            val speed: Double = intFromThreeBytes(lineBytes, INDEX_SPEED).toDouble() / MAGIC_NUMBER / SPEED_CORRECTION_MAGIC_NUMBER

            val tempIntake = shortFromBigEndian(lineBytes, INDEX_TEMP_INTAKE) / 10
            val tempCoolant = shortFromBigEndian(lineBytes, INDEX_TEMP_COOLANT) / 10
            val tempOil = shortFromBigEndian(lineBytes, INDEX_TEMP_OIL) / 10
            val tempGearbox = shortFromBigEndian(lineBytes, INDEX_TEMP_GEARBOX) / 10
            val tempClutch = shortFromBigEndian(lineBytes, INDEX_TEMP_CLUTCH) / 10
            val tempExt = shortFromBigEndian(lineBytes, INDEX_TEMP_EXTERNAL) / 10

            val gpsLon = 0.0000001 * intFromFourBytes(lineBytes, INDEX_GPS_LAT)
            val gpsLat = 0.0000001 * intFromFourBytes(lineBytes, INDEX_GPS_LON)
            //0x65 - elevation?

            val gpsUpdated = !(gpsLat == prevGpsLat && gpsLon == prevGpsLon)
            prevGpsLat = gpsLat
            prevGpsLon = gpsLon

            val rpmFromBytes = intFromThreeBytes(lineBytes, INDEX_RPM)
            val rpm = if(rpmFromBytes != 0) MAGIC_NUMBER * 1000000 / rpmFromBytes else 0

            val relTime  = 0.01 * intFromThreeBytes(lineBytes, INDEX_REL_TIME)

            val dataLine = DataLine(throttlePercent, brakePressure, steeringAngle, gear, speed, rpm,
                    tempOil, tempCoolant, tempGearbox, tempClutch, tempIntake, tempExt, gpsLat, gpsLon,
                    relTime, gpsUpdated)
            resList.add(dataLine)

            dataLogger.logResult(dataLine)
        }

        return resList
    }

    override fun setDataLogger(logger: InputDataLogger<DataLine>) {
        dataLogger = logger
    }

    private fun shortFromBigEndian(bytes : ByteArray, firstIndex : Int)
            = (((bytes[firstIndex + 1].toInt() and 0xFF) shl 8)
            or (bytes[firstIndex].toInt() and 0xFF)).toShort()

    private fun shortFromLittleEndian(bytes : ByteArray, firstIndex : Int)
            = (((bytes[firstIndex].toInt() and 0xFF) shl 8)
            or (bytes[firstIndex + 1].toInt() and 0xFF))

    private fun intFromThreeBytes(bytes : ByteArray, firstIndex : Int) : Int =
            ((bytes[firstIndex].toInt() and 0xFF) shl 16) +
            ((bytes[firstIndex + 1].toInt() and 0xFF) shl 8) +
            (bytes[firstIndex + 2].toInt() and 0xFF)

    private fun intFromFourBytes(bytes : ByteArray, firstIndex : Int) : Int =
            ((bytes[firstIndex].toInt() and 0xFF) shl 24) +
            ((bytes[firstIndex + 1].toInt() and 0xFF) shl 16) +
            ((bytes[firstIndex + 2].toInt() and 0xFF) shl 8) +
            (bytes[firstIndex + 3].toInt() and 0xFF)
}