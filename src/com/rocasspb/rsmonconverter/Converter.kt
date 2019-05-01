package com.rocasspb.rsmonconverter

import com.rocasspb.rsmonconverter.field.FieldEnum
import com.rocasspb.rsmonconverter.input.InputDataReader
import com.rocasspb.rsmonconverter.input.RSMonitorReader
import com.rocasspb.rsmonconverter.output.OutputDataWriter
import com.rocasspb.rsmonconverter.output.RaceRenderCSVDataWriter
import java.io.ByteArrayInputStream
import java.io.File
import java.io.PrintWriter

class Converter<T>(private val dataReader: InputDataReader<T>,
                   private val stream : ByteArrayInputStream,
                   private val output: OutputDataWriter<T>) {
    fun readInput(): List<T> = dataReader.readFromBytes(stream)

    fun writeOutput(data: List<T>) {
        output.writeDataToStream(data)
    }
}

fun main(args: Array<String>) {
    if(args.isEmpty())
        throw IllegalArgumentException("Please provide input and output filenames")
    val inputName = args[0]
    val file = File(inputName)
    if(file.isDirectory) {
        file.listFiles { _, name -> name.endsWith(".run") }
                .forEach { processFile(it, formatOutputName(it.name)) }
    } else {
        val outName = if (args.size > 1)
            args[1]
        else
            formatOutputName(inputName)
        processFile(file, outName)
    }
}

fun processFile(file: File, outName: String) {
    val bytes = file.readBytes()
    val stream = ByteArrayInputStream(bytes)
    val dataReader = RSMonitorReader()
    //dataReader.setDataLogger(com.rocasspb.rsmonconverter.DiffedInputDataLogger(true, true))

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
            FieldEnum.OBD_TEMP_INTAKE,

            FieldEnum.OBD_WHEEL_FL,
            FieldEnum.OBD_WHEEL_FR,
            FieldEnum.OBD_WHEEL_RL,
            FieldEnum.OBD_WHEEL_RR
    )

    val converter = Converter(dataReader, stream, RaceRenderCSVDataWriter(PrintWriter(outName), fields))
    converter.apply {
        writeOutput(readInput())
    }
}

fun formatOutputName(inputName: String) = inputName.substring(0, inputName.lastIndexOf('.')) + ".csv"