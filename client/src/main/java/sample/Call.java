package sample;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class Call implements Runnable{

    private Controller controller;
    private String nick;
    private Connect connection;
    private double width;
    private double height;
    private String response;
    private BooleanProperty isResponse;

    @FXML private BorderPane background;
    @FXML private VBox mainVBox;
    @FXML private TextArea promptField;
    @FXML private Label endLabel;


    public Call(Controller controller, String nick, Connect connection){
        this.controller = controller;
        this.nick = nick;
        this.connection = connection;
        isResponse = new SimpleBooleanProperty(false);
    }

    @FXML private void returnTo(){
        try {
            connection.write("G");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample.fxml"));
            controller = new Controller();
            loader.setController(controller);
            Parent root = loader.load();
            Stage stage = (Stage) background.getScene().getWindow();
            stage.setScene(new Scene(root, width, height));
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    @FXML private void enter(KeyEvent key){
        if(key.getCode().equals(KeyCode.ENTER)){
            try {
                mainVBox.getChildren().add(createField(promptField.getText(), connection.getNick(), true));
                connection.write("P" + promptField.getText());
                promptField.setText("");
                promptField.positionCaret(0);

            }catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }

    private HBox createField(String text, String nick, boolean which){
        HBox hbox = new HBox();
        hbox.setSpacing(10);
        Label label = new Label(text);
        Label who = new Label(nick + ":");
        if (which)
            who.setStyle("-fx-text-fill: #990022;");
        else
            who.setStyle("-fx-text-fill: #220099;");
        label.setWrapText(true);
        hbox.getChildren().add(who);
        hbox.getChildren().add(label);
        return hbox;
    }

    public void reload(){
        try {
            System.out.println("początek czytania");
            response = connection.readEverything();
            System.out.println("koniec czytania");
            isResponse.setValue(true);
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    private void ziomekNapisal(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (response.charAt(0) == 'G'){
                    endLabel.setText(nick + " opuścił czat");
                    return;
                }
                mainVBox.getChildren().add(createField(response, nick, false));
                isResponse.setValue(false);
            }
        });
    }

    @FXML
    public void initialize(){
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        height = primaryScreenBounds.getWidth() * 0.494;
        width = primaryScreenBounds.getWidth() * 0.8;
        mainVBox.setPrefHeight(height - 220);
        isResponse.addListener(e->ziomekNapisal());
    }

    @Override
    public void run() {
        while(true) { reload(); }
    }
}
