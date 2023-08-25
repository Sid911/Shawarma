data class Token(
    val type: TokenType,
    val lexeme: String,
    val literal: Any?,
    val line: Int,
    val start: Int,
    val offset: Int,
)

/**
 * Token Type for shawarma language
 */
enum class TokenType {
    // Single-character tokens
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, COLON ,SEMICOLON, PLUS, MINUS, STAR, SLASH,

    // One or two character tokens
    EQUAL, EQUAL_EQUAL,NOT_EQUAL
    GREATER, GREATER_EQUAL, LESS, LESS_EQUAL,OR,PLUS,

    // Literals
    IDENTIFIER, STRING, NUMBER, DATATYPE,

    // Keywords
    INGREDIENT, FROOZEN, RECIPE,BILL, TASTE, ELSESPICE, SWALLOW,
    STIR, BAKE, GIVEUP, NEXTPLS, FAVORITE, CASE, OTHERWISE,
    SERVE, ORDER, MENU, FLAVOR,TRUE, FALSE, COOK, POTATO,FOR,ME,DIGEST

    // Special tokens
    EOF, EOL
}