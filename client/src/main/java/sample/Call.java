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

public class Call implements Runnable{

    private Controller controller;
    private String nick;
    private Connect connection;
    private double width;
    private double height;

    @FXML
    private BorderPane background;

    @FXML
    private Button endCallButton;

    public Call(Controller controller, String nick, Connect connection){
        this.controller = controller;
        this.nick = nick;
        this.connection = connection;
    }

    private void endCall(){
        double height = background.getHeight();
        double width = background.getWidth();
        try {
            connection.write("G");
            connection.unlockMutex();
        }catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex){
            ex.printStackTrace();
        }
        Stage stage = (Stage) background.getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample.fxml"));
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.show();
        }catch(IOException e){
            System.err.println("Could not find sample.fxml");
        }
    }

    public void reload(){
//        connection.sendImage();
        Image frame = connection.getImage();
        if(frame != null){
            BackgroundImage myImage = new BackgroundImage(frame, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
            background.setBackground(new Background(myImage));
        }else{
            endCall();
        }
    }

    @FXML
    public void initialize(){
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        height = primaryScreenBounds.getWidth() * 0.495;
        width = primaryScreenBounds.getWidth() * 0.8;
        BackgroundImage myImage = new BackgroundImage(new Image("img/pewdiepie.jpg", width, height, false,false), BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        background.setBackground(new Background(myImage));
        endCallButton.setOnAction(e->endCall());
        ImageView imageView = new ImageView(new Image("img/call.png"));
        imageView.setFitHeight(30);
        imageView.setFitWidth(30);
        endCallButton.setGraphic(imageView);
    }

    @Override
    public void run() {
        while(true) { reload(); }
    }
}
