import com.sun.org.apache.xpath.internal.operations.Bool
import java.io.PrintWriter

class RaceRenderCSVDataWriter(private val output: PrintWriter) : OutputDataWriter<DataLine>  {
    override fun writeDataToStream(dataList: List<DataLine>) {
        writeHeader(output)
        writeDataTableHeader(output)

        for (dataLine in dataList) {
            writeData(dataLine, output)
        }
        output.flush()
        output.close()
    }

    private fun writeData(dataLine: DataLine, output: PrintWriter) {
        dataLine.apply {
            writeField(formatDoubleDefault(relativeTime), output)
            writeField((if (gpsUpdate) 1 else 0).toString(), output)

            writeField(1.toString(), output)
            writeField(formatDoubleDefault(gpsLat, 7), output)
            writeField(formatDoubleDefault(gpsLon, 7), output)
            //writeField(formatDoubleDefault(dataLine), output)
            writeField(formatDoubleDefault(speed), output)
            writeField(rpm.toString(), output)
            writeField(throttlePercent.toString(), output)
            writeField(formatDoubleDefault(brakePressure), output)
            writeField(steeringAngle.toString(), output, true)

            /*writeField(formatDoubleDefault(dataLine.), output)
        writeField(formatDoubleDefault(dataLine), output)
        writeField(formatDoubleDefault(dataLine), output)
        writeField(formatDoubleDefault(dataLine), output)
        writeField(formatDoubleDefault(dataLine), output)
        writeField(formatDoubleDefault(dataLine), output, true)*/
        }
    }

    private fun formatDoubleDefault(double: Double, precision: Int = 2) = String.format("%." + precision + "f", double)

    private fun writeField(fieldText: String, output: PrintWriter, isLast: Boolean = false) {
        val ending = if (isLast) "\n" else ","
        output.print(fieldText + ending)
    }

    private fun writeHeader(output: PrintWriter) {
        output.println("# RaceRender Data")
    }

    private fun writeDataTableHeader(output: PrintWriter) {
        //todo - move to dataLine, make hashmap
        writeField("\"Time\"", output)
        writeField("\"GPS_Update\"", output)
        writeField("\"OBD_Update\"", output)
        writeField("\"Latitude\"", output)
        writeField("\"Longitude\"", output)
        writeField("\"Speed (KPH) *OBD\"", output)
        writeField("\"Engine Speed (RPM) *OBD\"", output)
        writeField("\"Accelerator Pedal (%) *OBD\"", output)
        writeField("\"Brake Pedal *OBD\"", output)
        writeField("\"Steering angle *OBD\"", output, true)
    }
}