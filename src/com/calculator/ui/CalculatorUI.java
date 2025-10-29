package com.calculator.ui; // This file belongs to the 'com.calculator.ui' package.

import com.calculator.engine.CalculatorEngine; // We need to import the 'engine' to do the math.

import javax.swing.*; // We need all the Swing components (buttons, text areas).
import java.awt.*; // We need AWT for layout, color, and font.
import java.awt.event.ActionEvent; // Represents a button click event.
import java.awt.event.ActionListener; // The 'listener' that waits for button clicks.
        
// This is our main UI class.
// OOP Concept: 'extends JFrame' means our class IS-A window (Inheritance).
// OOP Concept: 'implements ActionListener' means our class MUST handle button clicks (Polymorphism).
public class CalculatorUI extends JFrame implements ActionListener { 

    // --- UI Components ---
    private JTextArea display; // The main text area for numbers.
    private JLabel history; // The small label above the display.
    private JLabel modeIndicator; // The label for 'DEG' (degrees) or 'RAD' (radians).
    
    // --- UI State Variables ---
    // These booleans track the state of the SHIFT, ALPHA, etc., keys.
    private boolean isShiftActive = false; 
    private boolean isAlphaActive = false; 
    private boolean isHypActive = false; 
    private boolean isStoActive = false; 
    private boolean isRclActive = false; 
    
    // --- Engine ---
    private CalculatorEngine engine; // This will hold our calculator 'brain'.
    // OOP Concept: Our UI 'HAS-A' CalculatorEngine. This is called Composition.
    // OOP Concept: All the 'private' variables above are hidden from other classes. This is Encapsulation.

    // --- Color Scheme ---
    // These 'final' variables are constants for our theme colors.
    private final Color windowBgColor = new Color(40, 40, 40);
    private final Color calculatorBodyColor = new Color(25, 25, 25);
    private final Color displayBgColor = new Color(50, 60, 50);
    private final Color displayTextColor = Color.WHITE;
    private final Color buttonBgColor = new Color(60, 60, 60);
    private final Color buttonTextColor = Color.WHITE;
    private final Color onButtonColor = new Color(240, 100, 100);
    private final Color shiftButtonColor = new Color(255, 220, 130);
    private final Color alphaButtonColor = new Color(255, 182, 193);
    private final Color delButtonColor = new Color(130, 210, 130);
    private final Color acButtonColor = new Color(240, 160, 0);
    private final Color equalsButtonColor = new Color(180, 190, 210);

    // This is the constructor. It runs when 'new CalculatorUI()' is called.
    public CalculatorUI() { 
        this.engine = new CalculatorEngine(); // OOP: We create the 'engine' object.

        setTitle("OOPS mini project - Scientific Calculator"); // Set the text at the top of the window.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Makes the 'X' button close the app.
        getContentPane().setBackground(windowBgColor); // Sets the window's background color.
        setLayout(new BorderLayout(10, 10)); // Sets the layout for the window.
        
        JPanel mainPanel = new JPanel(new BorderLayout(10,10)); // Create a main panel to hold everything.
        mainPanel.setBackground(calculatorBodyColor); 
        mainPanel.setBorder(BorderFactory.createCompoundBorder( // Add some padding and a border.
            BorderFactory.createEmptyBorder(20, 20, 20, 20),
            BorderFactory.createLineBorder(Color.DARK_GRAY, 2, true)
        ));

        // This section builds the text display area.
        JPanel displayPanel = new JPanel(new BorderLayout());
        displayPanel.setBackground(calculatorBodyColor);
        displayPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(displayBgColor);
        history = new JLabel(" "); // Create the history label.
        history.setHorizontalAlignment(SwingConstants.RIGHT);
        history.setForeground(displayTextColor);
        history.setFont(new Font("Monospaced", Font.PLAIN, 14));
        modeIndicator = new JLabel("DEG"); // Create the mode label.
        modeIndicator.setHorizontalAlignment(SwingConstants.LEFT);
        modeIndicator.setForeground(displayTextColor);
        modeIndicator.setFont(new Font("Monospaced", Font.PLAIN, 14));
        topBar.add(history, BorderLayout.CENTER);
        topBar.add(modeIndicator, BorderLayout.WEST);

        display = new JTextArea("0", 2, 20); // Create the main display.
        display.setEditable(false); // User cannot type directly into it.
        display.setFont(new Font("Monospaced", Font.BOLD, 32));
        display.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        display.setBackground(displayBgColor);
        display.setForeground(displayTextColor);
        display.setMargin(new Insets(5, 5, 5, 5));
        
        JPanel displayContainer = new JPanel(new BorderLayout());
        displayContainer.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        displayContainer.add(topBar, BorderLayout.NORTH);
        displayContainer.add(display, BorderLayout.CENTER);
        displayPanel.add(displayContainer, BorderLayout.CENTER);
        
        mainPanel.add(displayPanel, BorderLayout.NORTH); // Add the display to the top of the main panel.
        mainPanel.add(createButtonPanel(), BorderLayout.CENTER); // Add the button grid to the center.
        
        add(mainPanel, BorderLayout.CENTER); // Add the main panel to the window.
        
        pack(); // Tell the window to resize to fit all components.
        setLocationRelativeTo(null); // Center the window on the screen.
        setMinimumSize(new Dimension(420, 750)); // Set a minimum window size.
        setVisible(true); // Make the window visible.
    }

    // This method builds and returns the button panel.
    // OOP Concept: This is Abstraction. Main code doesn't know *how* this panel is built.
    private JPanel createButtonPanel() { 
        JPanel panel = new JPanel(new GridBagLayout()); // Use GridBagLayout for a flexible grid.
        panel.setBackground(calculatorBodyColor);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints(); // Object to control component layout.
        gbc.fill = GridBagConstraints.BOTH; // Make buttons fill their grid cells.
        gbc.weightx = 1.0; // Allow cells to grow horizontally.
        gbc.weighty = 1.0; // Allow cells to grow vertically.
        gbc.insets = new Insets(4, 4, 4, 4); // Add 4px padding around buttons.
        
        // A 2D array to define the button layout. 'null' is an empty space.
        String[][] buttons = { 
            {"SHIFT", "ALPHA", null, "MODE", "ON"},
            {"x²", "x³", "xʸ", "x⁻¹", "√"},
            {"sin", "cos", "tan", "hyp", "log"},
            {"ln", "(", ")", "(-)", "RCL"},
            {"7", "8", "9", "DEL", "AC"},
            {"4", "5", "6", "×", "÷"},
            {"1", "2", "3", "+", "-"},
            {"0", ".", "×10ˣ", "Ans", "M+"},
            {"="}
        };

        for (int r = 0; r < buttons.length; r++) { // Loop through each row.
            for (int c = 0; c < buttons[r].length; c++) { // Loop through each column.
                if (buttons[r][c] == null) continue; // Skip the 'null' empty spaces.
                
                gbc.gridx = c; // Set grid x position.
                gbc.gridy = r; // Set grid y position.
                gbc.gridwidth = 1; // Default width is 1 cell.

                // --- Special rules for layout ---
                if (r == 0) { // For the first row...
                    if (c == 2) gbc.gridwidth = 2; // Make the "MODE" button 2 cells wide.
                    if (c > 2) gbc.gridx = c + 1; // Shift "ON" button to the right.
                }
                
                if (r == 8) { // For the last row...
                    gbc.gridwidth = 5; // Make the "=" button 5 cells wide.
                }
                
                Color color = buttonBgColor; // Set the default button color.
                // Check for special buttons and change their color.
                if (buttons[r][c].equals("SHIFT")) color = shiftButtonColor; 
                else if (buttons[r][c].equals("ALPHA")) color = alphaButtonColor;
                else if (buttons[r][c].equals("ON")) color = onButtonColor;
                else if (buttons[r][c].equals("DEL")) color = delButtonColor;
                else if (buttons[r][c].equals("AC")) color = acButtonColor;
                else if (buttons[r][c].equals("=")) color = equalsButtonColor;

                // Call our helper method to create and add the button.
                addButton(panel, buttons[r][c], gbc, color); 
            }
        }
        return panel; // Return the finished panel.
    }

    // This is a private helper method to create a single button.
    // OOP Concept: This is also Abstraction.
    private void addButton(JPanel panel, String text, GridBagConstraints gbc, Color color) { 
        JButton button = new JButton(text); // Create the button object.
        button.setFont(new Font("Inter", Font.BOLD, 16)); // Set its font.
        button.setFocusable(false); // Remove the dotted line when clicked.
        button.setBackground(color); // Set its background color.
        
        // Set the text color to black for light-colored buttons.
        if(color.equals(shiftButtonColor) || color.equals(alphaButtonColor) || color.equals(acButtonColor) || color.equals(equalsButtonColor) || color.equals(delButtonColor)){
            button.setForeground(Color.BLACK); 
        } else {
            button.setForeground(buttonTextColor); // Set text to white for dark buttons.
        }
        button.setBorder(BorderFactory.createEtchedBorder());
        button.addActionListener(this); // Tell the button to notify 'this' class when clicked.
        panel.add(button, gbc); // Add the button to the panel.
    }
    
    // This method is required by the 'ActionListener' interface.
    // OOP Concept: This is Polymorphism. It's our version of 'actionPerformed'.
    @Override 
    public void actionPerformed(ActionEvent e) { 
        String command = e.getActionCommand(); // Get the text (e.g., "7" or "+") from the clicked button.
        
        // If we are in STO, RCL, or ALPHA mode, handle it differently.
        if (isStoActive || isRclActive || isAlphaActive) { 
            handleMemoryAndAlpha(command);
            return; // Stop here.
        }
        
        // A 'switch' block to handle all the regular button clicks.
        switch (command) { 
            case "=": calculate(); break; // Run the calculation.
            case "ON": case "AC": // If "ON" or "AC" is clicked...
                display.setText("0"); history.setText(" "); // Reset the display.
                break;
            case "DEL": // If "DEL" is clicked...
                String current = display.getText(); // Get current text.
                // Remove one character, but don't go past "0".
                display.setText(current.length() > 1 && !current.equals("0") ? current.substring(0, current.length() - 1) : "0");
                break;
            case "SHIFT": isShiftActive = !isShiftActive; break; // Toggle the SHIFT state.
            case "ALPHA": isAlphaActive = !isAlphaActive; break; // Toggle the ALPHA state.
            case "hyp": isHypActive = !isHypActive; break; // Toggle the HYP state.
            
            // --- THIS IS THE CORRECTED LOGIC ---
            case "RCL":
                if (isShiftActive) {
                    isStoActive = true;    // Set STO mode to true
                    isShiftActive = false; // Turn off SHIFT mode
                } else {
                    isRclActive = true;    // Set RCL mode to true
                }
                break;
            // --- END OF CORRECTION ---
                
            case "MODE":
                // OOP Concept: Abstraction. We tell the engine to change its mode.
                engine.setDegrees(!engine.isDegrees()); 
                break;
            case "sin": handleFunction(getTrigFunction("sin")); break; // Handle 'sin' click.
            case "cos": handleFunction(getTrigFunction("cos")); break;
            case "tan": handleFunction(getTrigFunction("tan")); break; 
            case "M+":
                try { // We use 'try-catch' in case the math is bad (e.g., "5++").
                    // OOP Concept: Abstraction. Ask the engine to evaluate the text.
                    double currentValue = engine.evaluate(display.getText()); 
                    // OOP Concept: Encapsulation. Use getters/setters to modify engine's memory.
                    engine.setM(engine.getM() + currentValue); 
                    history.setText("M=" + engine.getM()); // Update history.
                } catch (Exception ex) { display.setText("Error"); } // Show "Error" if calculation fails.
                break;
            case "log": case "ln": case "√": handleFunction(command); break;
            case "Ans": handleInput("Ans"); break;
            case "x²": handleInput("^2"); break;
            case "x³": handleInput("^3"); break;
            case "x⁻¹": handleInput("^(-1)"); break;
            case "xʸ": handleInput("^"); break;
            case "×10ˣ": handleInput("*10^"); break;
            case "×": handleInput("*"); break;
            case "÷": handleInput("/"); break;
            case "(-)" : handleInput("(-"); break;
            default: // This handles all other buttons (numbers, operators, etc.)
                if ("0123456789.+-*/()".contains(command)) {
                        handleInput(command);
                }
                break;
        }
        updateModeIndicator(); // Update the 'DEG'/'RAD' label after every click.
    }
    
    // This helper method handles clicks when STO, RCL, or ALPHA is active.
    private void handleMemoryAndAlpha(String command) { 
        String variable;
        // Map the button pressed to a variable name.
        switch (command) { 
            case "(-)" : variable = "A"; break;
            case "hyp" : variable = "B"; break;
            case "sin" : variable = "C"; break;
            case "cos" : variable = "D"; break;
            case "tan" : variable = "E"; break;
            case "RCL" : variable = "F"; break;
            case ")"   : variable = "X"; break;
            case "M+"  : variable = "M"; break;
            default: variable = null;
        }

        if (variable != null) { // If we pressed a valid variable button...
            if (isStoActive) { // If we are in "Store" mode...
                try {
                    // OOP: Ask the engine to evaluate the text.
                    double valueToStore = engine.evaluate(display.getText()); 
                    // OOP: Ask the engine to store the value in the variable.
                    engine.storeVariable(variable, valueToStore); 
                    history.setText(display.getText() + "→" + variable); // Show "5→A"
                } catch (Exception e) { display.setText("Error"); }
            } else if (isRclActive || isAlphaActive) { // If we are in "Recall" or "Alpha" mode...
                handleInput(variable); // Just type the variable name (e.g., "A").
            }
        }
        // Reset all the special modes back to false.
        isStoActive = isRclActive = isAlphaActive = false; 
        updateModeIndicator(); // Update the UI.
    }
    
    // This helper figures out the correct function name (e.g., "asin" or "sinh").
    private String getTrigFunction(String base) { 
        if (isShiftActive) return "a" + base + (isHypActive ? "h" : ""); // e.g., "asin", "asinh"
        return base + (isHypActive ? "h" : ""); // e.g., "sin", "sinh"
    }
    
    // This helper updates the 'DEG'/'RAD' label.
    private void updateModeIndicator() { 
        String indicator = "";
        if (isShiftActive) indicator += "SHIFT "; // Check our private UI state.
        if (isAlphaActive) indicator += "ALPHA ";
        if (isStoActive) indicator += "STO ";
        if (isRclActive) indicator += "RCL ";
        if (isHypActive) indicator += "HYP ";
        // OOP: Encapsulation. Ask the engine for its current mode (DEG or RAD).
        indicator += (engine.isDegrees() ? "DEG" : "RAD"); 
        modeIndicator.setText(indicator); // Set the label's text.
    }
    
    // This helper adds text to the display.
    private void handleInput(String input) { 
        String currentText = display.getText();
        // If the display is just "0", replace it. Otherwise, add to it.
        display.setText(currentText.equals("0") && !"()+-*/.^,".contains(input) ? input : currentText + input);
    }

    // This helper adds a function (like "sin(") to the display.
    private void handleFunction(String func) { 
        String currentText = display.getText();
        // If "0", replace it with "sin(". Otherwise, add "sin(" to the end.
        display.setText(currentText.equals("0") ? func + "(" : currentText + func + "(");
    }
    
    // This helper shows a pop-up message (for unimplemented buttons).
    private void showInfoDialog(String message) { 
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // This helper runs the calculation.
    private void calculate() { 
        String expression = display.getText(); // Get the text to calculate.
        history.setText(expression + "="); // Show the expression in the history.
        try {
            // OOP: Abstraction. Ask the engine to do the calculation.
            double result = engine.evaluate(expression); 
            // Format the result (no ".0" for whole numbers).
            display.setText(result == (long) result ? String.format("%d", (long) result) : String.format("%s", result));
            // OOP: Encapsulation. Tell the engine what the new "Last Answer" is.
            engine.setLastAnswer(display.getText()); 
        } catch (Exception e) { // If the engine throws an error...
            // Show a simple error message on the display.
            display.setText(e.getMessage().replace("java.lang.RuntimeException: ", ""));
        }
    }
}
