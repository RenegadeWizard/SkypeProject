package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class Call {

    @FXML
    private BorderPane background;

    @FXML
    private Button endCallButton;

    private void endCall(){
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        double height = primaryScreenBounds.getWidth() * 0.494;
        double width = primaryScreenBounds.getWidth() * 0.8;
        Stage stage = (Stage) background.getScene().getWindow();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/sample.fxml"));
            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.show();
        }catch(IOException e){
            System.err.println("Could not find sample.fxml");
        }
    }

    @FXML
    public void initialize(){
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        double height = primaryScreenBounds.getWidth() * 0.494;
        double width = primaryScreenBounds.getWidth() * 0.8;
        BackgroundImage myImage = new BackgroundImage(new Image("img/pewdiepie.jpg", width, height, false,false), BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        background.setBackground(new Background(myImage));
        endCallButton.setOnAction(e->endCall());
        ImageView imageView = new ImageView(new Image("img/call.png"));
        imageView.setFitHeight(30);
        imageView.setFitWidth(30);
        endCallButton.setGraphic(imageView);
    }
}
