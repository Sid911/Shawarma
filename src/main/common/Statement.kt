
sealed class Statement{
       class Block(val statements: List<Statement>): Statement()
       class ExpressionSt(val expr: Expression): Statement()
       class Receipe(val name: Token, val params: List<Token>, val body: List<Statement>): Statement()
       class Taste(val condition: Expression, val thenBranch: Statement, val elseBranch: Statement?): Statement()
       class Serve(val value: Expression): Statement()
       class Bill(val keyword: Token, val value: Expression?): Statement()
       class Ingredient(val name: Token, val initializer: Expression?): Statement()
       class Stir(val condition: Expression, val body: Statement): Statement()
   }