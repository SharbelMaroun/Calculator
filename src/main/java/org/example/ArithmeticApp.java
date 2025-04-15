package org.example;
import java.util.Stack;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ArithmeticApp {

    static String result = null; // Global variable for the final result
    /**
     * operate method to execute the calculator program.
     * Prompts the user for base and expression input, validates input,
     * and performs the calculations based on the specified base.
     *
     * @param rawExpression to get the received expression input.
     */
    public static String operate(String rawExpression, int base) {
        // Loop until a valid base is entered
        try {
            String expression = rawExpression.replaceAll("\\s+", "");  // Remove all spaces

            if (!expressionValidity(rawExpression, base)) { // Expression validity test
                result = "Error: invalid expression";
                return result;
            }

            String regex = "([0-9A-F]+|[+\\-*/^&|~])";
            // Compile the pattern and find matches
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(expression);

            Stack<String> operatorsStack = new Stack<>(); // Stack only for operators
            Stack<Integer> numbersStack = new Stack<>(); // Stack only for numbers

            while (matcher.find()) { // For every sub-string
                String token = matcher.group(1);

                // Ensure input is exactly 8 bits when its in base 2, and it's not operators or bitwise
                if (base == 2 && token.length() != 8 && !isOperator(token) && !isBitwise(token)) {
                    result = "Error: invalid expression";
                    return result;
                }

                // Waiting for the next operator to decide the precedence
                if (isOperator(token) || isBitwise(token)) {
                    while (!operatorsStack.isEmpty() && precedence(token) <= precedence(operatorsStack.peek())) {
                        if(!calculate(numbersStack, operatorsStack)) {// The previous numbers can be calculated
                            return result;
                        }
                    }
                    operatorsStack.push(token); // Push the most recent operator
                } else {
                    if (base == 2) {
                        if (token.charAt(0) == '1') { // If MSB = 1 indicates Negative number
                            numbersStack.push((Integer.parseInt(token, base)) - 256); // by 2's complement
                        } else {
                            numbersStack.push((Integer.parseInt(token, base)));
                        }
                    } else {
                        numbersStack.push(Integer.parseInt(token.toLowerCase(), base));
                    }
                }
            }

            // Process the remaining operators and operands on the stacks
            while (!operatorsStack.isEmpty()) {
                if(!calculate(numbersStack, operatorsStack)) {
                    return result;
                }
            }

            // Pop the result from the stack and store it in a variable
            int resultNum = numbersStack.pop();
            print(resultNum, base);
        }
        catch (Exception e){
            result = "Error: invalid expression";
            return result;
        }
        return result;
    }

    /**
     * Validates an expression based on the specified base.
     *
     * @param expression The mathematical expression to validate.
     * @param base       The base of the numerical system (2, 8, 10, 16).
     * @return true if the expression is valid, false otherwise.
     */
     protected static boolean expressionValidity(String expression, int base) {
        int[] binaryOperator = {-1}; // 0 for operators "+-*/" and 1 for bitwise
        char prevChar = '\0';

        Pattern pattern = Pattern.compile("\\d+\\s+\\d+");
        Matcher matcher = pattern.matcher(expression);
        if(matcher.find()) {
            return false;
        }
        // Loop through each character in the expression
        if((expression.isEmpty()) || ("0123456789ABCDEF- ".indexOf(expression.charAt(0)) == -1) || ("0123456789ABCDEF ".indexOf(expression.charAt(expression.length()-1)) == -1)){
            return false;
        }
        for (char c : expression.toCharArray()) {

            if(isOperator(String.valueOf(prevChar)) && isOperator(String.valueOf(c))){
                return false;
            }
            if(c == ' '){
                prevChar = c;
                continue;
            }
            switch (base) {
                case 2: // Binary (base 2)
                    if (!isBinaryCharacter(String.valueOf(c),binaryOperator)) {
                        return false;
                    }
                    break;

                case 8: // Octal (base 8)
                    if (!isOctalCharacter(String.valueOf(c))) {
                        return false;
                    }
                    break;

                case 10: // Decimal (base 10)
                    if (!isDecimalCharacter(String.valueOf(c))) {
                        return false;
                    }
                    break;

                case 16: // Hexadecimal (base 16)
                    if (!isHexCharacter(String.valueOf(c))) {
                        return false;
                    }
                    break;
            }
            prevChar = c;
        }
        // If all characters are valid
        return true;
    }

    /**
     * Checks if a token is a valid binary character.
     *
     * @param token The token to check.
     * @param binaryOperator Array to track operator type for binary expressions.
     * @return true if the token is a valid binary character, false otherwise.
     */
    private static boolean isBinaryCharacter(String token, int[] binaryOperator) {
        if(token.length() != 1){
            return false;
        }
        char c = token.charAt(0);
        // Combination of operators with bitwise
        if(isBitwise(String.valueOf(c)) && binaryOperator[0] == 0 || isOperator(String.valueOf(c)) && binaryOperator[0] == 1)
        {
            return false;
        }
        if(isBitwise(String.valueOf(c))){
            binaryOperator[0] = 1; // For bitwise
            return true;
        } else if(isOperator(String.valueOf(c))){
            binaryOperator[0] = 0; // For operators "+-*/"
            return true;
        }
        return c == '0' || c == '1' || isOperator(String.valueOf(c));
    }

    /**
     * Checks if a token is a valid octal character.
     *
     * @param token The token to check.
     * @return true if the token is a valid octal character, false otherwise.
     */
    private static boolean isOctalCharacter(String token) {
        // we are checking one character
        if(token.length() != 1){
            return false;
        }
        char c = token.charAt(0);
        return (c >= '0' && c <= '7') || isOperator(String.valueOf(c));
    }

    /**
     * Checks if a token is a valid decimal character.
     *
     * @param token The token to check.
     * @return true if the token is a valid decimal character, false otherwise.
     */
    private static boolean isDecimalCharacter(String token) {
        if(token.length() != 1){
            return false;
        }
        char c = token.charAt(0);
        return (c >= '0' && c <= '9') || isOperator(String.valueOf(c));
    }

    /**
     * Checks if a token is a valid hexadecimal character.
     *
     * @param token The token to check.
     * @return true if the token is a valid hexadecimal character, false otherwise.
     */
    private static boolean isHexCharacter(String token) {
        if(token.length() != 1){
            return false;
        }
        char c = token.charAt(0);
        return Character.isDigit(c) || (c >= 'A' && c <= 'F') || isOperator(String.valueOf(c));
    }

    /**
     * Checks if a token is an arithmetic operator.
     *
     * @param token The token to check.
     * @return true if the token is an operator, false otherwise.
     */
    private static boolean isOperator(String token) {
        if(token.length() != 1){
            return false;
        }
        char c = token.charAt(0);
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    /**
     * Checks if a token is a bitwise operator.
     *
     * @param token The token to check.
     * @return true if the token is a bitwise operator, false otherwise.
     */
    public static boolean isBitwise(String token) {
        if(token.length() != 1){
            return false;
        }
        char c = token.charAt(0);
        return c == '&' || c == '|'  || c == '~' || c == '^';
    }

    /**
     * Determines the precedence of an operator.
     *
     * @param operator The operator whose precedence is to be determined.
     * @return The precedence level of the operator.
     */
    private static int precedence(String operator) {
        return switch (operator) {
            case "+", "-", "|" -> 1;
            case "*", "/", "^" -> 2;
            case "&" -> 3;
            case "~" -> 4;
            default -> 0;
        };
    }

    /**
     * Performs a calculation using the top elements of the number and operator stacks.
     *
     * @param numbersStack   The stack of numbers.
     * @param operatorsStack The stack of operators.
     */
    private static boolean calculate(Stack<Integer> numbersStack, Stack<String> operatorsStack) {

        int num2 = numbersStack.pop();
        int num1 = 0;
        String operator = operatorsStack.pop();
        // The case minus at the beginning of the expression
        if("/*-+&|^".contains(operator) && !numbersStack.isEmpty()){
            num1 = numbersStack.pop();
        }
        // implement the operators and bitwise
        switch (operator) {
            case "+":
                numbersStack.push(num1 + num2);
                break;
            case "-":
                numbersStack.push(num1 - num2);
                break;
            case "*":
                numbersStack.push(num1 * num2);
                break;
            case "/":
                if (num2 == 0) {
                    result = "Error: trying to divide by 0 (evaluated: \"0\")";
                    return false;
                }
                numbersStack.push(num1 / num2);
                break;
            case "~":
                numbersStack.push(~num2);
                break;
            case "&":
                numbersStack.push(num1 & num2);
                break;
            case "^":
                numbersStack.push(num1 ^ num2);
                break;
            case "|":
                numbersStack.push(num1 | num2);
                break;
        }
        return true;
    }

    /**
     * Prints the result of an arithmetic expression in the specified base (binary, octal, decimal, or hexadecimal).
     *
     * @param resultNum The evaluated result of the expression.
     * @param base The base in which to display the result (2, 8, 10, or 16).
     */
    public static void print(int resultNum, int base){
        switch(base){
            case 2: // Binary print
                // Track the number of overflows and underflows
                int overflowCount = 0;
                int underflowCount = 0;

                // Normalize the result by checking if it overflows or underflows
                while (resultNum > 127) { // Overflow
                    resultNum -= 256;  // Wrap around
                    overflowCount++;
                }

                while (resultNum < -128) { // Underflow
                    resultNum += 256;  // Wrap around
                    underflowCount++;
                }
                if(overflowCount % 2 == 1){
                    resultNum -= 256;
                } else if(underflowCount % 2 == 1){
                    resultNum += 256;
                }

                result = String.format("%8s", Integer.toBinaryString(resultNum & 0xFF)).replace(' ', '0');
                break;
            case 8: // Octal print
                if (resultNum < 0) {
                    result = ("-" + Integer.toOctalString(Math.abs(resultNum)));
                } else {
                    result = Integer.toOctalString(resultNum);
                }
                break;
            case 10: // Decimal print
                result = Integer.toString(resultNum);
                break;
            case 16: // Hexadecimal print
                if (resultNum < 0) {
                    result = ("-" + Integer.toHexString(-resultNum).toUpperCase());
                } else {
                    result = Integer.toHexString(resultNum).toUpperCase();
                }
                break;
        }
    }

    public static String convertExpression(String expression, int sourceBase, int targetBase) {
        // Split the expression into tokens (numbers and everything else)
        String[] tokens = expression.split("(?=[+\\-*/^|&~()])|(?<=[+\\-*/^|&~()])");

        StringBuilder result = new StringBuilder();

        for (String token : tokens) {
            token = token.trim(); // Remove any leading or trailing whitespace

            // Check if the token is a valid number in the source base
            if (isNumber(token, sourceBase)) {
                // Convert the number to the target base
                int decimalValue = Integer.parseInt(token, sourceBase);
                result.append(Integer.toString(decimalValue, targetBase).toUpperCase());
            } else {
                // Keep everything else unchanged
                result.append(token);
            }
        }

        return result.toString();
    }

    private static boolean isNumber(String token, int base) {
        if (token.isEmpty()) return false; // Ignore empty tokens
        try {
            Integer.parseInt(token, base);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}