import java.io.ByteArrayInputStream
import java.util.*

class RSMonitorReader : InputDataReader<DataLine> {
    companion object {
        private const val LINE_LENGTH = 139

        private const val INDEX_SPEED = 0x18
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

        private const val INDEX_GEAR = 0x98

        private const val RPM_MAGIC_NUMBER = 1536000000 // this is divided by RPM value for reason unclear to me
        private const val SPEED_MAGIC_NUMBER = 1.4
    }

    private var dataLogger: InputDataLogger = NullDataLogger()

    override fun readFromBytes(bytes: ByteArrayInputStream): List<DataLine> {
        val lineBytes = ByteArray(LINE_LENGTH)
        val resList = LinkedList<DataLine>()
        while (bytes.available() > LINE_LENGTH) {
            bytes.read(lineBytes)

            dataLogger.logData(lineBytes)

            val throttlePercent = shortFromLittleEndian(lineBytes, INDEX_THROTTLE) / 10
            val brakePressure =  0.01 * shortFromLittleEndian(lineBytes, INDEX_BRAKE)
            val steeringAngle = shortFromLittleEndian(lineBytes, INDEX_STEERING) / 10
            val gear = lineBytes[INDEX_GEAR]
            val speed = ((shortFromBigEndian(lineBytes, INDEX_SPEED) * SPEED_MAGIC_NUMBER / 1000).toInt())

            val tempIntake = shortFromLittleEndian(lineBytes, INDEX_TEMP_INTAKE) / 10
            val tempCoolant = shortFromLittleEndian(lineBytes, INDEX_TEMP_COOLANT) / 10
            val tempOil = shortFromLittleEndian(lineBytes, INDEX_TEMP_OIL) / 10
            val tempGearbox = shortFromLittleEndian(lineBytes, INDEX_TEMP_GEARBOX) / 10
            val tempClutch = shortFromLittleEndian(lineBytes, INDEX_TEMP_CLUTCH) / 10

            val gpsLon = 0.0000001 * intFromBytes(lineBytes, INDEX_GPS_LAT)
            val gpsLat = 0.0000001 * intFromBytes(lineBytes, INDEX_GPS_LON)
            //0x65 - elevation?

            //accel.readFromlineBytes(lineBytes)

            val rpm = RPM_MAGIC_NUMBER / intFromBytes(lineBytes, INDEX_RPM)

            resList.add(DataLine(throttlePercent, brakePressure, steeringAngle, gear, speed, rpm,
                    tempOil, tempCoolant, tempGearbox, tempClutch, tempIntake, gpsLat, gpsLon))
        }

        return resList
    }

    override fun setDataLogger(logger: InputDataLogger) {
        dataLogger = logger
    }

    private fun shortFromLittleEndian(bytes : ByteArray, firstIndex : Int)
            = (((bytes[firstIndex + 1].toInt() and 0xFF) shl 8)
            or (bytes[firstIndex].toInt() and 0xFF)).toShort()

    private fun shortFromBigEndian(bytes : ByteArray, firstIndex : Int)
            = (((bytes[firstIndex].toInt() and 0xFF) shl 8)
            or (bytes[firstIndex + 1].toInt() and 0xFF))

    private fun floatFromBytes(bytes : ByteArray, firstIndex : Int) : Float = Float.fromBits(intFromBytes(bytes, firstIndex))

    private fun intFromBytes(bytes : ByteArray, firstIndex : Int) : Int =
            ((bytes[firstIndex].toInt() and 0xFF) shl 24) +
                    ((bytes[firstIndex + 1].toInt() and 0xFF) shl 16) +
                    ((bytes[firstIndex + 2].toInt() and 0xFF) shl 8)
}