   sealed class Stmt{
       class Block(val statements: List<Stmt>): Stmt()
       class Expression(val expr: Expr): Stmt()
       class Receipe(val name: Token, val params: List<Token>, val body: List<Stmt>): Stmt()
       class Taste(val condition: Expr, val thenBranch: Stmt, val elseBranch: Stmt?): Stmt()
       class Serve(val value: Expr): Stmt()
       class Bill(val keyword: Token, val value: Expr?): Stmt()
       class Ingredient(val name: Token, val initializer: Expr?): Stmt()
       class Stir(val condition: Expr, val body: Stmt): Stmt()
   }