package parser;

import javafx.scene.control.TextArea;
import lexicalAnalysisPackage.Lexer;
import lexicalAnalysisPackage.Token;
import lexicalAnalysisPackage.Token.Kind;
import mainCalculatorPackage.CalculatorValue;
import programManagementPackage.ProgramsUserInterface;
import java.util.Scanner;
import java.util.Stack;

public class ExpressionTreeBuilderEvaluator {
    /*
      <p>
      Title: ExpressionTreeBuilderEvaluator - A demonstration of how to build and evaluate a tree
      </p>

      <p>
      Description: A controller object class that implements an expression parser, tree builder,
      and tree evaluator functions
      </p>

      <p>
      Copyright: Copyright © 2019
      </p>

      @author Lynn Robert Carter
     * @version 1.00    Baseline version 2019-03-10
     * @author JSGREWAL
     * @version 2.00 Baseline Version 2019-03-31 Integrated with programmable calculator
     * @version 3.00 2019-05-05 Updated to support input and print statements.
     */

    /**********************************************************************************************
     *
     * The ExpressionTreeBuilderEvaluator Class provides the functions that parses an input stream
     * of lexical tokens, uses the structure to build an expression tree, and then uses that tree
     * to compute the value of the expression.
     *
     * The following are the primary followed by the secondary attributes for this Class
     */

    // The following String is the input for this demonstration
    //	private static String theExpression = "2-5*6+3.14159";
    private String theExpression = "";

    private Lexer lexer;

    private Token current;
    private Token next;

    // The following are the stacks that are used to transform the parse output into a tree
    private Stack<ExprNode> exprStack = new Stack<>();
    private Stack<Token> opStack = new Stack<>();

    /***
     * The Driver Method: Gets called by User-Interface, does the computations and returns the results to the UI Layer
     * @param exp the Expression
     * @param consoleArea_run Run Console
     * @param consoleArea_debug Debug Console
     * @return result The Result
     */
    public String solveExpression(String exp, TextArea consoleArea_run, TextArea consoleArea_debug) {

        theExpression = exp.trim();
        consoleArea_run.appendText("\n");
        consoleArea_run.appendText(String.format("The expression is: %s\n", theExpression));
        // The following are the attributes that support the scanning and lexing of the input
        Scanner theReader = new Scanner(theExpression);
        // Set up the Scanner and the Lexer
        lexer = new Lexer(theReader);
        current = lexer.accept();
        next = lexer.accept();
        /*
         * RESERVED WORD IDENTIFIED: print
         */
        if ((current.getTokenKind() == Kind.RESERVED_WORD)
                && (current.getTokenCode() == 2)) {
            current = next;
            next = lexer.accept();
            String stringToPrint = theExpression.substring(5);
            consoleArea_run.appendText(String.format("The output of the print statement is: %s",
                    stringToPrint));

        }
        /*
         * RESERVED WORD IDENTIFIED: input
         */
        if ((current.getTokenKind() == Kind.RESERVED_WORD)
                && (current.getTokenCode() == 3)) {
            current = next;
            next = lexer.accept();
            consoleArea_run.appendText("Awaiting user-input...\n");
            ProgramsUserInterface.readyToTakeInput();
            ProgramsUserInterface.inputIsAwaited = true;

        }

        CalculatorValue result = new CalculatorValue();
        // Invoke the parser and the tree builder
        boolean isValid = addSubExpr(consoleArea_debug);
        consoleArea_debug.appendText(String.format("\nThe expression is valid: %s\n", isValid));
        if (isValid) {
            // Display the expression tree
            ExprNode theTree = exprStack.pop();
            consoleArea_debug.appendText(theTree.toString());

            // Evaluate the expression tree
            consoleArea_debug.appendText("\nThe evaluation of the tree:");
            result = new CalculatorValue(compute(theTree, consoleArea_debug), 0.00);
            consoleArea_run.appendText(String.format("\nThe resulting value is: %s", result.mvToString()));

        }
        return result.mvToString();
    }



    /**********
     * The addSub Expression method parses a sequence of expression elements that are added
     * together, subtracted from one another, or a blend of them.
     *
     * @return The method returns a boolean value indicating if the parse was successful
     */
    private boolean addSubExpr(TextArea consoleArea_debug) {
        consoleArea_debug.appendText(String.format("\n-------\nValidating the expression:" +
                " %s for multiplication and division operations...\n", theExpression));
        // The method assumes the input is a sequence of additive/subtractive elements separated
        // by addition and/or subtraction operators
        if (mpyDivExpr(consoleArea_debug)) {
            consoleArea_debug.appendText("VALIDATION SUCCESSFUL!");
            consoleArea_debug.appendText(String.format("\n-------\nValidating the expression: " +
                    "%s for addition and subtraction operations.\n", theExpression));
            // Once an additive/subtractive element has been found, it can be followed by a
            // sequence of addition or subtraction operators followed by another
            // additive/subtractive element.  Therefore we start by looking for a "+" or a "-"
            while ((current.getTokenKind() == Kind.SYMBOL) &&
                    ((current.getTokenCode() == 6) ||        // The "+" operator
                            (current.getTokenCode() == 7))) {        // The "-" operator

                // When you find a "+" or a "-", push it onto the operator stack
                opStack.push(current);

                // Advance to the next input token
                current = next;
                next = lexer.accept();

                // Look for the next additive/subtractive element
                if (mpyDivExpr(consoleArea_debug)) {

                    // If one is found, pop the two operands and the operator
                    ExprNode expr2 = exprStack.pop();
                    ExprNode expr1 = exprStack.pop();
                    Token oper = opStack.pop();

                    // Create an Expression Tree node from those three items and push it onto
                    // the expression stack
                    exprStack.push(new ExprNode(oper, true, expr1, expr2));
                    consoleArea_debug.appendText("VALIDATION SUCCESSFUL!");
                } else {

                    // If we get here, we saw a "+" or a "-", but it was not followed by a valid
                    // additive/subtractive element
                    consoleArea_debug.appendText("VALIDATION FAILED!!!\n");
                    consoleArea_debug.appendText("A required additive/subtractive element was not found!!!\n " +
                            "Suggestion: Please make sure that there is a valid variable or a number on " +
                            "both sides of the operator.");
                    return false;
                }
            }

            // Reaching this point indicates that we have processed the sequence of
            // additive/subtractive elements
            return true;
        } else

            // This indicates that the first thing found was not an additive/subtractive element
            return false;
    }

    /**********
     * The mpyDiv Expression method parses a sequence of expression elements that are multiplied
     * together, divided from one another, or a blend of them.
     *
     * @return The method returns a boolean value indicating if the parse was successful
     */
    private boolean mpyDivExpr(TextArea consoleArea_debug) {

        // The method assumes the input is a sequence of terms separated by multiplication and/or
        // division operators
        if (term()) {

            // Once an multiplication/division element has been found, it can be followed by a
            // sequence of multiplication or division operators followed by another
            // multiplication/division element.  Therefore we start by looking for a "*" or a "/"
            while ((current.getTokenKind() == Kind.SYMBOL) &&
                    ((current.getTokenCode() == 8) ||        // The "*" operator
                            (current.getTokenCode() == 9))) {        // The "/" operator

                // When you find a "*" or a "/", push it onto the operator stack
                opStack.push(current);

                // Advance to the next input token
                current = next;
                next = lexer.accept();

                // Look for the next multiplication/division element
                if (term()) {

                    // If one is found, pop the two operands and the operator
                    ExprNode expr2 = exprStack.pop();
                    ExprNode expr1 = exprStack.pop();
                    Token oper = opStack.pop();

                    // Create an Expression Tree node from those three items and push it onto
                    // the expression stack
                    exprStack.push(new ExprNode(oper, true, expr1, expr2));

                } else {

                    // If we get here, we saw a "*" or a "/", but it was not followed by a valid
                    // multiplication/division element
                    consoleArea_debug.appendText("VALIDATION FAILED!");
                    consoleArea_debug.appendText("An operand is missing for the binary operation!!!\n Suggestion: " +
                            "Please make sure that there are valid variable or number on the both sides of operator.");

                    return false;
                }
            }

            // Reaching this point indicates that we have processed the sequence of
            // additive/subtractive elements
            return true;
        } else

            // This indicates that the first thing found was not a multiplication/division element
            return false;
    }

    /**********
     * The term Expression method parses constants.
     *
     * @return The method returns a boolean value indicating if the parse was successful
     */
    private boolean term() {

        // Parse the term
        if (current.getTokenKind() == Kind.FLOAT ||
                current.getTokenKind() == Kind.INTEGER) {

            // When you find one, push a corresponding expression tree node onto the stack
            exprStack.push(new ExprNode(current, false, null, null));

            // Advance to the next input token
            current = next;
            next = lexer.accept();

            // Signal that the term was found
            return true;
        } else

            // Signal that a term was not found
            return false;
    }

    /**********
     * The compute method is passed a tree as an input parameter and computes the value of the
     * tree based on the operator nodes and the value node in the tree.  Precedence is encoded
     * into the tree structure, so there is no need to deal with it during the evaluation.
     *
     * @param r - The input parameter of the expression tree
     *
     * @return  - A double value of the result of evaluating the expression tree
     */
    private double compute(ExprNode r, TextArea consoleArea_debug) {

        // Check to see if this expression tree node is an operator.
        if ((r.getOp().getTokenKind() == Kind.SYMBOL) && ((r.getOp().getTokenCode() == 6) ||
                (r.getOp().getTokenCode() == 7) || (r.getOp().getTokenCode() == 8) ||
                (r.getOp().getTokenCode() == 9))) {

            // if so, fetch the left and right sub-tree references and evaluate them
            double leftValue = compute(r.getLeft(), consoleArea_debug);
            double rightValue = compute(r.getRight(), consoleArea_debug);

            CalculatorValue cv_left = new CalculatorValue(leftValue, 0.00);
            CalculatorValue cv_right = new CalculatorValue(rightValue, 0.00);
            CalculatorValue cv_result = new CalculatorValue(cv_left);
            // Give the value for the left and the right sub-trees, use the operator code
            // to select the correct operation
            //			double result =0;
            switch ((int) r.getOp().getTokenCode()) {
                case 6:
                    cv_result.add(cv_right);
                    break;
                case 7:

                    cv_result.sub(cv_right);

                    break;
                case 8:

                    cv_result.mpy(cv_right);

                    break;
                case 9:
                    cv_result.div(cv_right);

                    break;
            }

            // Display the actual computation working from the leaves up to the root
            consoleArea_debug.appendText(" \n  " + cv_result.mvToString() + " = " + cv_left.mvToString() + r.getOp().getTokenText() + cv_right.mvToString());
            // Return the result to the caller
            return Double.parseDouble(cv_result.mvToString());
        }
        // If the node is not an operator, determine what it is and fetch the value
        else if (r.getOp().getTokenKind() == Kind.INTEGER) {
            Scanner convertInteger = new Scanner(r.getOp().getTokenText());
            double result = convertInteger.nextDouble();
            convertInteger.close();
            return result;
        } else if (r.getOp().getTokenKind() == Kind.FLOAT) {
            Scanner convertFloat = new Scanner(r.getOp().getTokenText());
            double result = convertFloat.nextDouble();
            convertFloat.close();
            return result;
        }
        // If it is not a recognized element, treat it as a value of zero
        else return 0.0;
    }


}
