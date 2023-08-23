
class Hello{
    fun print(): String{
        return "Hello"
    }
}

fun shiftLeft(i: Int) : Int{
    return i.shl(2) // 0001 << 2 = 0100
}

fun main(args: Array<String>) {
    println("Hello World!")
    println("Program arguments: ${args.joinToString()}")
    val parser: Parser = Parser;
}