import kotlin.test.*

class InterpreterTest {
    @Test
    fun testShiftLeft() {
//        assert(false)
        assertTrue( shiftLeft(3) == 12)
        assertEquals(shiftLeft(3), 12)
    }
}

class HelloTests{
    @Test
    fun testHello(){
        val h = Hello()
        assertEquals( h.print() , "Hello")
    }

    @Test
    fun the(){
        val h = Hello()
        assertEquals( h.print() , "Hello")
    }
}