class DiffedInputDataLOgger : InputDataLogger {
    companion object {
        const val ANSI_RESET = "\u001B[0m"
        const val ANSI__YELLOW = "\u001B[33m"
        const val ANSI_BLUE = "\u001B[34m"
    }

    private var oldLine: ByteArray? = null
    private var i = 0

    override fun logData(line: ByteArray) {
        if(oldLine == null) {
            oldLine = line
            printHeader(line.size)
            return
        }

        val text = StringBuilder()

        for (i in 0 until line.size) {
            if(oldLine!!.size <= i) {
                //todo
            }

            text.append(printColoredByte(line[i], oldLine!![i]))
        }

        text.append(ANSI_BLUE).append(i++).append(ANSI_RESET)


        val dl = DataLine()
        //dl.readFromBytes(line)
        text.append(" ")
        text.append(ANSI__YELLOW)
        text.append(dl)
        text.append(ANSI_RESET)

        println(text)

        oldLine = line
    }

    private fun printHeader(size: Int) {
        val text = StringBuilder()

        text.append(ANSI_BLUE)
        for (i in 0 until size) {
            text.append(printByte(i.toByte()))
        }
        text.append(ANSI_RESET)
        println(text)
    }

    private fun printColoredByte(current: Byte, old: Byte) : String {
        val s = printByte(current)
        if(current == old)
            return s

        return ANSI__YELLOW + s + ANSI_RESET

    }

    private fun printByte(byte: Byte) = String.format("%02X ", byte)
}