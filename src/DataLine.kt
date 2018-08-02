import java.io.ByteArrayInputStream

interface ByteReader {
    fun readFromBytes(bytes: ByteArray)
}

data class DataLine(var throttlePercent : Int = 0, var brakePressure : Double = .0,
                    var steeringAngle : Int = 0, var gear: Byte = 0,
                    var speed : Int = 0, var rpm: Int = 0,
                    var tempOil: Int = 0,
                    var tempCoolant: Int = 0, var tempGearbox: Int = 0,
                    var tempClutch: Int = 0, var tempIntake: Int = 0,
                    var gpsLat : Double = 0.0, var gpsLon : Double = 0.0,
                    var accel : AccelerationMatrix = AccelerationMatrix()) : ByteReader {
    override fun readFromBytes(bytes: ByteArray){
        throttlePercent = shortFromLittleEndian(bytes, 0x36) / 10
        brakePressure =  0.01 * shortFromLittleEndian(bytes, 0x42)
        steeringAngle = shortFromLittleEndian(bytes, 0x47) / 10
        gear = bytes[0x98]
        speed = ((shortFromBigEndian(bytes, 0x18) * 1.4 / 1000).toInt())

        tempIntake = shortFromLittleEndian(bytes, 0x1d) / 10
        tempCoolant = shortFromLittleEndian(bytes, 0x22) / 10
        tempOil = shortFromLittleEndian(bytes, 0x27) / 10
        tempGearbox = shortFromLittleEndian(bytes, 0x2c) / 10
        tempClutch = shortFromLittleEndian(bytes, 0x31) / 10

        gpsLon = 0.0000001 * intFromBytes(bytes, 0x5c)
        gpsLat = 0.0000001 * intFromBytes(bytes, 0x60)
        //0x65 - elevation?

        //accel.readFromBytes(bytes)

        rpm = 1536000000 / intFromBytes(bytes, 0x4B)
    }

    
}
        
data class AccelerationMatrix(var accel : FloatArray = FloatArray(4)) : ByteReader{
    override fun readFromBytes(bytes: ByteArray) {
        var index = 0x74

        for (i in 0 until accel.size) {
            accel[i] = floatFromBytes(bytes, index)
            index += 5
        }
    }

    override fun toString() = "" // "Accelerations: " + accel[0] + " /" + accel[1] +  " /" + accel[2] + " /" + accel[3]
}

fun shortFromLittleEndian(bytes : ByteArray, firstIndex : Int)
        = (((bytes[firstIndex + 1].toInt() and 0xFF) shl 8)
        or (bytes[firstIndex].toInt() and 0xFF)).toShort()

fun shortFromBigEndian(bytes : ByteArray, firstIndex : Int)
        = (((bytes[firstIndex].toInt() and 0xFF) shl 8)
        or (bytes[firstIndex + 1].toInt() and 0xFF))

fun floatFromBytes(bytes : ByteArray, firstIndex : Int) : Float = Float.fromBits(intFromBytes(bytes, firstIndex))

fun intFromBytes(bytes : ByteArray, firstIndex : Int) : Int =
        ((bytes[firstIndex].toInt() and 0xFF) shl 24) +
        ((bytes[firstIndex + 1].toInt() and 0xFF) shl 16) +
        ((bytes[firstIndex + 2].toInt() and 0xFF) shl 8)