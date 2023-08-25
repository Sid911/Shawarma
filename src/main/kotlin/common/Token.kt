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
ingredient [ing] // variable decleration
frozen // non mutable
recipe // function
bill // return
taste // if
elseSpice // elseif
swallow // else
stir // while
bake // for
giveUp // break
nextPls // continue
favorite // switch
case // case
otherwise // default
serve // println
order // input stdin
menu // struct
flavor // enum
potato // trait
cook // implementation
for // for in implementation cook [Trait] for [struct]
me // Self for structs
digest // gc the memory