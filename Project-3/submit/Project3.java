import java.util.Arrays;
import itsc2214.*;

/**
 * Project 3 Setup - Expression Evaluator
 * ITSC 2214 Fall 2026
 * 
 * Implements the ExpressionEvaluator interface to handle parenthesis checking,
 * infix-to-postfix conversion, and postfix/infix expression evaluation.
 * 
 * @author Ian Williams
 */
public class Project3 implements ExpressionEvaluator {

    /**
     * Parses an expression in a string into an array of string tokens by removing
     * all whitespace and splitting operands from operators and parentheses.
     * 
     * @param arg0 the raw expression string to tokenize
     * @return an array of string tokens representing the expression components
     */
    public String[] tokenize(String arg0) {
        String expr = arg0.replaceAll("\\s+", ""); // remove whitespace first
        String[] tokens = expr.split("(?<=[-+*/()])|(?=[-+*/()])");
        return Arrays.stream(tokens)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
    }

    /**
     * Checks whether the parentheses in the given expression are valid and properly
     * balanced.
     * Uses a stack to track opening and closing parentheses.
     * 
     * @param expr the expression string to check
     * @return true if all parentheses are balanced and valid, false otherwise
     */
    @Override
    public boolean balancedParenthesis(String expr) {
        StackArrayList<String> pubStack = new StackArrayList<String>();
        String[] exp = tokenize(expr);

        for (int i = 0; i < exp.length; i++) {
            String token = exp[i];
            // If a left parenthesis is encountered, push it onto the stack
            if (token.equals("(")) {
                pubStack.push(token);
            }
            // If a right parenthesis is encountered, check for matching opening parenthesis
            else if (token.equals(")")) {
                if (pubStack.size() == 0) {
                    return false;
                } else {
                    pubStack.pop();
                }
            }
        }
        // If stack is empty, all parentheses matched properly
        return pubStack.size() == 0;
    }

    /**
     * Converts an infix expression string into a postfix notation queue (Reverse
     * Polish Notation),
     * removing all parentheses while preserving the correct order of evaluation.
     * 
     * @param expr the infix expression string to convert
     * @return a QueueADT containing the postfix expression tokens
     */
    @Override
    public QueueADT<String> infix2Postfix(String expr) {
        QueueArrayList<String> operand = new QueueArrayList<String>();
        StackArrayList<String> operator = new StackArrayList<String>();

        String[] exp = tokenize(expr);

        for (int i = 0; i < exp.length; i++) {
            String token = exp[i];

            // If the token is an operand (number), add it directly to the queue
            if (token.matches("\\d+")) {
                operand.enqueue(token);
            }
            // If the token is a left parenthesis, push it onto the operator stack
            else if (token.equals("(")) {
                operator.push("(");
            }
            // If the token is a right parenthesis, pop operators to queue until matching
            // left parenthesis
            else if (token.equals(")")) {
                while (operator.size() != 0 && !operator.peek().equals("(")) {
                    operand.enqueue(operator.peek());
                    operator.pop();
                }
                if (operator.size() != 0 && operator.peek().equals("(")) {
                    operator.pop();
                }
            }
            // If the token is an operator (+, -, *, /), handle precedence and operator
            // stack
            else {
                while (operator.size() != 0 && !operator.peek().equals("(")) {
                    operand.enqueue(operator.peek());
                    operator.pop();
                }
                operator.push(token);
            }
        }

        // Pop any remaining operators from the stack into the queue
        while (operator.size() != 0) {
            operand.enqueue(operator.peek());
            operator.pop();
        }
        return operand;
    }

    /**
     * Evaluates a postfix expression stored in a queue using a stack.
     * 
     * @param queue the QueueADT containing the postfix expression tokens
     * @return the integer result of the evaluated expression
     * @throws ArithmeticException      if division by zero occurs
     * @throws IllegalArgumentException if there are syntax errors or unknown
     *                                  operators
     */
    @Override
    public int evaluatePostfix(QueueADT<String> queue) {
        String token = null;
        StackArrayList<String> parentStack = new StackArrayList<String>();

        while (queue.size() != 0) {
            token = queue.peek();
            queue.dequeue();

            // If operand, push onto stack
            if (token.matches("\\d+")) {
                parentStack.push(token);
            }
            // If operator, pop two operands and apply operation
            else {
                int result = 0;
                if (parentStack.size() >= 2) {
                    int operand2 = Integer.parseInt(parentStack.peek());
                    parentStack.pop();
                    int operand1 = Integer.parseInt(parentStack.peek());
                    parentStack.pop();

                    if (token.equals("+")) {
                        result = operand1 + operand2;
                    } else if (token.equals("-")) {
                        result = operand1 - operand2;
                    } else if (token.equals("*")) {
                        result = operand1 * operand2;
                    } else if (token.equals("/")) {
                        if (operand2 == 0) {
                            throw new ArithmeticException("Division by zero");
                        }
                        result = operand1 / operand2;
                    } else {
                        throw new IllegalArgumentException("Unknown operator: " + token);
                    }
                    parentStack.push(Integer.toString(result));
                } else {
                    throw new IllegalArgumentException("Extra Operator/Missing Operand");
                }
            }
        }

        if (parentStack.size() != 1) {
            throw new IllegalArgumentException("Invalid postfix expression structure");
        }
        return Integer.parseInt(parentStack.peek());
    }

    /**
     * Evaluates an infix expression string by first checking for balanced
     * parentheses,
     * converting it to postfix notation, and then evaluating the postfix
     * expression.
     * 
     * @param arg0 the infix expression string to evaluate
     * @return the integer result of the evaluation
     * @throws ArithmeticException      if division by zero occurs
     * @throws IllegalArgumentException if parentheses are unbalanced or expression
     *                                  is malformed
     */
    @Override
    public int evaluateInfix(String arg0) throws ArithmeticException, IllegalArgumentException {
        // Check if parentheses are balanced before conversion and evaluation
        if (!balancedParenthesis(arg0)) {
            throw new IllegalArgumentException("Unbalanced parenthesis in expression");
        }
        QueueADT<String> postfixQueue = infix2Postfix(arg0);
        return evaluatePostfix(postfixQueue);
    }
}