package sample;

import com.github.sarxos.webcam.Webcam;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
//import org.opencv.core.Core;
//import org.opencv.core.CvType;
//import org.opencv.core.Mat;
//import org.opencv.videoio.VideoCapture;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;
import java.util.List;
import java.util.Scanner;

public class Main extends Application {

    private Controller controller = new Controller();

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample.fxml"));
        loader.setController(controller);
        Parent root = loader.load();
        primaryStage.setTitle("Spyke");
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        double height = primaryScreenBounds.getWidth() * 0.494;
        double width = primaryScreenBounds.getWidth() * 0.8;
        primaryStage.setScene(new Scene(root, width, height));
        primaryStage.show();
    }

    public void stop(){
        controller.stopApp();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
