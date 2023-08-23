/**
 * @author Shikhar
 * @property line Represents line number for the token
 * @property start Represents charter index till the start of the document
 */
data class Token(
    val type: TokenType,
    val lexeme: String,
    val literal: Any?,
    val line: Int,
    val start: Int,
    val offset: Int,
)

/**
 * Token Type for shwarma language
 */
enum class TokenType {
    // Single-character tokens
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, COLON ,SEMICOLON, PLUS, MINUS, STAR, SLASH,

    // One or two character tokens
    BANG, BANG_EQUAL, EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL, LESS, LESS_EQUAL,

    // Literals
    IDENTIFIER, STRING, NUMBER, DATATYPE,

    // Keywords
    INGREDIENT, FREEZE, RECIPE, PLATE, TASTE, ELSESPICE, SWALLOW,
    STIR, BAKE, GIVEUP, NEXTPLS, FAVORITE, CASE, OTHERWISE,
    SERVE, ORDER, MENU, FLAVOR, TRUE, FALSE, COOK, POTATO,

    // Special tokens
    EOF, EOL
}