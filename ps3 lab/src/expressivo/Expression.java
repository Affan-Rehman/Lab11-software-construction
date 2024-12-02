package expressivo;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import expressivo.parser.*;

/**
 * An immutable type representing a polynomial expression, as defined in
 * the problem set documentation.
 */
public interface Expression {
    
    /**
     * Parse an expression.
     * @param input expression to parse, as defined in the PS3 handout.
     * @return expression AST for the input
     * @throws IllegalArgumentException if the expression is invalid
     */
    public static Expression parse(String input) {
        // Create a stream of characters from the input
        CharStream stream = CharStreams.fromString(input);
        
        // Create a lexer that feeds off of input CharStream
        ExpressionLexer lexer = new ExpressionLexer(stream);
        
        // Create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        
        // Create a parser that feeds off the tokens buffer
        ExpressionParser parser = new ExpressionParser(tokens);
        
        // Tell ANTLR to throw exceptions on error
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, 
                                  int line, int charPositionInLine, String msg, RecognitionException e) {
                throw new IllegalArgumentException(msg);
            }
        });
        
        try {
            // Begin parsing at rule "root"
            ParseTree tree = parser.root();
            
            // Make a parse tree visitor that builds the AST
            ExpressionMaker maker = new ExpressionMaker();
            Expression expression = maker.visit(tree);
            
            return expression;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid expression: " + e.getMessage());
        }
    }

    /**
     * @return a parseable representation of this expression, such that
     * for all e:Expression, e.equals(Expression.parse(e.toString())).
     */
    @Override 
    public String toString();

    /**
     * @param thatObj any object
     * @return true if and only if this and thatObj are structurally-equal
     * Expressions, as defined in the PS3 handout.
     */
    @Override
    public boolean equals(Object thatObj);
    
    /**
     * @return hash code value consistent with the equals() definition of structural
     * equality, such that for all e1,e2:Expression,
     *     e1.equals(e2) implies e1.hashCode() == e2.hashCode()
     */
    @Override
    public int hashCode();
}

/**
 * Make an expression from a parse tree.
 */
class ExpressionMaker extends ExpressionBaseVisitor<Expression> {
    
    @Override
    public Expression visitRoot(ExpressionParser.RootContext context) {
        return visit(context.expression());
    }
    
    @Override
    public Expression visitExpression(ExpressionParser.ExpressionContext context) {
        Expression result = visit(context.product(0));
        
        for (int i = 1; i < context.product().size(); i++) {
            Expression right = visit(context.product(i));
            result = new SumExpression(result, right);
        }
        
        return result;
    }
    
    @Override
    public Expression visitProduct(ExpressionParser.ProductContext context) {
        Expression result = visit(context.primary(0));
        
        for (int i = 1; i < context.primary().size(); i++) {
            Expression right = visit(context.primary(i));
            result = new ProductExpression(result, right);
        }
        
        return result;
    }
    
    @Override
    public Expression visitPrimary(ExpressionParser.PrimaryContext context) {
        if (context.NUMBER() != null) {
            return new NumberExpression(Double.parseDouble(context.NUMBER().getText()));
        } else if (context.VARIABLE() != null) {
            return new VariableExpression(context.VARIABLE().getText());
        } else {
            return visit(context.expression());
        }
    }
}