package com.calculator.engine; // This file belongs to the 'com.calculator.engine' package.

import java.util.ArrayList; // We need ArrayList to build our 'stack' data structure.
import java.util.Arrays; // We need this for a helper list of trig functions.

// This is the 'brain' of our calculator. It does all the math.
// OOP Concept: This class is a good example of Abstraction and Encapsulation.
// It hides all complex logic from the UI.
public class CalculatorEngine { 

    // --- Calculation State ---
    // All these variables are 'private', so only this class can access them directly.
    // OOP Concept: This is Encapsulation.
    private double varA, varB, varC, varD, varE, varF, varX; // For STO/RCL memory.
    private double varM = 0.0; // For M+ memory.
    private String lastAnswer = "0"; // For the 'Ans' button.
    private boolean isDegrees = true; // To track DEG or RAD mode.

    // --- Public Methods (API for the Engine) ---
    // These 'public' methods are the *only* way the UI can interact with this class.

    // A 'setter' method to change the private 'isDegrees' variable.
    // OOP Concept: This is part of Encapsulation.
    public void setDegrees(boolean isDegrees) { 
        this.isDegrees = isDegrees; // 'this' refers to the current object.
    }

    // A 'getter' method to read the private 'isDegrees' variable.
    // OOP Concept: This is also part of Encapsulation.
    public boolean isDegrees() { 
        return this.isDegrees;
    }

    // Setter for 'lastAnswer'.
    public void setLastAnswer(String lastAnswer) { 
        this.lastAnswer = lastAnswer;
    }

    // Getter for 'varM'.
    public double getM() { 
        return this.varM;
    }

    // Setter for 'varM'.
    public void setM(double m) { 
        this.varM = m;
    }
    
    // This public method lets the UI store a value in one of our private variables.
    public void storeVariable(String varName, double value) { 
        switch (varName) { 
            case "A": this.varA = value; break; // 'this.varA' refers to the private field.
            case "B": this.varB = value; break;
            case "C": this.varC = value; break;
            case "D": this.varD = value; break;
            case "E": this.varE = value; break;
            case "F": this.varF = value; break;
            case "X": this.varX = value; break;
            case "M": this.varM = value; break;
        }
    }

    // --- ArrayList-based Stack helper methods ---
    // These methods are 'private', so the UI can't see them.
    // OOP Concept: This is Abstraction. We are hiding *how* our stack works.
    
    // A 'generic' push method (works for any type 'T').
    private <T> void push(ArrayList<T> list, T item) { 
        list.add(item); // Add the item to the end of the list.
    }
    
    // A 'generic' pop method.
    private <T> T pop(ArrayList<T> list) { 
        if (list.isEmpty()) throw new RuntimeException("Stack underflow"); // Safety check.
        return list.remove(list.size() - 1); // Remove and return the last item.
    }
    
    // A 'generic' peek method.
    private <T> T peek(ArrayList<T> list) { 
        if (list.isEmpty()) throw new RuntimeException("Stack is empty"); // Safety check.
        return list.get(list.size() - 1); // Return the last item (but don't remove it).
    }
    // --- End of ArrayList-based Stack helpers ---

    // This is the main public method the UI calls to get a result.
    // OOP Concept: Abstraction. The UI doesn't know *how* we evaluate, just that we *do*.
    public double evaluate(String expression) { 
        // Step 1: Replace all variable names with their stored values.
        expression = expression.replaceAll("A", "(" + this.varA + ")");
        expression = expression.replaceAll("B", "(" + this.varB + ")");
        // ... (rest of variable replacements) ...
        expression = expression.replaceAll("C", "(" + this.varC + ")");
        expression = expression.replaceAll("D", "(" + this.varD + ")");
        expression = expression.replaceAll("E", "(" + this.varE + ")");
        expression = expression.replaceAll("F", "(" + this.varF + ")");
        expression = expression.replaceAll("X", "(" + this.varX + ")");
        expression = expression.replaceAll("M", "(" + this.varM + ")");

        expression = expression.replaceAll("Ans", "(" + this.lastAnswer + ")");
        expression = expression.replaceAll("π", String.valueOf(Math.PI));
        expression = expression.replaceAll("e", String.valueOf(Math.E));
        expression = expression.replaceAll("√", "sqrt"); // Change '√' to "sqrt"
        
        // Step 2: Add implied multiplication (e.g., "5sin" -> "5*sin")
        expression = expression.replaceAll("(\\d)([a-zA-Z(])", "$1*$2"); 
        expression = expression.replaceAll("(\\))(\\d)", "$1*$2"); 
        expression = expression.replaceAll("(\\))([a-zA-Z(])", "$1*$2"); 
        
        char[] tokens = expression.toCharArray(); // Convert the string into an array of characters.
        
        // We use two ArrayLists as 'stacks' to do the math.
        ArrayList<Double> values = new ArrayList<Double>(); // One stack for numbers.
        ArrayList<Object> ops = new ArrayList<Object>(); // One stack for operators ('+' or "sin").

        // Step 3: Loop through the expression (This is the Shunting-Yard algorithm).
        for (int i = 0; i < tokens.length; i++) { 
            if (tokens[i] == ' ') continue; // Skip spaces.
            
            // If it's a number (or a decimal point)...
            if ((tokens[i] >= '0' && tokens[i] <= '9') || tokens[i] == '.') {
                StringBuilder sbuf = new StringBuilder(); // Build the full number string.
                while (i < tokens.length && ((tokens[i] >= '0' && tokens[i] <= '9') || tokens[i] == '.')) {
                    sbuf.append(tokens[i++]);
                }
                i--; // Go back one character (we went one too far).
                push(values, Double.parseDouble(sbuf.toString())); // Add the number to the 'values' stack.
            } 
            // If it's a letter (start of a function like "sin" or "sqrt")...
            else if (Character.isLetter(tokens[i])) { 
                StringBuilder sbuf = new StringBuilder(); // Build the function name.
                while (i < tokens.length && Character.isLetter(tokens[i])) {
                    sbuf.append(tokens[i++]);
                }
                i--;
                push(ops, sbuf.toString()); // Add the function name to the 'ops' stack.
            } 
            // If it's an open parenthesis...
            else if (tokens[i] == '(') { 
                push(ops, tokens[i]); // Push it onto the 'ops' stack.
            } 
            // If it's a close parenthesis...
            else if (tokens[i] == ')') { 
                // Solve everything inside the parentheses.
                while (!ops.isEmpty() && !peek(ops).equals('(')) {
                    applyOp(ops, values);
                }
                if (!ops.isEmpty()) pop(ops); // Get rid of the '('.
                
                // If there was a function before the '(', (e.g., "sin(...)"), apply it now.
                if (!ops.isEmpty() && peek(ops) instanceof String) {
                    applyFunc(ops, values);
                }
            } 
            // If it's a math operator (+, -, *, /)...
            else { 
                // Handle negative numbers (e.g., "-5" or "(-5").
                if (tokens[i] == '-' && (i == 0 || "+-*/^(".indexOf(tokens[i-1]) != -1)) {
                    push(values, -1.0); // Push -1.
                    push(ops, '*'); // and a '*'.
                } else {
                    // While the operator on the stack has higher precedence, apply it.
                    while (!ops.isEmpty() && hasPrecedence(tokens[i], peek(ops))) {
                        applyOp(ops, values);
                    }
                    push(ops, tokens[i]); // Push the current operator.
                }
            }
        }
        
        // Step 4: After the loop, apply any remaining operations.
        while (!ops.isEmpty()) {
            applyOp(ops, values);
        }
        
        if(values.size() != 1) throw new RuntimeException("Syntax Error"); // Safety check.
        return pop(values); // The last number on the stack is our answer!
    }
    
    // This private helper applies an operator (like '+') to the top two numbers.
    private void applyOp(ArrayList<Object> ops, ArrayList<Double> values) { 
        Object op = pop(ops); // Get the operator (e.g., '+').
        if(op.equals('(')) throw new RuntimeException("Mismatched Parentheses");
        if(values.size() < 2) throw new RuntimeException("Syntax Error");
        double right = pop(values); // Get the right-hand number.
        double left = pop(values); // Get the left-hand number.
        char operator = (char) op; // Convert the operator object to a 'char'.
        
        // Do the math based on the operator.
        switch (operator) { 
            case '+': push(values, left + right); break;
            case '-': push(values, left - right); break;
            case '*': push(values, left * right); break;
            case '/':
                if (right == 0) throw new ArithmeticException("Division by Zero"); // Error check.
                push(values, left / right);
                break;
            case '^': push(values, Math.pow(left, right)); break;
        }
    }

    // This private helper applies a function (like 'sin') to the top number.
    private void applyFunc(ArrayList<Object> ops, ArrayList<Double> values) { 
        String f = (String) pop(ops); // Get the function name (e.g., "sin").
        if (values.isEmpty()) throw new RuntimeException("Syntax Error");
        double val = pop(values); // Get the number to apply the function to.
        double result;

        // Check if it's a basic trig function.
        boolean isAngular = Arrays.asList("sin", "cos", "tan").contains(f);
        // Check if it's an inverse trig function.
        boolean isInverseAngular = Arrays.asList("asin", "acos", "atan").contains(f);

        // OOP: Encapsulation. Check our private 'isDegrees' variable.
        if (this.isDegrees && isAngular) { 
            val = Math.toRadians(val); // Convert to radians before calculation.
        }

        // Do the math based on the function name.
        switch(f) { 
            case "sin": result = Math.sin(val); break;
            case "cos": result = Math.cos(val); break;
            case "tan": result = Math.tan(val); break;
            case "asin": result = Math.asin(val); break;
            case "acos": result = Math.acos(val); break;
            case "atan": result = Math.atan(val); break;
            case "sinh": result = Math.sinh(val); break;
            case "cosh": result = Math.cosh(val); break;
            case "tanh": result = Math.tanh(val); break;
            case "asinh": result = Math.log(val + Math.sqrt(val*val + 1.0)); break;
            case "acosh": result = Math.log(val + Math.sqrt(val*val - 1.0)); break;
            case "atanh": result = 0.5 * Math.log((1.0 + val) / (1.0 - val)); break;
            case "sqrt": result = Math.sqrt(val); break;
            case "log": result = Math.log10(val); break;
            case "ln": result = Math.log(val); break;
            default: throw new RuntimeException("Unknown function: " + f);
        }

        // If we are in degree mode, convert inverse trig results back to degrees.
        if (this.isDegrees && isInverseAngular) { 
            result = Math.toDegrees(result);
        }
        
        push(values, result); // Push the final result back onto the 'values' stack.
    }
    
    // This private helper checks operator precedence (e.g., '*' is higher than '+').
    private boolean hasPrecedence(char op1, Object op2) { 
        if (op2.equals('(') || op2.equals(')')) return false; // Parentheses have lowest precedence.
        if (op2 instanceof String) return false; // Functions (like "sin") have highest.
        
        char op2Char = (char) op2;
        // '^' (power) is highest.
        if ((op1 == '^') && (op2Char == '*' || op2Char == '/' || op2Char == '+' || op2Char == '-')) return false;
        // '*' and '/' are next.
        if ((op1 == '*' || op1 == '/') && (op2Char == '+' || op2Char == '-')) return false;
        // '+' and '-' are lowest.
        return true;
    }
}

