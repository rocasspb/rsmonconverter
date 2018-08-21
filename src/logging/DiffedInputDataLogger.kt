package logging

import DataLine

class DiffedInputDataLogger(override var shouldLogData: Boolean, override var shouldLogResult: Boolean) : InputDataLogger<DataLine> {
    companion object {
        const val ANSI_RESET = "\u001B[0m"
        const val ANSI__YELLOW = "\u001B[33m"
        const val ANSI_BLUE = "\u001B[34m"
    }

    private var oldLine: ByteArray? = null
    private var i = 0

    override fun logResult(result: DataLine) {
        if(!shouldLogResult)
            return
        val text = StringBuilder()
        text.append(" ")
        text.append(ANSI__YELLOW)
        text.append(result)
        text.append(ANSI_RESET)

        if(!shouldLogData)
            text.append("\n") //kludge

        print(text)
    }

    override fun logData(bytes: ByteArray) {
        if(!shouldLogData)
            return
        if(oldLine == null) {
            oldLine = bytes
            printHeader(bytes.size)
            printSingleLine(bytes)
            return
        }

        printSingleLine(bytes)

        oldLine = bytes.copyOf()
    }

    private fun printSingleLine(bytes: ByteArray) {
        val text = StringBuilder().append("\n")

        for (i in 0 until bytes.size) {
            if (oldLine!!.size <= i) {
                //todo
            }

            text.append(printColoredByte(bytes[i], oldLine!![i]))
        }

        text.append(ANSI_BLUE).append(i++).append(ANSI_RESET)
        print(text)
    }

    private fun printHeader(size: Int) {
        val text = StringBuilder()

        text.append(ANSI_BLUE)
        for (i in 0 until size) {
            text.append(printByte(i.toByte()))
        }
        text.append(ANSI_RESET)
        print(text)
    }

    private fun printColoredByte(current: Byte, old: Byte) : String {
        val s = printByte(current)
        if(current == old)
            return s

        return ANSI__YELLOW + s + ANSI_RESET

    }

    private fun printByte(byte: Byte) = String.format("%02X ", byte)
}