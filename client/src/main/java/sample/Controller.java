package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class Controller {
    @FXML
    private VBox contacts;

    @FXML
    private TextField ip;

    @FXML
    private TextField port;

    @FXML
    private TextField nick;

    @FXML
    public void initialize(){
        HBox hbox = new HBox();
        Button mainButton = new Button();
        Image image = new Image("img/default-profile.jpg");
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);
        mainButton.setGraphic(imageView);
        Button callButton = new Button();
        ImageView callImage = new ImageView(new Image("img/call.png"));
        callImage.setFitWidth(30);
        callImage.setFitHeight(30);

        callButton.setGraphic(callImage);
        mainButton.getStyleClass().add("contact");
        callButton.getStyleClass().add("call");
        hbox.getChildren().addAll(mainButton, callButton);
        hbox.getStyleClass().add("hbox");
        contacts.getChildren().addAll(hbox);
    }

    private boolean validIp(String ip){
        return ip.matches("(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");
    }

    private boolean validPort(String port){
        try {
            int p = Integer.parseInt(port);
            return p < 65535 && p > 0;
        }catch(NumberFormatException e){
            return false;
        }
    }

    @FXML
    public void connect(){
        boolean exit = false;
        if(!validIp(ip.getText())){
            ip.getStyleClass().remove("input");
            ip.getStyleClass().add("wrong");
            exit = true;
        }else{
            ip.getStyleClass().remove("wrong");
            ip.getStyleClass().add("input");
        }

        if(!validPort(port.getText())){
            port.getStyleClass().remove("input");
            port.getStyleClass().add("wrong");
            exit = true;
        }else{
            port.getStyleClass().remove("wrong");
            port.getStyleClass().add("input");
        }

        if(nick.getText().isEmpty() || nick.getText().length() > 15){
            nick.getStyleClass().remove("input");
            nick.getStyleClass().add("wrong");
            exit = true;
        }else{
            nick.getStyleClass().remove("wrong");
            nick.getStyleClass().add("input");
        }
        if(exit)
            return;


    }
}
