package sample;

import com.github.sarxos.webcam.Webcam;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
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
//        Parent root = FXMLLoader.load(getClass().getResource("/sample.fxml"));
//        primaryStage.setTitle("Hello World");
//        primaryStage.setScene(new Scene(root, 300, 275));
//        primaryStage.show();
//        Connect connetion = new Connect("localhost", 1234);
//        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
//        System.out.println("Enter username");
//
//        String userName = myObj.nextLine();  // Read user input
//        connetion.sendNick(userName);
//
//        connetion.receiveInfo();
//        connetion.disconnect();

        Webcam webcam = Webcam.getDefault();
        System.out.println("mhm");
        webcam.open();
        System.out.println("lol");

    }


    public static void main(String[] args) {
        launch(args);
    }
}
