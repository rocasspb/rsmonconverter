import org.junit.Test

class ConverterTest {
    var converter = Converter()

    @Test
    fun testFindOneByteSequence() {
        val sequence = ByteArray(1)
        sequence.set(0, 'a'.toByte())

        val bytes = "abcabcabcadcabd".toByteArray()

        converter.findSequence(bytes, sequence, 0, 3)
    }

    @Test
    fun testFindTwoByteSequence() {
        val sequence = "ab".toByteArray()
        val bytes = "abcabcabcadcabd".toByteArray()

        converter.findSequence(bytes, sequence, 0, 3)
    }

    @Test
    fun testListSequences() {
        val sequence = "ab".toByteArray()
        val bytes = "abcabcabcadcabd".toByteArray()

        converter.listSequences(bytes, 1, 3)
    }

}