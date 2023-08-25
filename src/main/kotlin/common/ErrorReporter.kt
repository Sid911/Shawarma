import common.Token

interface ErrorReporterInterface {
    var hadError: Boolean

    fun error(line: Int, message: String)
    fun error(token: Token, message: String)
}