/**
 * Sample Skeleton for 'primary.fxml' Controller Class
 */

package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import java.util.List;

public class PrimaryController {

    int sourceBase = 10; // Saving the previous base

    @FXML // fx:id="buttonAND"
    private Button buttonAND; // Value injected by FXMLLoader

    @FXML // fx:id="buttonNOT"
    private Button buttonNOT; // Value injected by FXMLLoader

    @FXML // fx:id="button1"
    private Button button1; // Value injected by FXMLLoader

    @FXML // fx:id="button2"
    private Button button2; // Value injected by FXMLLoader

    @FXML // fx:id="button3"
    private Button button3; // Value injected by FXMLLoader

    @FXML // fx:id="button4"
    private Button button4; // Value injected by FXMLLoader

    @FXML // fx:id="button5"
    private Button button5; // Value injected by FXMLLoader

    @FXML // fx:id="button6"
    private Button button6; // Value injected by FXMLLoader

    @FXML // fx:id="button7"
    private Button button7; // Value injected by FXMLLoader

    @FXML // fx:id="button8"
    private Button button8; // Value injected by FXMLLoader

    @FXML // fx:id="button9"
    private Button button9; // Value injected by FXMLLoader

    @FXML // fx:id="buttonA"
    private Button buttonA; // Value injected by FXMLLoader

    @FXML // fx:id="buttonB"
    private Button buttonB; // Value injected by FXMLLoader

    @FXML // fx:id="buttonC"
    private Button buttonC; // Value injected by FXMLLoader

    @FXML // fx:id="buttonD"
    private Button buttonD; // Value injected by FXMLLoader

    @FXML // fx:id="buttonE"
    private Button buttonE; // Value injected by FXMLLoader

    @FXML // fx:id="buttonF"
    private Button buttonF; // Value injected by FXMLLoader

    @FXML // fx:id="buttonOR"
    private Button buttonOR; // Value injected by FXMLLoader

    @FXML // fx:id="buttonXOR"
    private Button buttonXOR; // Value injected by FXMLLoader

    @FXML // fx:id="calcScreen"
    private TextField calcScreen; // Value injected by FXMLLoader

    @FXML // fx:id="listBox"
    private ComboBox<Integer> listBox; // Value injected by FXMLLoader


    @FXML
    public void initialize() {
        // Add items programmatically if not defined in FXML
        listBox.getItems().addAll(2, 8, 10, 16);
        // Set default value
        listBox.setValue(10); // Replace 10 with your desired default number
        enableAllButtons();
        List.of(buttonA, buttonB, buttonC, buttonD, buttonE, buttonF, buttonXOR,
                buttonOR, buttonAND, buttonNOT).forEach(button -> button.setDisable(true));
    }

    @FXML
    private void enableAllButtons() {
        List.of(button1, button2, button3, button4, button5, button6, button7, button8, button9,
                buttonA,buttonB,buttonC,buttonD,buttonE,buttonF,buttonXOR, buttonOR, buttonAND, buttonNOT).forEach(button -> button.setDisable(false));
    }

    @FXML
    private void onComboBoxAction() {
        int targetBase = listBox.getValue();
        String currentExpression = calcScreen.getText();

        if(currentExpression != null && (!currentExpression.isEmpty()) && sourceBase != targetBase) {
            calcScreen.clear();
            calcScreen.setText(ArithmeticApp.convertExpression(currentExpression, sourceBase, targetBase));
            calculate();
        }

        sourceBase = targetBase;
        // Disable/Enable buttons based on the selected item
        switch (targetBase){
            case(8):
                enableAllButtons();
                List.of(button8, button9,buttonA, buttonB, buttonC, buttonD, buttonE,
                        buttonF,buttonXOR, buttonOR, buttonAND, buttonNOT).forEach(button -> button.setDisable(true));
                break;
            case(10):
                enableAllButtons();
                List.of(buttonA, buttonB, buttonC, buttonD, buttonE, buttonF, buttonXOR,
                        buttonOR, buttonAND, buttonNOT).forEach(button -> button.setDisable(true));
                break;
            case(16):
                enableAllButtons();
                List.of(buttonXOR, buttonOR, buttonAND, buttonNOT).forEach(button -> button.setDisable(true));
                break;
            case(2):
                enableAllButtons();
                List.of(button2, button3, button4,button5,button6,button7,button8,button9,
                        buttonA,buttonB,buttonC,buttonD,buttonE,buttonF).forEach(button -> button.setDisable(true));
                break;

        }
    }

    @FXML
    void buildExpression(ActionEvent event) {
        String expression = ((Button)event.getSource()).getText();
        if(expression.equals("Clear")) {
            calcScreen.clear();
        }
        else{
            calcScreen.setText(calcScreen.getText() + expression);
        }

    }

    @FXML
    void calculate() {
        Integer selectedBase = listBox.getValue();
        String expression = calcScreen.getText(); // Get the expression from the TextField

        if (selectedBase != null) {
            // Call the operate method from ArithmeticApp
            String result = ArithmeticApp.operate(expression, selectedBase);
            calcScreen.clear();
            calcScreen.setText(result);
        } else {
            // Handle case when the base is missing
            calcScreen.setText("Please select Base, then insert the expression");
        }
    }

}
