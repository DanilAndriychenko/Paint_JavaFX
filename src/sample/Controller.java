package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class Controller {

    @FXML
    private StackPane stackPane;

    @FXML
    public void showAboutDialog(ActionEvent actionEvent){
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
}
