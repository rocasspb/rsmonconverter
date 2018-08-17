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
            writeField(throttlePercent.toString(), output, true)

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
        val sb = StringBuilder()
        //todo - move to dataLine, make hashmap
        sb.append("\"Time\"")
        sb.append("\"GPS_Update\"")
        sb.append("\"OBD_Update\"")
        sb.append("\"Latitude\"")
        sb.append("\"Longitude\"")
        //sb.append("\"Altitude (m)\"")
        sb.append("\"Speed (KPH)\"")
        sb.append("\"Engine Speed (RPM) *OBD\"")
        sb.append("\"Accelerator Pedal (%) *OBD\"")
        output.println(sb)
    }
}