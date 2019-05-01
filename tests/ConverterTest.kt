import com.rocasspb.rsmonconverter.DataLine
import com.rocasspb.rsmonconverter.field.*
import com.rocasspb.rsmonconverter.formatOutputName
import com.rocasspb.rsmonconverter.input.RSMonitorReader
import com.rocasspb.rsmonconverter.output.RaceRenderCSVDataWriter
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import kotlin.test.assertEquals

class ConverterTest {
    @Test
    fun testOutFileName() {
        assertEquals( "abc.csv", formatOutputName("abc.de"))
        assertEquals( "abd.csv", formatOutputName("abd."))
        assertEquals( "2017_08_03_08_57_56_50.06676_04.29639_log.csv",
                formatOutputName("2017_08_03_08_57_56_50.06676_04.29639_log.run"))
    }

    @Test
    fun testDataReader() {
        val bytes : Array<Byte> = arrayOf(-104, 29, 0, 0, -53, 0, 0, 0, 9, 0, 0, 0, 9, 63, 1, 64, 8, 0, 0, 0, 0, 8, 64, 0, 0, 0, 64, 72, 6, 0, 0, 78, 72, 8, 0, 0, 80, 72, 9, 0, 0, 81, 72, 10, 0, 0, 82, 72, 25, 88, 2, -69, 74, 1, 0, 0, 75, 94, 5, 0, 0, 0, 99, 94, 2, 0, 0, 0, 96, 93, 3, 0, 0, 96, 18, 0, 0, 0, 18, 26, 0, 0, 26, 24, 0, 0, 24, 23, 0, 0, 23, 10, 3, -114, -90, 91, 30, 17, 20, 17, 0, 0, 31, 64, 79, 57, 127, -1, -1, -1, 0, 0, 31, 64, 20, 14, 0, 0, 0, 14, 15, 0, 0, 0, 15, 16, 0, 0, 0, 16, 17, 0, 0, 0, 17, 78, 59, -102, -54, 0, -19, 79, -128, 0, -49, 72, 1, 0, 0, 73, 27, 0, 0, 27, 25, 0, 0, 25, 55, 59, 24, 7, 22, 4, 7, -29, 0, -107, 9, 0, 0, 10, 19, 1, 0, 0, 0, 0, 0, 0, 0, 1, 6)
        val byteArray = ByteArray(bytes.size, init = { i -> bytes[i] })
        val expectedDataLine = DataLine(throttlePercent=0, brakePressure=0.0, steeringAngle=0, gear=0, speed=0.0, rpm=0, tempOil=0, tempCoolant=0, tempGearbox=0, tempClutch=60, tempIntake=0, tempExternal=0, gpsLat=50.4435729, gpsLon=5.9680347, wheelRR=0, wheelRL=0, wheelFR=0, wheelFL=0, accelLat=-0.0, accelLon=0.0, boost=0, power=0, torque=0, relativeTime=0.1, gpsUpdate=true)

        val lines = RSMonitorReader().readFromBytes(ByteArrayInputStream(byteArray))
        assertEquals(1, lines.size)
        with(lines[0]) {
            assertEquals(expectedDataLine.throttlePercent, this[FieldEnum.OBD_THROTTLE_PERCENT]!!.value)
            assertEquals(expectedDataLine.accelLat, this[FieldEnum.ACCEL_LAT]!!.value)
            assertEquals(expectedDataLine.accelLon, this[FieldEnum.ACCEL_LON]!!.value)
            assertEquals(expectedDataLine.boost, this[FieldEnum.OBD_BOOST]!!.value)
            assertEquals(expectedDataLine.brakePressure, this[FieldEnum.OBD_BRAKE_PRESSURE]!!.value)
            assertEquals(expectedDataLine.gear, this[FieldEnum.OBD_GEAR]!!.value)
            assertEquals(expectedDataLine.gpsLat, this[FieldEnum.GPS_LAT]!!.value)
            assertEquals(expectedDataLine.gpsLon, this[FieldEnum.GPS_LON]!!.value)
            assertEquals(expectedDataLine.gpsUpdate, this[FieldEnum.GPS_UPD]!!.value)
            assertEquals(expectedDataLine.power, this[FieldEnum.OBD_POWER]!!.value)
            assertEquals(expectedDataLine.relativeTime, this[FieldEnum.REL_TIME]!!.value)
            assertEquals(expectedDataLine.rpm, this[FieldEnum.OBD_RPM]!!.value)
            assertEquals(expectedDataLine.speed, this[FieldEnum.OBD_SPEED]!!.value)
            assertEquals(expectedDataLine.steeringAngle, this[FieldEnum.OBD_STEERING_ANGLE]!!.value)
            assertEquals(expectedDataLine.tempClutch, this[FieldEnum.OBD_TEMP_CLUTCH]!!.value)
            assertEquals(expectedDataLine.tempCoolant, this[FieldEnum.OBD_TEMP_COOLANT]!!.value)
            assertEquals(expectedDataLine.tempExternal, this[FieldEnum.OBD_TEMP_EXT]!!.value)
            assertEquals(expectedDataLine.tempGearbox, this[FieldEnum.OBD_TEMP_GEARBOX]!!.value)
            assertEquals(expectedDataLine.tempIntake, this[FieldEnum.OBD_TEMP_INTAKE]!!.value)
            assertEquals(expectedDataLine.tempOil, this[FieldEnum.OBD_TEMP_OIL]!!.value)
            assertEquals(expectedDataLine.boost, this[FieldEnum.OBD_BOOST]!!.value)
            assertEquals(expectedDataLine.torque, this[FieldEnum.OBD_TORQUE]!!.value)
            assertEquals(expectedDataLine.wheelFL, this[FieldEnum.OBD_WHEEL_FL]!!.value)
            assertEquals(expectedDataLine.wheelFR, this[FieldEnum.OBD_WHEEL_FR]!!.value)
            assertEquals(expectedDataLine.wheelRL, this[FieldEnum.OBD_WHEEL_RL]!!.value)
            assertEquals(expectedDataLine.wheelRR, this[FieldEnum.OBD_WHEEL_RR]!!.value)
        }
    }

    @Test
    fun testDataWriter() {
        val fields = listOf(
                FieldEnum.REL_TIME,
                FieldEnum.GPS_UPD,
                FieldEnum.OBD_UPD,
                FieldEnum.GPS_LAT,
                FieldEnum.GPS_LON,

                FieldEnum.OBD_SPEED,
                FieldEnum.OBD_GEAR,
                FieldEnum.OBD_RPM,
                FieldEnum.OBD_THROTTLE_PERCENT,
                FieldEnum.OBD_BRAKE_PRESSURE,
                FieldEnum.OBD_STEERING_ANGLE,

                FieldEnum.OBD_BOOST,
                FieldEnum.OBD_POWER,
                FieldEnum.OBD_TORQUE,

                FieldEnum.ACCEL_LAT,
                FieldEnum.ACCEL_LON,

                FieldEnum.OBD_TEMP_COOLANT,
                FieldEnum.OBD_TEMP_OIL,
                FieldEnum.OBD_TEMP_CLUTCH,
                FieldEnum.OBD_TEMP_GEARBOX,
                FieldEnum.OBD_TEMP_EXT,
                FieldEnum.OBD_TEMP_INTAKE

                /*FieldEnum.OBD_WHEEL_FL,
                FieldEnum.OBD_WHEEL_FR,
                FieldEnum.OBD_WHEEL_RL,
                FieldEnum.OBD_WHEEL_RR*/
        )

        val map = HashMap<FieldEnum, Field<Any>>()
        map[FieldEnum.OBD_UPD] = BooleanField(true)
        map[FieldEnum.OBD_THROTTLE_PERCENT] = IntField(27)
        map[FieldEnum.OBD_BRAKE_PRESSURE] = DoubleField(0.0)
        map[FieldEnum.OBD_STEERING_ANGLE] = IntField(-119)

        map[FieldEnum.OBD_GEAR] = ByteField(2)
        map[FieldEnum.OBD_SPEED] = DoubleField(61.12021857923497)

        map[FieldEnum.OBD_TEMP_INTAKE] = IntField(28)
        map[FieldEnum.OBD_TEMP_COOLANT] = IntField(92)
        map[FieldEnum.OBD_TEMP_OIL] = IntField(111)
        map[FieldEnum.OBD_TEMP_GEARBOX] = IntField(85)
        map[FieldEnum.OBD_TEMP_CLUTCH] = IntField(102)
        map[FieldEnum.OBD_TEMP_EXT] = IntField(17)
        map[FieldEnum.GPS_LON] = LatLonField(5.9670701)
        map[FieldEnum.GPS_LAT] = LatLonField(50.441627499999996)
        map[FieldEnum.GPS_UPD] = BooleanField(false)

        map[FieldEnum.OBD_RPM] = IntField(4946)

        map[FieldEnum.OBD_WHEEL_RR] = IntField(30750)
        map[FieldEnum.OBD_WHEEL_RL] = IntField(27993)
        map[FieldEnum.OBD_WHEEL_FR] = IntField(31061)
        map[FieldEnum.OBD_WHEEL_FL] = IntField(30962)

        map[FieldEnum.ACCEL_LAT] = DoubleField(1.11328125)
        map[FieldEnum.ACCEL_LON] = DoubleField(-0.0390625)

        map[FieldEnum.REL_TIME] = DoubleField(1000.6)

        map[FieldEnum.OBD_BOOST] = IntField(40)
        map[FieldEnum.OBD_TORQUE] = IntField(87)
        map[FieldEnum.OBD_POWER] = IntField(61)

        val expectedResult = "1000.60,0,1,50.4416275,5.9670701,61.12,2,4946,27,0.00,-119,40,61,87,1.11,-0.04,92,111,102,85,17,28\n"

        val writer = StringWriter()

        val dataWriter = RaceRenderCSVDataWriter(PrintWriter(writer), fields)
        dataWriter.writeData(map, PrintWriter(writer))
        assertEquals(expectedResult, writer.toString())
    }
}