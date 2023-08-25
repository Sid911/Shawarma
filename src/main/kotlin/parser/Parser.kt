import common.Token
import common.TokenType
import java.lang.RuntimeException
import java.util.ArrayDeque

 public class Parser(
     private val tokens: ArrayDeque<Token>,
     private val errorReporter: ErrorReporterInterface
 ){
     private class ParseError : RuntimeException()
     private var current = 0;

     fun parse(): List<Statement>{
         val statements = mutableListOf<Statement>()
         while (!isAtEnd()){
             declaration()?.let{statements.add(it)}
         }
         return statements
     }

     private fun declaration(): Statement?{
         return try {
             when{
                 match(TokenType.MENU) -> menuDeclaration()
                 match(TokenType.RECEIPE) -> Receipe("Receipe")
                 match(TokenType.INGREDIENT) -> Ingredient()
                 else -> statement()
             }
         }catch (error: ParseError){
             synchronize()
             null
         }
     }

     private fun menuDeclaration(): Statement.Menu{
         val name = consume(TokenType.IDENTIFIER, "Expect menu name")
         consume(TokenType.LEFT_BRACE, "Expect '{' before menu body")

         val fields = mutableListOf<Statement.Menu>()
         while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()){
             fields.add(Receipe("method"))
         }
         consume(TokenType.RIGHT_BRACE, "Expect '}' after menu body")
         return Statement.Menu(name,fields)
     }

     private fun Receipe(kind: String): Statement.Receipe{
         val name = consume(TokenType.IDENTIFIER, "Expect $kind name.")
         consume(TokenType.LEFT_PAREN, "Expect '(' after $kind name.")
         val parameters = mutableListOf<Token>()
         if (!check(TokenType.RIGHT_PAREN)){
             do {
                 if (parameters.size >= 255){
                     error(peek(), "Can't have more than 255 parameters.")
                 }
                 parameters.add(consume(TokenType.IDENTIFIER, "Expext parameter name."))
             }while (match(TokenType.COMMA))
         }
         consume(TokenType.RIGHT_PAREN, "Expect ')' after parameters.")
         consume(TokenType.LEFT_BRACE, "Expext '{' before $kind body.")
         val body = block()
         return Statement.Receipe(name,parameters,body)
     }

     private fun Ingredient(): Statement{
         val name = consume(TokenType.IDENTIFIER, "Expect ingredient name")
         val isFrozen = match(TokenType.FREEZE)
         consume(TokenType.COLON, "Expect ':' after ingredient name")
         val kind = consume(TokenType.IDENTIFIER, "Expect ingredient type")
         if (isFrozen){
             val initialization = expression()
             consume(TokenType.SEMI_COLON, "Expect ';' after ingredient declaration")
             return Statement.Ingredient(name,kind,initialization)
         }
         else{
             consume(TokenType.SEMICOLON, "Expect ';' after ingredient declaration")
             return Statement.Ingredient(name,kind,null)
         }
     }

     private fun statement(): Statement{
         return when{
             match(TokenType.BAKE) -> bakestatement()
             match(TokenType.TASTE) -> tastestatement()
             match(TokenType.SERVE) -> servestatement()
             match(TokenType.BILL) -> billstatement()
             match(TokenType.STIR) -> stirstatement()
             match(TokenType.FAVOURITE) -> favouritestatement()
             match(TokenType.GIVEUP) -> giveUpStatement()
             match(TokenType.LEFT_BRACE) -> Statement.Block(block())
             else -> expressionstatement()
         }
     }

     private fun billstatement(): Statement{
         val keyword = previous()
         val value = if (!check(TokenType.SEMICOLON)) expression() else null
         consume(TokenType.SEMICOLON, "Expect ';' after return value")
         return Statement.Bill(keyword, value)
     }

     private fun bakestatement(): Statement{
         consume(TokenType.LEFT_PAREN, "Expect '(' after bake")
         val initializer = when {
             match(TokenType.SEMICOLON) -> null
             match(TokenType.INGREDIENT) -> Ingredient()
             else -> expressionstatement()
         }
         val condition = if (!check(TokenType.SEMICOLON)) expression() else Expression.Literal(true)
         consume(TokenType.SEMICOLON, "Expect ';' after loop condition")
         val increment = if (!check(TokenType.RIGHT_PAREN)) expression() else null
         var body = statement()
         increment?.let {body = Statement.Block(listOf(body,Statement.Expression(it))) }
         body = Statement.Stir(condition, body)
         initializer?.let { body = Statement.Block(listOf(it, body))}
         return body
     }

     private fun favouritestatement(): Statement{
         val condition = expression()
         consume(TokenType.LEFT_BRACE, "Expect '{' before favourite case")
         val cases = mutableListOf<Pair<Expression, List<Statement>>>()
         while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
             if (match(TokenType.BREAK)) {
                 caseStatements.add(breakStatement())
             } else {
                 val casevalue = expression()
                 consume(TokenType.COLON, "Expect ':' after case value ")
                 val caseStatement = block()
                 cases.add(casevalue to caseStatement)
             }
         }
         consume(TokenType.RIGHT_BRACE, "Expect '}' after favourite cases")
         return Statement.Favourite(condition,cases)
     }

     private fun stirstatement(): Statement{
         consume(TokenType.LEFT_PAREN, "Expect '(' after stir ")
         val condition = expression()
         val body = statement()
         return Statement.Stir(condition, body)
     }

     private fun block(): List<Statement>{
         val statements = mutableListOf<Statement>()
         while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()){
             declaration()?.let {statements.add(it)}
         }
         consume(TokenType.RIGHT_BRACE, "Expect '}' after block.")
         return statements
     }

     private fun tastestatement(): Statement{
         consume(TokenType.LEFT_PAREN, "Expect '(' after taste")
         val condition = expression()
         consume(TokenType.RIGHT_PAREN, "Expect ')' after taste condition")

         val thenBranch = statement()
         val elseifBranches = mutableListOf<Pair<Expression, Statement>>()

         while (match(TokenType.ELSESPICE)) {
             consume(TokenType.LEFT_PAREN, "Expect '(' after elsespice")
             val elseifCondition = expression()
             consume(TokenType.RIGHT_PAREN, "Expect ')' after elsespice condition")
             val elseifBranch = statement()
             elseifBranches.add(elseifCondition to elseifBranch)
         }
         val elseBranch = if (match(TokenType.SWALLOW)) statement() else null
         return Statement.Taste(condition, thenBranch, elseifBranches, elseBranch)
     }

     private fun expressionstatement(): Statement{
         val expr = expression()
         consume(TokenType.SEMICOLON, "Expect ';' after expression.")
         return Statement.Expression(expr)
     }

     private fun servestatement(): Statement{
         val value = expression()
         consume(TokenType.SEMICOLON, "Expect ';' after value.")
         return Statement.Serve(value)
     }

     private fun expression(): Expression{
         return assignment()
     }

     private fun assignment(): Expression{
         val expr = or()
         if (match(TokenType.EQUAL)) {
             val equals = previous()
             val value = assignment()
             when (expr) {
                 is Expression.Variable -> {
                     val name = expr.name
                     return Expression.Assign(name,value)
                 }
                 is Expression.Get -> {
                     return Expression.Set(expr.obj, expr.name, value)
                 }
                 else -> error(equals, "Invalid assignment target")
             }
             error(equals, "Invalid assignment target")
         }
         return expr
     }

     private fun or(): Expression{
         val expr = and()
         while (match(TokenType.OR)) {
             val operator = previous()
             val right = and()
             expr = Expression.Logical(expr,operator,right)
         }
         return expr
     }

     private fun and(): Expression{
         var expr = equality()
         while (match(TokenType.AND)){
             val operator = previous()
             val right = equality()
             expr = Expression.Logical(expr,operator,right)
         }
         return expr
     }

     private fun equality(): Expression{
         var expr = comparison()
         while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)){
             val operator = previous()
             val right = comparison()
             expr = Expression.Binary(expr,operator,right)
         }
         return expr
     }

     private fun previous(): Token {
         return tokens[current - 1]
     }

     private fun match(vararg types: TokenType): Boolean{
         for (type in types){
             if (check(type)){
                 advance()
                 return true
             }
         }
         return false
     }

     private fun advance(): Token {
         if (!isAtEnd()){
             current++
         }
         return previous()
     }

     private fun synchronize(){
         advance()
         while (!isAtEnd()){
             if (previous().type === TokenType.SEMICOLON){
                 return
             }
             when(peek().type){
                 TokenType.MENU, TokenType.RECEIPE, TokenType.INGREDIENT, TokenType.BAKE, TokenType.TASTE,
                 TokenType.STIR, TokenType.BAKE, TokenType.SERVE,
                 TokenType.BILL -> {
                     return
                 }
             }
             advance()
         }
     }

     private fun comparison(): Expression{
         var expr = term()
         while (match(
                 TokenType.GREATER,
                 TokenType.GREATER_EQUAL,
                 TokenType.LESS,
                 TokenType.LESS_EQUAL,
         ))
         {
             val operator = previous()
             val right = term()
             expr = Expression.Binary(expr,operator,right)
         }
         return expr
     }

     private fun term(): Expression{
         var expr = factor()
         while (match(TokenType.PLUS, TokenType.MINUS)){
             val operator = previous()
             val right = factor()
             expr = Expression.Binary(expr,operator,right)
         }
         return expr
     }

     private fun factor(): Expression{
         var expr = unary()
         while (match(TokenType.SLASH, TokenType.STAR)){
             val operator = previous()
             val right: Expression = unary()
             expr = Expression.Binary(expr,operator,right)
         }
         return expr
     }

     private fun unary(): Expression{
         while (match(TokenType.BANG, TokenType.MINUS)){
             val operator = previous()
             val right = unary()
             return Expression.Unary(operator,right)
         }
         return call()
     }

     private fun call(): Expression{
         var expr = primary()
         while (true){
             expr = if (match(TokenType.LEFT_PARAN)){
                 finishcall(expr)
             } else if (match(TokenType.DOT)){
                 val name = consume(TokenType.IDENTIFIER, "Expect prperty name after '.' ")
                 Expression.Get(expr,name)
             } else{
                 break
             }
         }
         return expr
     }

     private fun finishcall(callee: Expression): Expression{
         val arguments = mutableListOf<Expression>()
         if (!check(TokenType.RIGHT_PAREN)) {
             do {
                 if (arguments.size >= 255){
                     error(peek(), "Can't have more than 255 arguments.")
                 }
                 arguments.add(expression())
             } while (match(TokenType.COMMA))
         }
         val paren = consume(TokenType.RIGHT_PAREN, "Expect ')' after arguments.")
         return Expression.Call(callee,paren,arguments)
     }

     private fun primary(): Expression{
         return when{
             match(TokenType.FALSE) -> Expression.Literal(false)
             match(TokenType.TRUE) -> Expression.Literal(true)
             match(TokenType.NIL) -> Exoression.Literal(null)
             match(TokenType.NUMBER, TokenType.STRING) -> Expression.Literal(previous().literal)
             match(TokenType.IDENTIFIER) -> Expression.Variable(previous())
             match(TokenType.LEFT_PAREN) -> {
                 val expr = expression()
                 consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.")
                 Expression.Grouping(expr)
             }
             match(TokenType.ME) -> Expression.Me(previous())
             else -> throw error(peek(), "Expect expression.")


         }
     }

     private fun consume(type: TokenType, message: String): Token {
         if(check(type)){
             return advance()
         }
         throw error(peek(),message)
     }

     private fun giveUpStatement(): Statement {
         consume(TokenType.GIVEUP, "Expect 'giveup' statement")
         consume(TokenType.SEMICOLON, "Expect ';' after 'giveup'")
         return Statement.GiveUp()
     }

     private fun error(token: Token, message: string): ParseError{
         errorReporter.error(token,message)
         return ParseError()
     }

     private fun check(type: TokenType): Boolean{
         if(isAtEnd()){
             return false
         }
         return peek().type === type
     }

     private fun peek(): Token {
         return tokens[current]
     }

     private fun isAtEnd(): Boolean{
         return peek().type == TokenType.EOF
     }
 }