package com.rocasspb.rsmonconverter.output

import com.rocasspb.rsmonconverter.field.*
import java.io.PrintWriter
import java.util.*

class RaceRenderCSVDataWriter(private val output: PrintWriter) : OutputDataWriter<Map<FieldEnum, Field<Any>>> {
    private val fields = listOf(
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
            FieldEnum.OBD_TEMP_INTAKE)

    private val fieldToLabelMap = hashMapOf(
            FieldEnum.REL_TIME to "\"Time\"",
            FieldEnum.GPS_UPD to "\"GPS_Update\"",
            FieldEnum.OBD_UPD to "\"OBD_Update\"",
            FieldEnum.GPS_LAT to "\"Latitude\"",
            FieldEnum.GPS_LON to "\"Longitude\"",
            FieldEnum.OBD_SPEED to "\"Speed (KPH) *OBD\"",
            FieldEnum.OBD_GEAR to "\"Gear *OBD\"",
            FieldEnum.OBD_RPM to "\"Engine Speed (RPM) *OBD\"",
            FieldEnum.OBD_THROTTLE_PERCENT to "\"Accelerator Pedal (%) *OBD\"",
            FieldEnum.OBD_BRAKE_PRESSURE to "\"Brake Pedal *OBD\"",
            FieldEnum.OBD_STEERING_ANGLE to "\"Steering angle *OBD\"",
            FieldEnum.OBD_BOOST to "\"Boost *OBD\"",
            FieldEnum.OBD_POWER to "\"Power *OBD\"",
            FieldEnum.OBD_TORQUE to "\"Torque *OBD\"",
            FieldEnum.ACCEL_LAT to "\"Acceleration Lateral *OBD\"",
            FieldEnum.ACCEL_LON to "\"Acceleration Longitudinal *OBD\"",
            FieldEnum.OBD_TEMP_COOLANT to "\"Temp Coolant *OBD\"",
            FieldEnum.OBD_TEMP_OIL to "\"Temp Oil *OBD\"",
            FieldEnum.OBD_TEMP_CLUTCH to "\"Temp Clutch *OBD\"",
            FieldEnum.OBD_TEMP_GEARBOX to "\"Temp Gearbox *OBD\"",
            FieldEnum.OBD_TEMP_EXT to "\"Temp External *OBD\"",
            FieldEnum.OBD_TEMP_INTAKE to "\"Temp Intake *OBD\"",

            FieldEnum.OBD_WHEEL_FR to "\"Wheel Speed FR *OBD\"",
            FieldEnum.OBD_WHEEL_FL to "\"Wheel Speed FL *OBD\"",
            FieldEnum.OBD_WHEEL_RR to "\"Wheel Speed RR *OBD\"",
            FieldEnum.OBD_WHEEL_RL to "\"Wheel Speed RL *OBD\""
    )

    override fun writeDataToStream(dataList: List<Map<FieldEnum, Field<Any>>>) {
        writeHeader(output)
        writeDataTableHeader(output)

        for (dataLine in dataList) {
            writeData(dataLine, output)
        }
        output.flush()
        output.close()
    }

    fun writeData(dataLine: Map<FieldEnum, Field<Any>>, output: PrintWriter) {
        with(dataLine) {
            val line = fields.joinToString(separator = ",", postfix = "\n")
                { RRCSVValueVisitor().visit(dataLine[it]!!)}
            output.print(line)
        }
    }

    private fun writeHeader(output: PrintWriter) {
        output.println("# RaceRender Data")
    }

    private fun writeDataTableHeader(output: PrintWriter) {
        val line = fields.joinToString( separator = ",", postfix = "\n" ) {headerLabelByFieldEnum(it)!!}
        output.print(line)
    }

    private fun headerLabelByFieldEnum(enum: FieldEnum) = fieldToLabelMap[enum]
}

class RRCSVValueVisitor : FieldVisitor<String> {
    override fun <T> visit(field: Field<T>) : String = when(field){
            is LatLonField -> formatDoubleDefault(field.value, 7)
            is DoubleField -> formatDoubleDefault(field.value)
            is IntField -> field.value.toString()
            is ByteField -> field.value.toString()
            is BooleanField -> if(field.value) 1.toString() else 0.toString()
    }
}

private fun formatDoubleDefault(double: Double, precision: Int = 2) = String.format(Locale.ROOT, "%." + precision + "f", double)

