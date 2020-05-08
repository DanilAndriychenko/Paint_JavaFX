package sample;

import com.jfoenix.controls.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import jfxtras.labs.util.event.MouseControlUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

//TODO think about hot keys for each action

/**
 * The type Controller.
 */
public class Controller {

    @FXML
    private StackPane stackPane;
    @FXML
    private BorderPane borderPane;
    @FXML
    private Canvas canvas;
    @FXML
    private JFXButton buttonPaste;
    //
    @FXML
    private JFXToggleNode toggleNodeCut;
    @FXML
    private JFXButton buttonChangeSize;
    @FXML
    private JFXButton buttonRotate;
    //
    @FXML
    private JFXToggleNode toggleNodePencil;
    //
    @FXML
    private JFXToggleNode toggleNodeEraser;
    //takes color from chosen pixel and remember it in current color
    @FXML
    private JFXToggleNode toggleNodeColorPicker;
    //
    @FXML
    private JFXToggleNode toggleNodeCreateLabel;
    //
    @FXML
    private JFXToggleNode toggleNodeShapeCircle;
    //
    @FXML
    private JFXToggleNode toggleNodeShapeRectangle;
    @FXML
    private JFXColorPicker colorPickerForFigureLine;
    @FXML
    private JFXColorPicker colorPickerForFigureFill;
    @FXML
    private JFXColorPicker colorPickerCurrent;
    @FXML
    private JFXButton buttonRecentFirst;
    @FXML
    private JFXButton buttonRecentSecond;
    @FXML
    private JFXButton buttonRecentThird;
    @FXML
    private JFXButton buttonRecentFourth;
    @FXML
    private JFXButton buttonRecentFifth;
    @FXML
    private JFXSlider sliderPointSize;

    /*
     * This object is used to issue draw calls to a Canvas using a buffer.
     */
    private GraphicsContext graphicsContext;
//    /*
//     * This arraylist store buttons of select.
//     * Important moment that only one button from this array can be chosen and highlighted on the gui interface.
//     */
//    private ArrayList<JFXToggleNode> buttonsOfSelect;
    /*
     * Queue that store current color and recent.
     */
    private LinkedList<Color> colorsQueue;
    /*
     * ArrayList of buttons that responsible for colours.
     */
    private ArrayList<JFXButton> recentColorsButtons;
    /*
     * ToggleNode that store selected action.
     */
    private JFXToggleNode toggleNodeSelected = null;

    /**
     * Initialize.
     */
    @FXML
    public void initialize() {
        /*
         * Setting graphicsContext from canvas
         */
        graphicsContext = canvas.getGraphicsContext2D();
//        /*
//         *  Defining arrayList of buttonsOfSelect and adding them
//         */
//        buttonsOfSelect = new ArrayList<>();
//        buttonsOfSelect.addAll(List.of(toggleNodeCut, toggleNodePencil, toggleNodeFill, toggleNodeCreateLabel, toggleNodeEraser,
//                toggleNodeColorPicker, toggleNodeMagnifier));
        /*
         * Defining linkedList of colours, adding main colors and setting to panel.
         */
        colorsQueue = new LinkedList<>();
        colorsQueue.addAll(List.of(Color.BLACK, Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE));
        revalidateColorPanel();
        /*
         * Defining arrayList of buttons in order to know which of recent colors we want to restore.
         */
        recentColorsButtons = new ArrayList<>(List.of(buttonRecentFirst, buttonRecentSecond,
                buttonRecentThird, buttonRecentFourth, buttonRecentFifth));
        /*
         * Adding listeners to CurrentColorPicker and RecentButtons.
         */
        colorPickerCurrent.setOnAction(actionEvent -> addNewColorToPanel());
        buttonRecentFirst.setOnAction(actionEvent -> resetColors(buttonRecentFirst));
        buttonRecentSecond.setOnAction(actionEvent -> resetColors(buttonRecentSecond));
        buttonRecentThird.setOnAction(actionEvent -> resetColors(buttonRecentThird));
        buttonRecentFourth.setOnAction(actionEvent -> resetColors(buttonRecentFourth));
        buttonRecentFifth.setOnAction(actionEvent -> resetColors(buttonRecentFifth));
        /*
         * Setting start config for figure colors.
         */
        colorPickerForFigureLine.setValue(Color.BLACK);
        colorPickerForFigureFill.setValue(Color.WHITE);
        /*
         * Setting cursor - HAND because of no active action is chosen
         * Setting onAction to all toggleButtonsOfSelect
         */
        canvas.setCursor(Cursor.HAND);
        toggleNodeShapeCircle.setOnAction(actionEvent -> changeSelectedToggleNode(toggleNodeShapeCircle));
        toggleNodeShapeRectangle.setOnAction(actionEvent -> changeSelectedToggleNode(toggleNodeShapeRectangle));
        toggleNodeColorPicker.setOnAction(actionEvent -> changeSelectedToggleNode(toggleNodeColorPicker));
        toggleNodeCreateLabel.setOnAction(actionEvent -> changeSelectedToggleNode(toggleNodeCreateLabel));
        toggleNodeCut.setOnAction(actionEvent -> changeSelectedToggleNode(toggleNodeCut));
        toggleNodeEraser.setOnAction(actionEvent -> changeSelectedToggleNode(toggleNodeEraser));
        toggleNodePencil.setOnAction(actionEvent -> changeSelectedToggleNode(toggleNodePencil));
        /*
         * Painting canvas in white.
         */
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        /*
         * Setting slider maximum point and current.
         */
        sliderPointSize.setMax(50);
        sliderPointSize.setValue(25);
        /*
         * Statements for mouse pressed on the canvas.
         */
        canvas.setOnMousePressed(mouseEvent -> {
            if (toggleNodeSelected == toggleNodeColorPicker) {
                /*
                 * Reading color from chosen pixel and setting current to it.
                 */
                colorPickerCurrent.
                        setValue(canvas.snapshot(null, null).getPixelReader().getColor((int) mouseEvent.getX(), (int) mouseEvent.getY()));
                addNewColorToPanel();
            } else if (toggleNodeSelected == toggleNodePencil) {
                /*
                 * Draws rectangular dot on clicked area.
                 */
                graphicsContext.setStroke(colorPickerCurrent.getValue());
                graphicsContext.setLineWidth(sliderPointSize.getValue());
                graphicsContext.strokeLine(mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getX(), mouseEvent.getY());
            } else if (toggleNodeSelected == toggleNodeEraser) {
                /*
                 * Erase rectangular dot on clicked area.
                 * Works by drawing white color
                 */
                graphicsContext.setStroke(Color.WHITE);
                graphicsContext.setLineWidth(sliderPointSize.getValue());
                graphicsContext.strokeLine(mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getX(), mouseEvent.getY());
            } else return;
        });
    }

    /**
     * Show about dialog.
     */
    @FXML
    public void showAboutDialog() {
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("About application"));
        content.setBody(new Text("This is a very simple JavaFX application named \"Paint 2D\".\n" +
                "Paint 2D 2020.05 (Ultimate Edition)\n" +
                "Build #UI-201.7223.91, built on May 13, 2020\n" +
                "Licensed to Danil Andriychenko\n" +
                "Windows 10.0\n"));
        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        JFXButton button = new JFXButton("Thank you!");
        button.setOnAction(event -> dialog.close());
        content.setActions(button);
        dialog.show();
    }

    /**
     * Re validating color panel. In other words, setting all colors from colorsQueue.
     */
    private void revalidateColorPanel() {
        colorPickerCurrent.setValue(colorsQueue.getFirst());
        buttonRecentFirst.setBackground(new Background(new BackgroundFill(colorsQueue.get(1), null, null)));
        buttonRecentSecond.setBackground(new Background(new BackgroundFill(colorsQueue.get(2), null, null)));
        buttonRecentThird.setBackground(new Background(new BackgroundFill(colorsQueue.get(3), null, null)));
        buttonRecentFourth.setBackground(new Background(new BackgroundFill(colorsQueue.get(4), null, null)));
        buttonRecentFifth.setBackground(new Background(new BackgroundFill(colorsQueue.get(5), null, null)));
    }

    /**
     * Resetting color order relying on which of recent buttons was clicked.
     *
     * @param clickedRecentColorButton
     */
    private void resetColors(JFXButton clickedRecentColorButton) {
        int indexOfChosenColor = recentColorsButtons.indexOf(clickedRecentColorButton) + 1;
        Color newMainColor = colorsQueue.get(indexOfChosenColor);
        colorsQueue.remove(indexOfChosenColor);
        colorsQueue.push(newMainColor);
        revalidateColorPanel();
    }

    /**
     * Changes selected toggleNode, taking into consideration clicking on same button or when there is no selected toggleNode.
     *
     * @param newSelectedNode
     */
    private void changeSelectedToggleNode(JFXToggleNode newSelectedNode) {
        if (toggleNodeSelected == null) {
            toggleNodeSelected = newSelectedNode;
            canvas.setCursor(Cursor.DEFAULT);
        } else if (toggleNodeSelected == newSelectedNode) {
            toggleNodeSelected = null;
            canvas.setCursor(Cursor.HAND);
        } else {
            toggleNodeSelected.setSelected(false);
            toggleNodeSelected = newSelectedNode;
            canvas.setCursor(Cursor.DEFAULT);
        }
    }

    /**
     * If new chosen color not equals current, then add it on the color panel.
     */
    private void addNewColorToPanel() {
        if (!colorPickerCurrent.getValue().equals(colorsQueue.getFirst())) {
            colorsQueue.push(colorPickerCurrent.getValue());
            revalidateColorPanel();
        }
    }
}
