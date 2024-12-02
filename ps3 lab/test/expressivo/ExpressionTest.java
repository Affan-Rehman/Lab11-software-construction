package expressivo;

import static org.junit.Assert.*;
import org.junit.Test;

public class ExpressionTest {

    @Test
    public void testBasicSumExpression() {
        Expression expr = new SumExpression(new NumberExpression(3), new NumberExpression(4));
        assertEquals("3.0 + 4.0", expr.toString());
    }

    @Test
    public void testBasicProductExpression() {
        Expression expr = new ProductExpression(new NumberExpression(5), new NumberExpression(6));
        assertEquals("5.0 * 6.0", expr.toString());
    }

    @Test
    public void testNestedExpressions() {
        // Test nested sum and product expressions
        Expression inner = new SumExpression(new NumberExpression(2), new NumberExpression(3));
        Expression outer = new ProductExpression(inner, new NumberExpression(4));
        assertEquals("(2.0 + 3.0) * 4.0", outer.toString());
    }

    @Test
    public void testComplexNestedExpressions() {
        // Test more complex nested expressions
        Expression expr1 = new SumExpression(new NumberExpression(1), new NumberExpression(2));
        Expression expr2 = new ProductExpression(new NumberExpression(3), new NumberExpression(4));
        Expression combined = new SumExpression(expr1, expr2);
        assertEquals("(1.0 + 2.0) + (3.0 * 4.0)", combined.toString());
    }

    @Test
    public void testExpressionEquality() {
        Expression expr1 = new SumExpression(new NumberExpression(3), new NumberExpression(4));
        Expression expr2 = new SumExpression(new NumberExpression(3), new NumberExpression(4));
        Expression expr3 = new SumExpression(new NumberExpression(4), new NumberExpression(3));

        assertTrue(expr1.equals(expr2));  // Same structure and values
        assertFalse(expr1.equals(expr3)); // Different order
        assertFalse(expr1.equals(null));  // Null comparison
        assertTrue(expr1.equals(expr1));   // Self comparison
    }

    @Test
    public void testHashCodeConsistency() {
        Expression expr1 = new SumExpression(new NumberExpression(3), new NumberExpression(4));
        Expression expr2 = new SumExpression(new NumberExpression(3), new NumberExpression(4));
        
        assertEquals(expr1.hashCode(), expr2.hashCode());
        assertEquals(expr1.hashCode(), expr1.hashCode()); // Multiple invocations
    }

    @Test
    public void testZeroAndOneProperties() {
        Expression zero = new NumberExpression(0);
        Expression one = new NumberExpression(1);
        Expression num = new NumberExpression(5);

        Expression sumWithZero = new SumExpression(num, zero);
        Expression productWithOne = new ProductExpression(num, one);

        assertEquals("5.0 + 0.0", sumWithZero.toString());
        assertEquals("5.0 * 1.0", productWithOne.toString());
    }

    @Test
    public void testCommutativeProperty() {
        Expression expr1 = new SumExpression(new NumberExpression(2), new NumberExpression(3));
        Expression expr2 = new SumExpression(new NumberExpression(3), new NumberExpression(2));
        Expression prod1 = new ProductExpression(new NumberExpression(2), new NumberExpression(3));
        Expression prod2 = new ProductExpression(new NumberExpression(3), new NumberExpression(2));

        // Note: This test might fail if your implementation considers order important
        assertEquals(expr1.toString().replace(" ", ""), 
                    expr2.toString().replace(" ", ""));
        assertEquals(prod1.toString().replace(" ", ""), 
                    prod2.toString().replace(" ", ""));
    }

    @Test
    public void testDeepNesting() {
        Expression expr = new ProductExpression(
            new SumExpression(
                new ProductExpression(new NumberExpression(1), new NumberExpression(2)),
                new NumberExpression(3)
            ),
            new SumExpression(
                new NumberExpression(4),
                new NumberExpression(5)
            )
        );
        assertEquals("((1.0 * 2.0) + 3.0) * (4.0 + 5.0)", expr.toString());
    }

    @Test
    public void testEdgeCases() {
        // Test with very large numbers
        Expression largeNum = new NumberExpression(Double.MAX_VALUE);
        Expression sum = new SumExpression(largeNum, new NumberExpression(1));
        assertTrue(sum.toString().contains(String.valueOf(Double.MAX_VALUE)));
        
        // Test with negative numbers
        Expression negNum = new NumberExpression(-5);
        Expression prod = new ProductExpression(negNum, new NumberExpression(3));
        assertEquals("-5.0 * 3.0", prod.toString());
    }

    @Test
    public void testParseSimpleNumber() {
        Expression expr = Expression.parse("3");
        assertEquals("3.0", expr.toString());
    }

    @Test
    public void testParseSimpleAddition() {
        Expression expr = Expression.parse("3 + 2.4");
        assertEquals("3.0 + 2.4", expr.toString());
    }

    @Test
    public void testParseWithVariable() {
        Expression expr = Expression.parse("3 * x + 2.4");
        assertEquals("3.0 * x + 2.4", expr.toString());
    }

    @Test
    public void testParseWithParentheses() {
        Expression expr = Expression.parse("3 * (x + 2.4)");
        assertEquals("3.0 * (x + 2.4)", expr.toString());
    }

    @Test
    public void testParseComplexExpression() {
        Expression expr = Expression.parse("((3 + 4) * x * x)");
        assertEquals("((3.0 + 4.0) * x * x)", expr.toString());
    }

    @Test
    public void testParseMultipleVariables() {
        Expression expr = Expression.parse("foo + bar + baz");
        assertEquals("foo + bar + baz", expr.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseIncompleteExpression() {
        Expression.parse("3 *");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseUnclosedParentheses() {
        Expression.parse("( 3");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseInvalidSpacing() {
        Expression.parse("3 x");
    }
}
