package com.calculator; // This file belongs to the 'com.calculator' package.

import com.calculator.ui.CalculatorUI; // We need to import the CalculatorUI class from its package.
import javax.swing.SwingUtilities; // We need this to safely create the UI.

// This is the main class that starts our whole program.
public class Main { 
    // This 'main' method is the entry point for Java.
    public static void main(String[] args) { 
        
        // This makes sure our window (JFrame) is created on a safe thread.
        SwingUtilities.invokeLater(new Runnable() { 
            public void run() {
                // This line creates our calculator window.
                // OOP Concept: This is creating an 'Object' (an instance) of our CalculatorUI class.
                new CalculatorUI(); 
            }
        });
    }
}

