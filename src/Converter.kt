import java.io.File
import java.util.*
import javax.xml.bind.DatatypeConverter

const val ANSI_RESET = "\u001B[0m"
const val ANSI__YELLOW = "\u001B[33m"
const val ANSI_BLUE = "\u001B[34m"

class Converter {
    fun listSequences(bytes: ByteArray, length: Int, distance: Int) {
        for (index in 0..bytes.size - distance - length) {
            val sequence = bytes.copyOfRange(index, index + length + 1)
            findSequence(bytes, sequence, index, distance)
        }

    }

    fun findSequence(bytes: ByteArray, sequence: ByteArray, index: Int, distance : Int) {
        var numberOfMatches = 0
        var currentIndex = index
        while(bytes.size > currentIndex + distance + sequence.size) {
            var i = 0
            for (byte in sequence) {
                if (byte != bytes[currentIndex + i++]) {
                    reportSequence(sequence, index, numberOfMatches)
                    return
                }
            }
            numberOfMatches++
            currentIndex += distance
        }

        reportSequence(sequence, index, numberOfMatches)
    }

    private fun reportSequence(sequence: ByteArray, firstOccurence: Int, nuberOfMatches: Int) {
        if(nuberOfMatches < 3000) //threshold
            return
        val builder = StringBuilder()
        for (byte in sequence) {
            builder.append(printByte(byte))
        }

        println("Found sequence: ${builder.toString()}\n first occurence: $firstOccurence\n Repeats: $nuberOfMatches")
    }

    fun enlistDiffs(bytes: ByteArray, seqStart: ByteArray, limit: Int) {
        diffLines(collectLines(bytes, seqStart, limit))
    }

    private fun collectLines(bytes: ByteArray, seqStart: ByteArray, limit: Int) : List<ByteArray> {
        val list = LinkedList<ByteArray>()
        var count = 0

        var index = 0 //findNextSequenceStart(bytes, seqStart, 0)
        if(index == -1)
            return emptyList()

        while(count < limit) {
            val nextIndex = index + 183
            if(nextIndex >= bytes.size)
                break

            list.add(bytes.copyOfRange(index, nextIndex))
            index = nextIndex
            count++
        }
        return list
    }

    private fun diffLines(list : List<ByteArray>) {
        var oldLine: ByteArray? = null
        for ((j, line) in list.withIndex()) {
            if(oldLine == null) {
                oldLine = line
                printHeader(line.size)

                continue
            }

            val text = StringBuilder()

            for (i in 0 until line.size) {
                if(oldLine.size <= i) {
                    //todo
                }

                text.append(printColoredByte(line[i], oldLine[i]))
            }

            text.append(ANSI_BLUE).append(j).append(ANSI_RESET)


            val dl = DataLine()
            dl.readFromBytes(line)
            text.append(" ")
            text.append(ANSI__YELLOW)
            text.append(dl)
            text.append(ANSI_RESET)

            println(text)

            oldLine = line
        }
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

    private fun findNextSequenceStart(bytes: ByteArray, seqStart: ByteArray, index: Int) : Int {
        var currentIndex = index
        while(bytes.size > currentIndex + seqStart.size) {
            if (isMatchingSequence(bytes, seqStart, currentIndex))
                return currentIndex
            currentIndex++
        }
        return -1
    }

    private fun isMatchingSequence(bytes: ByteArray, seqStart: ByteArray, index: Int) : Boolean {
        for ((i, byte) in seqStart.withIndex()) {
            if (byte != bytes[index + i]) {
                return false
            }
        }
        return true
    }
}

fun main(args: Array<String>) {
    val seq = DatatypeConverter.parseHexBinary("397FFF")
    val seq2 = DatatypeConverter.parseHexBinary("4c04b0")
    //Converter().listSequences(File("C:/temp/log.run").readBytes(), 6, 183)
    Converter().enlistDiffs(File("C:/temp/new/log1.run").readBytes(), seq, 1000)
}