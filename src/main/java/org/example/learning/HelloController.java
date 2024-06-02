package org.example.learning;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.example.learning.components.LineCursor;
import org.example.learning.components.WindowBox;
import org.example.learning.components.constants.ControllerNode;
import org.example.learning.components.glyph.ControlPanel;
import org.example.learning.components.glyph.DoublyLinkedList;
import org.example.learning.components.glyph.FontControlPanel;
import org.example.learning.components.glyph.TextGlyph;
import org.example.learning.components.uibuilders.ControlBuilder;

import java.util.Arrays;

public class HelloController {
    private static final String defaultFont = Font.getDefault().getName();
    String[] fontWeightArray = new String[]{"Thin", "Extra Light", "Light", "Regular", "Medium", "Semi Bold",
            "Bold", "Extra Bold", "Black"};
    private static final String defaultFontWeight = "Regular";
    Double[] fontSizes = new Double[]{10.0, 11.0, 12.0, 13.0, 14.0, 15.0,
            16.0, 20.0, 24.0, 32.0, 36.0, 40.0, 48.0, 64.0, 96.0, 128.0};
    private static final double defaultFontSize = 12;
    private static final Color defaultColor = Color.BLACK;
    /**
     * Builds the sidebar for styling the text elements. Dynamic functions for styling different elements.
     */
    ControlBuilder controlBuilder;
    /**
     * The sidebar for styling the text elements.
     */
    ScrollPane sidebarScrollPane;
    /**
     * Text objects to be drawn on the screen
     */
    DoublyLinkedList textRowList = new DoublyLinkedList();
    /**
     * Current text which the user is typing into
     */
    WindowBox windowBox;
    /**
     * Line cursor flashing line to symbolize where the user is typing
     */
    LineCursor lineCursor;
    @FXML
    AnchorPane textAnchorPane;

    @FXML
    VBox sidebarVBox;

//    @FXML
//    ComboBox<String> fontComboBox;
//
//    @FXML
//    ComboBox<String> fontWeightComboBox;
//
//    @FXML
//    ComboBox<Double> fontSizeComboBox;
//
//    @FXML
//    ColorPicker colorPicker;
//
//    @FXML
//    Button plusFontSizeButton;
//
//    @FXML
//    Button minusFontSizeButton;

    ControlPanel fontControlPanel;


    @FXML
    public void initialize() {
//        fontControlPanel = new FontControlPanel(fontComboBox, defaultFont,
//                fontWeightComboBox, defaultFontWeight,
//                fontSizeComboBox, defaultFontSize,
//                colorPicker, defaultColor);

        windowBox = new WindowBox(textAnchorPane, textRowList, fontControlPanel, lineCursor);
        controlBuilder = new ControlBuilder(sidebarVBox);
        controlBuilder.buildFontCommandBar();

        // dynamically increase font size function
        ComboBox<Double> fontSizeComboBox = (ComboBox<Double>) controlBuilder.getNode(ControllerNode.FONT_SIZE);
        Button increaseFontSizeButton = (Button) controlBuilder.getNode(ControllerNode.FONT_INCREASE_SIZE);
        controlBuilder.addFunction("increaseFontSize", () -> {
            TextGlyph currentText = windowBox.getCurrentText();
            double newFontSize = currentText.getFontSize() + 1;
            currentText.setFont(Font.font(currentText.getFont().getName(), currentText.getFontWeight(), newFontSize));
            currentText.setFontSize(newFontSize);
            if (fontSizeComboBox != null) {
                fontSizeComboBox.setValue(newFontSize);
            }
        });

        if (increaseFontSizeButton != null) {
            increaseFontSizeButton.setOnAction(click -> {
                controlBuilder.executeFunction("increaseFontSize");
            });
        }

        // dynamically decrease font size function
        Button decreaseFontSizeButton = (Button) controlBuilder.getNode(ControllerNode.FONT_DECREASE_SIZE);
        controlBuilder.addFunction("decreaseFontSize", () -> {
            TextGlyph currentText = windowBox.getCurrentText();
            double newFontSize = currentText.getFontSize() - 1;
            currentText.setFont(Font.font(currentText.getFont().getName(), currentText.getFontWeight(), newFontSize));
            currentText.setFontSize(newFontSize);
            if (fontSizeComboBox != null) {
                fontSizeComboBox.setValue(newFontSize);
            }
        });
        if (decreaseFontSizeButton != null) {
            decreaseFontSizeButton.setOnAction(click -> {
                controlBuilder.executeFunction("decreaseFontSize");
            });
        }

//        // build combo boxes for selecting font styles
//        fontComboBox.setItems(FXCollections.observableArrayList(Font.getFamilies()));
//        fontComboBox.setValue(defaultFont);
//
//        fontWeightComboBox.setItems(FXCollections.observableArrayList(fontWeightArray));
//        fontWeightComboBox.setValue(defaultFontWeight);
//
//        fontSizeComboBox.setItems(FXCollections.observableArrayList(fontSizes));
//        fontSizeComboBox.setValue(defaultFontSize);
//
//        colorPicker.setValue(defaultColor);

    }

//    @FXML
//    public void selectFont() {
//        String font = fontComboBox.getValue();
//        TextGlyph currentText = windowBox.getCurrentText();
//        currentText.setFont(new Font(font, currentText.getFontSize()));
//
//    }
//
//    @FXML
//    public void selectFontWeight() {
//        String fontWeightString = fontWeightComboBox.getValue();
//        TextGlyph currentText = windowBox.getCurrentText();
//        FontWeight fontWeight = getFontWeight(fontWeightString);
//        currentText.setFontWeight(fontWeight);
//        currentText.setFont(Font.font(currentText.getFont().getName(), fontWeight, currentText.getFontSize()));
//    }

    public static FontWeight getFontWeight(String fontWeight) {
        switch (fontWeight) {
            case "Thin":
                return FontWeight.THIN;
            case "Extra Light":
                return FontWeight.EXTRA_LIGHT;
            case "Light":
                return FontWeight.LIGHT;
            case "Regular":
                return FontWeight.NORMAL;
            case "Medium":
                return FontWeight.MEDIUM;
            case "Semi Bold":
                return FontWeight.SEMI_BOLD;
            case "Bold":
                return FontWeight.BOLD;
            case "Extra Bold":
                return FontWeight.EXTRA_BOLD;
            case "Black":
                return FontWeight.BLACK;
            default:
                return FontWeight.NORMAL;
        }
    }

//    @FXML
//    public void selectFontSize() {
//        double fontSize = fontSizeComboBox.getValue();
//        TextGlyph currentText = windowBox.getCurrentText();
//        double currentFontSize = currentText.getFontSize();
//        currentText.setFont(Font.font(currentText.getFont().getName(), currentText.getFontWeight(), fontSize));
//        currentText.setFontSize(fontSize);
//        currentText.requestLayout();
//        windowBox.updateLineCursor();
//    }
//
//    @FXML
//    public void selectFontColor() {
//        Color selectedColor = colorPicker.getValue();
//        TextGlyph currentText = windowBox.getCurrentText();
//        currentText.getText().setFill(selectedColor);
//        currentText.setFontColor(selectedColor);
//    }

//    @FXML
//    public void incrementFontSize() {
//        TextGlyph currentText = windowBox.getCurrentText();
//        double newFontSize = currentText.getFontSize() + 1;
//        currentText.setFont(Font.font(currentText.getFont().getName(), currentText.getFontWeight(), newFontSize));
//        currentText.setFontSize(newFontSize);
//
//        fontSizeComboBox.setValue(newFontSize);
//
//    }

//    @FXML
//    public void decrementFontSize() {
//        TextGlyph currentText = windowBox.getCurrentText();
//        double newFontSize = currentText.getFontSize() + -1;
//        if (newFontSize < 0) {
//            return;
//        }
//        currentText.setFont(Font.font(currentText.getFont().getName(), currentText.getFontWeight(), newFontSize));
//        currentText.setFontSize(newFontSize);
//
//        fontSizeComboBox.setValue(newFontSize);
//
//    }


}