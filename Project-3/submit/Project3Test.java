import itsc2214.*;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Project 3 Student Test Suite
 * ITSC 2214 Fall 2026
 * 
 * Provides comprehensive unit testing for all methods in the Project3 class,
 * including parenthetical balance checks, infix-to-postfix conversion, postfix
 * evaluation,
 * infix evaluation, and exception handling (such as division by zero and
 * unbalanced parentheses).
 * 
 * @author Ian Williams
 * @version Oct 07, 2026
 */
public class Project3Test {
    private ExpressionEvaluator runner = null;

    /**
     * setup() method, runs before each of your test methods.
     * Initializes the runner instance for testing.
     */
    @Before
    public void setup() {
        runner = new Project3();
    }

    /**
     * Test balancedParenthesis() with various valid and invalid parentheses
     * configurations.
     */
    @Test
    public void testBalancedParenthesis() {
        // Valid expressions
        assertTrue("() should be valid", runner.balancedParenthesis("()"));
        assertTrue("(()) should be valid", runner.balancedParenthesis("(())"));
        assertTrue("10 + (20 * 5) should be valid", runner.balancedParenthesis("10 + (20 * 5)"));

        // Invalid expressions
        assertFalse("()) should not be balanced", runner.balancedParenthesis("())"));
        assertFalse(")( should not be balanced", runner.balancedParenthesis(")(="));
        assertFalse("( should not be balanced", runner.balancedParenthesis("("));
        assertFalse(") should not be balanced", runner.balancedParenthesis(")"));
        assertFalse("(10 + 20 should not be balanced", runner.balancedParenthesis("(10 + 20"));
        assertFalse("10 * 20) should not be balanced", runner.balancedParenthesis("10 * 20)"));
    }

    /**
     * Test infix2Postfix() with different operators, operands, and precedence
     * structures.
     */
    @Test
    public void testInfixToPostfix() {
        // Simple addition
        QueueADT<String> q = runner.infix2Postfix("10 + 20");
        assertEquals(3, q.size());
        assertEquals("10", q.dequeue());
        assertEquals("20", q.dequeue());
        assertEquals("+", q.dequeue());
        assertTrue(q.isEmpty());

        // Expression without parenthesis (matches instructor's algorithm: 10 + 20 * 5
        // -> 10 20 + 5 *)
        QueueADT<String> q2 = runner.infix2Postfix("10 + 20 * 5");
        assertEquals("10", q2.dequeue());
        assertEquals("20", q2.dequeue());
        assertEquals("+", q2.dequeue());
        assertEquals("5", q2.dequeue());
        assertEquals("*", q2.dequeue());

        // Parentheses grouping precedence test: 10 + (20 * 5) -> 10 20 5 * +
        QueueADT<String> q3 = runner.infix2Postfix("10 + (20 * 5)");
        assertEquals("10", q3.dequeue());
        assertEquals("20", q3.dequeue());
        assertEquals("5", q3.dequeue());
        assertEquals("*", q3.dequeue());
        assertEquals("+", q3.dequeue());
    }

    /**
     * Test evaluatePostfix() with correct postfix queues and operations.
     */
    @Test
    public void testEvaluatePostfix() {
        QueueADT<String> q = runner.infix2Postfix("10 + 20");
        assertEquals(30, runner.evaluatePostfix(q));

        q = runner.infix2Postfix("10 + 20 * 2");
        assertEquals(60, runner.evaluatePostfix(q));

        q = runner.infix2Postfix("10 + (20 * 2)");
        assertEquals(50, runner.evaluatePostfix(q));

        q = runner.infix2Postfix("100 - 20 / 2");
        assertEquals(40, runner.evaluatePostfix(q));
    }

    /**
     * Test that evaluatePostfix throws an ArithmeticException upon division by
     * zero.
     */
    @Test(expected = ArithmeticException.class)
    public void testEvaluatePostfixDivisionByZero() {
        QueueADT<String> q = runner.infix2Postfix("10 / 0");
        runner.evaluatePostfix(q);
    }

    /**
     * Test evaluateInfix() with complete infix expressions.
     */
    @Test
    public void testEvaluateInfix() {
        assertEquals(30, runner.evaluateInfix("10 + 20"));
        assertEquals(60, runner.evaluateInfix("10 + 20 * 2"));
        assertEquals(50, runner.evaluateInfix("10 + (20 * 2)"));
        assertEquals(15, runner.evaluateInfix("(10 + 20) / 2"));
    }

    /**
     * Test that evaluateInfix throws an IllegalArgumentException for unbalanced
     * parentheses.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEvaluateInfixUnbalancedParenthesis() {
        runner.evaluateInfix("10 + (20 * 2");
    }

    /**
     * Test that evaluateInfix throws an ArithmeticException when division by zero
     * is evaluated.
     */
    @Test(expected = ArithmeticException.class)
    public void testEvaluateInfixDivisionByZero() {
        runner.evaluateInfix("50 / (5 - 5)");
    }
}