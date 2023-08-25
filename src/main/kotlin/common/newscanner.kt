class Scanner2(private val sourceCode: String) {
    private var currentPos = 0
    private var currentLine = 1
    private var currentStart = 0

    fun scanTokens(): List<Token> {
        val tokens = mutableListOf<Token>()

        while (!isAtEnd()) {
            currentStart = currentPos
            val token = scanToken()
            if (token != null) {
                tokens.add(token)
            }
        }

        tokens.add(Token(TokenType.EOF, "", null, currentLine, currentStart, currentPos - currentStart))
        return tokens
    }

    private fun scanToken(): Token? {
        val c = advance()

        return when (c) {
            '(' -> Token(TokenType.LEFT_PAREN, "(", null, currentLine, currentStart, 1)
            ')' -> Token(TokenType.RIGHT_PAREN, ")", null, currentLine, currentStart, 1)
            '{' -> Token(TokenType.LEFT_BRACE, "{", null, currentLine, currentStart, 1)
            '}' -> Token(TokenType.RIGHT_BRACE, "}", null, currentLine, currentStart, 1)
            ',' -> Token(TokenType.COMMA, ",", null, currentLine, currentStart, 1)
            '.' -> Token(TokenType.DOT, ".", null, currentLine, currentStart, 1)
            ':' -> Token(TokenType.COLON, ":", null, currentLine, currentStart, 1)
            ';' -> Token(TokenType.SEMICOLON, ";", null, currentLine, currentStart, 1)
            '+' -> Token(TokenType.PLUS, "+", null, currentLine, currentStart, 1)
            '-' -> Token(TokenType.MINUS, "-", null, currentLine, currentStart, 1)
            '*' -> Token(TokenType.STAR, "*", null, currentLine, currentStart, 1)
            '/' -> Token(TokenType.SLASH, "/", null, currentLine, currentStart, 1)
            '=' -> {
                if (match('=')) Token(TokenType.EQUAL_EQUAL, "==", null, currentLine, currentStart, 2)
                else Token(TokenType.EQUAL, "=", null, currentLine, currentStart, 1)
            }
            '!' -> {
                if (match('=')) Token(TokenType.NOT_EQUAL, "!=", null, currentLine, currentStart, 2)
                else null
            }
            '>' -> {
                if (match('=')) Token(TokenType.GREATER_EQUAL, ">=", null, currentLine, currentStart, 2)
                else Token(TokenType.GREATER, ">", null, currentLine, currentStart, 1)
            }
            '<' -> {
                if (match('=')) Token(TokenType.LESS_EQUAL, "<=", null, currentLine, currentStart, 2)
                else Token(TokenType.LESS, "<", null, currentLine, currentStart, 1)
            }
            '[' -> {
                // Handle variable declaration
                val lexeme = scanUntil(']')
                if (match(']')) Token(TokenType.IDENTIFIER, lexeme, null, currentLine, currentStart, lexeme.length + 2)
                else null
            }
            // Handle other cases for IDENTIFIER, STRING, NUMBER, DATATYPE, and Keywords
            else -> null
        }
    }

    private fun isAtEnd(): Boolean {
        return currentPos >= sourceCode.length
    }

    private fun advance(): Char {
        val c = sourceCode[currentPos]
        if (c == '\n') {
            currentLine++
        }
        currentPos++
        return c
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (sourceCode[currentPos] != expected) return false
        currentPos++
        return true
    }

    private fun scanUntil(delimiter: Char): String {
        while (!isAtEnd() && sourceCode[currentPos] != delimiter) {
            currentPos++
        }
        return sourceCode.substring(currentStart + 1, currentPos)
    }
}
