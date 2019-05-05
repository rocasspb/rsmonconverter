package com.rocasspb.rsmonconverter.input

import com.rocasspb.rsmonconverter.field.*
import com.rocasspb.rsmonconverter.logging.InputDataLogger
import com.rocasspb.rsmonconverter.logging.NullDataLogger
import java.io.ByteArrayInputStream
import java.util.*
import kotlin.collections.HashMap

class RSMonitorReader : InputDataReader<Map<FieldEnum, Field<Any>>> {
    companion object {
        private const val LINE_LENGTH = 183

        private const val INDEX_LATERAL = 0x11
        private const val INDEX_LONGITUDAL = 0x13

        private const val INDEX_SPEED = 0x17
        private const val INDEX_TEMP_INTAKE = 0x1d
        private const val INDEX_TEMP_COOLANT = 0x22
        private const val INDEX_TEMP_OIL = 0x27
        private const val INDEX_TEMP_GEARBOX = 0x2c
        private const val INDEX_TEMP_CLUTCH = 0x31

        private const val INDEX_THROTTLE = 0x36
        private const val INDEX_BOOST = 0x3B
        private const val INDEX_BRAKE = 0x42
        private const val INDEX_STEERING = 0x47
        private const val INDEX_TORQUE = 0x50
        private const val INDEX_POWER = 0x58

        private const val INDEX_RPM = 0x4B

        private const val INDEX_GPS_LAT = 0x5c
        private const val INDEX_GPS_LON = 0x60

        private const val INDEX_WHEEL_RR = 0x74
        private const val INDEX_WHEEL_RL = 0x79
        private const val INDEX_WHEEL_FR = 0x7E
        private const val INDEX_WHEEL_FL = 0x83

        private const val INDEX_TEMP_EXTERNAL = 0x93

        private const val INDEX_GEAR = 0x98

        private const val INDEX_REL_TIME = 0xA9

        private const val MAGIC_NUMBER = 6 //wut? ))
        private const val MAGIC_NUMBER_ROTATION = 360
        private const val MAGIC_NUMBER_FF = 256
        private const val MAGIC_NUMBER_MILLION = 1000000
        private const val SPEED_CORRECTION_MAGIC_NUMBER = 122 //wut? ))
    }

    private var dataLogger: InputDataLogger<Map<FieldEnum, Field<Any>>> = NullDataLogger()

    override fun readFromBytes(bytes: ByteArrayInputStream): List<Map<FieldEnum, Field<Any>>> {
        val lineBytes = ByteArray(LINE_LENGTH)
        val resList = LinkedList<Map<FieldEnum, Field<Any>>>()

        //todo this is temporary until we are not counting time in reality. We assume updates in 10Hz
        var prevGpsLat = 0.0
        var prevGpsLon = 0.0

        while (bytes.available() >= LINE_LENGTH) {
            bytes.read(lineBytes)

            dataLogger.logData(lineBytes)

            val map = HashMap<FieldEnum, Field<Any>>()

            map[FieldEnum.OBD_UPD] = BooleanField(true)
            map[FieldEnum.OBD_THROTTLE_PERCENT] =
                    IntField(shortFromBigEndian(lineBytes, INDEX_THROTTLE) / 10)
            map[FieldEnum.OBD_BRAKE_PRESSURE] =
                    DoubleField(0.01 * shortFromBigEndian(lineBytes, INDEX_BRAKE))

            var steeringAngle = shortFromBigEndian(lineBytes, INDEX_STEERING) / 10
            if (Math.abs(steeringAngle) == 3276) //sometimes there is 0x0800 instead of 0. Can't figure out, why
                steeringAngle = 0

            map[FieldEnum.OBD_STEERING_ANGLE] = IntField(steeringAngle)

            map[FieldEnum.OBD_GEAR] = ByteField(lineBytes[INDEX_GEAR])
            map[FieldEnum.OBD_SPEED] = DoubleField(
                    intFromThreeBytes(lineBytes, INDEX_SPEED).toDouble() / MAGIC_NUMBER / SPEED_CORRECTION_MAGIC_NUMBER)

            map[FieldEnum.OBD_TEMP_INTAKE] = IntField(shortFromBigEndian(lineBytes, INDEX_TEMP_INTAKE) / 10)
            map[FieldEnum.OBD_TEMP_COOLANT] = IntField(shortFromBigEndian(lineBytes, INDEX_TEMP_COOLANT) / 10)
            map[FieldEnum.OBD_TEMP_OIL] = IntField(shortFromBigEndian(lineBytes, INDEX_TEMP_OIL) / 10)
            map[FieldEnum.OBD_TEMP_GEARBOX] = IntField(shortFromBigEndian(lineBytes, INDEX_TEMP_GEARBOX) / 10)
            map[FieldEnum.OBD_TEMP_CLUTCH] = IntField(shortFromBigEndian(lineBytes, INDEX_TEMP_CLUTCH) / 10)
            map[FieldEnum.OBD_TEMP_EXT] = IntField(shortFromBigEndian(lineBytes, INDEX_TEMP_EXTERNAL) / 10)

            val gpsLon = 0.0000001 * intFromFourBytes(lineBytes, INDEX_GPS_LAT)
            val gpsLat = 0.0000001 * intFromFourBytes(lineBytes, INDEX_GPS_LON)
            //0x65 - elevation?

            val gpsUpdated = !(gpsLat == prevGpsLat && gpsLon == prevGpsLon)
            prevGpsLat = gpsLat
            prevGpsLon = gpsLon
            map[FieldEnum.GPS_LON] = LatLonField(gpsLon)
            map[FieldEnum.GPS_LAT] = LatLonField(gpsLat)
            map[FieldEnum.GPS_UPD] = BooleanField(gpsUpdated)

            val rpmFromBytes = intFromThreeBytes(lineBytes, INDEX_RPM)
            val rpm = if (rpmFromBytes != 0) MAGIC_NUMBER * MAGIC_NUMBER_MILLION / rpmFromBytes else 0
            map[FieldEnum.OBD_RPM] = IntField(rpm)

            val rr = readWheel(intFromThreeBytes(lineBytes, INDEX_WHEEL_RR))
            val rl = readWheel(intFromThreeBytes(lineBytes, INDEX_WHEEL_RL))
            val fr = readWheel(intFromThreeBytes(lineBytes, INDEX_WHEEL_FR))
            val fl = readWheel(intFromThreeBytes(lineBytes, INDEX_WHEEL_FL))

            val avg = arrayOf(rr, rl, fr, fl).average().toInt()

            map[FieldEnum.OBD_WHEEL_RR] = IntField(rr - avg)
            map[FieldEnum.OBD_WHEEL_RL] = IntField(rl - avg)
            map[FieldEnum.OBD_WHEEL_FR] = IntField(fr - avg)
            map[FieldEnum.OBD_WHEEL_FL] = IntField(fl - avg)

            map[FieldEnum.ACCEL_LAT] = DoubleField(readAccel(lineBytes, INDEX_LATERAL))
            map[FieldEnum.ACCEL_LON] = DoubleField(-readAccel(lineBytes, INDEX_LONGITUDAL))

            map[FieldEnum.REL_TIME] = DoubleField(0.01 * intFromThreeBytes(lineBytes, INDEX_REL_TIME))

            map[FieldEnum.OBD_BOOST] = IntField(shortFromLittleEndian(lineBytes, INDEX_BOOST) * 5)
            map[FieldEnum.OBD_TORQUE] = IntField(shortFromLittleEndian(lineBytes, INDEX_TORQUE))
            map[FieldEnum.OBD_POWER] = IntField(shortFromLittleEndian(lineBytes, INDEX_POWER))

            resList.add(map)

            dataLogger.logResult(map)
        }

        return resList
    }

    override fun setDataLogger(logger: InputDataLogger<Map<FieldEnum, Field<Any>>>) {
        dataLogger = logger
    }

    fun readAccel(lineBytes: ByteArray, index: Int): Double {
        val sign = if(lineBytes[index].toInt() and 0x80 == 0) -1 else 1
        val res = (((lineBytes[index].toInt() and 0x7F) shl 8)
            or (lineBytes[index + 1].toInt() and 0xFF))
        return res.toDouble() * sign / MAGIC_NUMBER_FF
    }

    private fun readWheel(readValue: Int): Int
            = if (readValue == 0) 0 else MAGIC_NUMBER_ROTATION * MAGIC_NUMBER_MILLION / readValue

    private fun shortFromBigEndian(bytes: ByteArray, firstIndex: Int)
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