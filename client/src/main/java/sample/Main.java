package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
//        Parent root = FXMLLoader.load(getClass().getResource("/sample.fxml"));
//        primaryStage.setTitle("Hello World");
//        primaryStage.setScene(new Scene(root, 300, 275));
//        primaryStage.show();
        Connect connetion = new Connect("localhost", 1234);
        connetion.sendNick("Renegade");
//        connetion.connectTo("halo");

        connetion.receiveInfo();
        connetion.disconnect();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
