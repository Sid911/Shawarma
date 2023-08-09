
sealed class Statement{
       class Block(val statements: List<Statement>): Statement()
       class Expression(val expr: Expr): Statement()
       class Receipe(val name: Token, val params: List<Token>, val body: List<Statement>): Statement()
       class Taste(val condition: Expr, val thenBranch: Statement, val elseBranch: Statement?): Statement()
       class Serve(val value: Expr): Statement()
       class Bill(val keyword: Token, val value: Expr?): Statement()
       class Ingredient(val name: Token, val initializer: Expr?): Statement()
       class Stir(val condition: Expr, val body: Statement): Statement()
   }