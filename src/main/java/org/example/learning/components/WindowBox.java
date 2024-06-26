package org.example.learning.components;

import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.example.learning.components.glyph.*;

import java.security.Key;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

/**
 * This is the main class which the user types into to draw on the screen
 */
public class WindowBox {
    /**
     * Width of the editor Pane
     */
    public static final int WIDTH = 600;

    /**
     * Padding for the text area (pane)
     */
    public static final int PADDING = 8;
    /**
     * Current text which the user is typing into
     */
    private TextGlyph currentText = new TextGlyph("");
    /**
     * Text objects to be drawn on the screen
     */
    private DoublyLinkedList textRowList;
    /**
     * Holds all the text elements
     */
    AnchorPane pane;
    /**
     * Blinking cursor
     */
    LineCursor lineCursor;
    /**
     * Users current y Position on the screen
     */
    double yPos = 0;

    ControlPanel fontControlPanel;
    Element focusedElement = currentText;
    Scene scene;

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public WindowBox(AnchorPane anchorPane, DoublyLinkedList textRowList, ControlPanel fontControlPanel, LineCursor lineCursor) {
        this.textRowList = textRowList;
        this.fontControlPanel = fontControlPanel;
        this.lineCursor = lineCursor;
        // initialize pane & set styling
        pane = anchorPane;
        pane.getStyleClass().add("border-outline");
        TextFlow newTextFlow = new TextFlow(currentText);
        Line newLine = new Line(newTextFlow);
        createLine(newLine, newTextFlow);
        textRowList.insertEnd(newLine);
        createNewText(currentText);
        drawTextFlow(((Line) textRowList.getCurrent()).getTextFlow());
        // add event handlers for when the user types to draw on the screen
        initializeFocusHandler();
        // draw the cursor
        initializeCursor();


    }


    /**
     * Runs on initialization to start listening to keypress events
     * when the pane is clicked.
     */
    public void initializeFocusHandler() {
        /**
         * Adds focus to the Pane so it can listen to key events
         */
        pane.setOnMouseClicked(event -> {
            pane.requestFocus();
        });

        pane.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            KeyCode keyCode = event.getCode();

            if (keyCode.equals(KeyCode.RIGHT) || keyCode.equals(KeyCode.LEFT)) {
                // move cursor left or right and update where text is typed into the current text
                moveTextBufferWithinWord(keyCode);

            } else if (keyCode.equals(KeyCode.UP) || keyCode.equals(KeyCode.DOWN)) {
                moveUpOrDownLine(keyCode);
            } else if (keyCode.equals(KeyCode.BACK_SPACE)) {
                deleteLetter();
            } else if (keyCode.equals(KeyCode.ENTER)) {
                // enter key clicked
                startNewLine();
            } else if (keyCode.equals(KeyCode.TAB)) {
                // tab key clicked
                currentText.setText(currentText.getTextValue() + "\t");
            } else if (!event.getText().isEmpty()) {
                // alphanumerical character clicked. Update text and cursor position
                currentText.setText(currentText.getTextValue() + event.getText());
                Text text = new Text(event.getText());
                lineCursor.updatePosition(currentText.getLayoutX() + currentText.getLayoutBounds().getWidth() + text.getLayoutBounds().getWidth(), currentText.getParent().getLayoutY(), currentText.getLayoutBounds().getHeight());
            }
        });
    }

    public void moveUpOrDownLine(KeyCode keyDirection) {
        if (keyDirection.equals(KeyCode.UP)) {
            moveUpLine();
        } else {
            moveDownLine();
        }
    }

    public void moveUpLine() {
        if (textRowList.currentHasPrev()) {
            decreaseYPos();
            textRowList.decreaseCurrent();
            TextFlow newTextFlowLine = ((Line) textRowList.getCurrent()).getTextFlow();
            currentText = (TextGlyph) newTextFlowLine.getChildren().get(newTextFlowLine.getChildren().size() - 1);
            lineCursor.updatePosition(getTextFlowChildrenWidth(newTextFlowLine), yPos, currentText.getLayoutBounds().getHeight());
        }

    }

    public void moveDownLine() {
        if (textRowList.currentHasNext()) {
            increaseYPos();
            textRowList.increaseCurrent();
            TextFlow newTextFlowLine = ((Line) textRowList.getCurrent()).getTextFlow();
            currentText = (TextGlyph) newTextFlowLine.getChildren().get(newTextFlowLine.getChildren().size() - 1);
            lineCursor.updatePosition(getTextFlowChildrenWidth(newTextFlowLine), yPos, currentText.getLayoutBounds().getHeight());
        }


    }

    public double getTextFlowChildrenWidth(TextFlow textFlow) {
        double totalTextWidth = 0.0;

        for (Node node : textFlow.getChildren()) {
            if (node instanceof TextGlyph textGlyph) {
                totalTextWidth += textGlyph.getBoundsInLocal().getWidth();
            }

        }
        return totalTextWidth;
    }

    public void moveTextBufferWithinWord(KeyCode keyDirection) {
        System.out.println("key moved!!");
    }

    // add TextFlow to the screen
    public void drawTextFlow(TextFlow textFlow) {
        pane.getChildren().add(textFlow);
    }

    public void deleteLetter() {
        String currentTextContent = currentText.getTextValue();
        ObservableList<Node> textRowChildren = ((Line) (textRowList.getCurrent())).getTextFlow().getChildren();
        // delete character if there is character to delete
        if (!currentTextContent.isEmpty()) {
            String charToRemove = String.valueOf(currentTextContent.charAt(currentTextContent.length() - 1));
            currentText.setText(currentTextContent.substring(0, currentTextContent.length() - 1));
            lineCursor.updatePosition(currentText.getLayoutX() + currentText.getLayoutBounds().getWidth() - new Text(charToRemove).getLayoutBounds().getWidth(), ((Line) textRowList.getCurrent()).getTextFlow().getLayoutY(), currentText.getLayoutBounds().getHeight());
            // delete line and move cursor position up a line if no characters left in line
        } else if (!textRowChildren.isEmpty()) {
            if (textRowChildren.size() > 1) {
                textRowChildren.remove(textRowChildren.size() - 1);
                currentText = (TextGlyph) textRowChildren.get(textRowChildren.size() - 1);
            } else {
                deleteLine();
            }
        }
    }

    public void createNewText(TextGlyph newText) {
        newText.getText().boundsInLocalProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("text height");
            System.out.println(newText.getHeight());
        });

        currentText.setOnMouseClicked(click -> {
            // Prevent event from being called to parent TextFlow, this way the click affect applies to the
            // Text, instead of the TextFlow
            click.consume();
            // remove previous text from focused state
            deFocusElement(focusedElement);
            // make this text in the focused state
            focusedElement = newText;
            focusElement(focusedElement);
            // make this text the text to be updated on key click
            currentText = newText;
            // fontControlPanel.setStyles(currentText);
            yPos = currentText.getParent().getLayoutY();
            lineCursor.updatePosition(currentText.getLayoutX() + currentText.getLayoutBounds().getWidth(), yPos, currentText.getLayoutBounds().getHeight());
        });

        currentText.setOnMouseEntered(evt -> {
            System.out.println("entered");
            scene.setCursor(Cursor.HAND);
        });

        currentText.setOnMouseExited(evt -> {
            System.out.println("Exited");
            scene.setCursor(Cursor.DEFAULT);
        });

        currentText.setOnMouseReleased(click -> {

        });


    }

    public void deFocusElement(Element text) {
        text.removeBorder();
    }

    public void focusElement(Element text) {
        text.addBorder("blue");
    }

    /**
     * Starts a new line below the current one when the enter key is clicked.
     */
    public void startNewLine() {
        increaseYPos();
        currentText = new TextGlyph("");
        TextFlow newTextFlow = new TextFlow(currentText);
        Line newLine = new Line(newTextFlow);
        createLine(newLine, newTextFlow);

        textRowList.insertEnd(newLine);
        textRowList.increaseCurrent();

        createNewText(currentText);
        drawTextFlow(((Line) textRowList.getCurrent()).getTextFlow());
        lineCursor.updatePosition(0, yPos, currentText.getLayoutBounds().getHeight());
    }

    public void createLine(Line newLine, TextFlow newTextFlow) {
        // set text flow to always be 100% width of parent pane
        newTextFlow.prefWidthProperty().bind(pane.prefWidthProperty());
        newTextFlow.setLayoutY(yPos);
        newTextFlow.setOnMouseClicked(click -> {
            System.out.println("line clicked");
            deFocusElement(focusedElement);
            focusedElement = newLine;
            focusElement(newLine);
        });
    }

    /**
     * Deletes the current text line
     */
    public void deleteLine() {
        // delete line if not on first line (might need to be changed)
        if (textRowList.getCurrent() != textRowList.getFirst()) {
            // removes the TextFlow line
            textRowList.remove(textRowList.getCurrent());

            shiftLinesUp(textRowList.getCurrent());
            // moves the cursor one line up
            decreaseYPos();
            TextFlow currentTextFlow = ((Line) textRowList.getCurrent()).getTextFlow();
            currentText = new TextGlyph("");
            currentTextFlow.getChildren().add(currentText);
            lineCursor.updatePosition(currentTextFlow.getLayoutBounds().getWidth(), yPos, currentText.getLayoutBounds().getHeight());
        }
    }


    // update users Y position
    public void increaseYPos() {
        yPos += currentText.getLayoutBounds().getHeight();
    }

    public void decreaseYPos() {
        yPos -= currentText.getLayoutBounds().getHeight();
    }

    public void shiftLinesUp(ListNode node) {
        while (node.hasNext()) {
            TextFlow currentRow = ((Line) node).getTextFlow();
            currentRow.setLayoutY(currentRow.getLayoutY() - currentText.getLayoutBounds().getHeight());
            node = node.getNext();
        }

    }


    public Pane getPane() {
        return pane;
    }

    public void setPane(AnchorPane pane) {
        this.pane = pane;
    }

    // start the cursor animation and add to the pane
    public void initializeCursor() {
        lineCursor = new LineCursor(PADDING, PADDING, 10);
        lineCursor.setStroke(Color.BLACK);
        lineCursor.startTransition();
        pane.getChildren().add(lineCursor);
    }

    public void updateLineCursor() {
        lineCursor.updatePosition(((Line) textRowList.getCurrent()).getTextFlow().getLayoutBounds().getWidth(), yPos, currentText.getLayoutBounds().getHeight());
    }

    public TextGlyph getCurrentText() {
        return currentText;
    }

}
