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

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/sample.fxml"));
        primaryStage.setTitle("Spyke");
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        double height = primaryScreenBounds.getWidth() * 0.494;
        double width = primaryScreenBounds.getWidth() * 0.8;
        primaryStage.setScene(new Scene(root, width, height));
        primaryStage.show();
//        Connect connetion = new Connect("localhost", 1234);
//        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
//        System.out.println("Enter username");
//
//        String userName = myObj.nextLine();  // Read user input
//        connetion.sendNick(userName);
//
//        connetion.receiveInfo();
//        connetion.disconnect();

//        Webcam webcam = Webcam.getDefault();
//        List webcam = Webcam.getWebcams(2000);
//        Webcam webcam = Webcam.get
//        System.out.println("mhm");

//        webcam.open();
//        System.out.println("lol");

    }


    public static void main(String[] args) {
        launch(args);
    }
}
