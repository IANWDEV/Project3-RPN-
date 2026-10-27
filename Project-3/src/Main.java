import itsc2214.*;

/**
 * Sample main driver, you do NOT need to submit this to web-cat.
 */
public class Main {
    public static void main(String[] args) {
        PackageInfo.printInfo();

        doExpression("10 + (20 * 3) / 2");
        // doExpression("(10 + 20 * 2)");
        // doExpression(")10 + 20)");
        // doExpression("(10 + 20");
        // doExpression("(10 + (20))");
    }

    private static String queueToString(QueueADT<String> q) {
        StringBuffer out = new StringBuffer();
        int k = q.size();
        while (k > 0) {
            String t = q.dequeue();
            out.append(t);
            out.append(" ");
            q.enqueue(t); // put it back in the queue
            k--;
        }
        return out.toString().trim();
    }

    public static void printQ(QueueADT<String> q) {
        System.out.println(queueToString(q));
    }

    public static void doExpression(String str) {
        ExpressionEvaluator expr = new Project3();
        System.out.println("\n\nInfix: " + str);
        boolean valid = expr.balancedParenthesis(str);
        if (valid) {
            System.out.println("Valid parenthesis");
            QueueADT<String> queue = expr.infix2Postfix(str);
            System.out.print("Postfix: ");
            printQ(queue);
            int r = expr.evaluatePostfix(queue);
            System.out.println("result = " + r);
        } else
            System.out.println("Not valid parenthesis");
    }
}