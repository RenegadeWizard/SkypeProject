package sample;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;


public class Controller {

    private boolean connected = false;
    private Connect connection = new Connect();
    private Thread thread;

    @FXML
    private Button connectButton;

    @FXML
    private VBox contacts;

    @FXML
    private TextField ip;
    private String ipString;

    @FXML
    private TextField port;
    private String portString;

    @FXML
    private TextField nick;
    private String nickString;

    @FXML
    private Label info;

    @FXML
    public BorderPane background;

    @FXML
    public VBox popUpCall;

    @FXML
    public Label popUpLabel;

    @FXML
    public HBox popUpHbox;

    @FXML
    public Button callButton;

    @FXML
    public Button endCallButton;

    public boolean getConnected() { return connected; }

    private BooleanProperty isReceivingConn;

    private void popUp(String nick){
        popUpCall.getStyleClass().clear();
        popUpCall.getStyleClass().add("popUpCall");
        popUpLabel.getStyleClass().clear();
        popUpLabel.getStyleClass().add("popUpLabel");
        popUpHbox.getStyleClass().clear();
        popUpHbox.getStyleClass().add("popUpHbox");
        callButton.getStyleClass().clear();
        callButton.getStyleClass().add("call");
        endCallButton.getStyleClass().clear();
        endCallButton.getStyleClass().add("endCall");

        popUpLabel.setText(nick + " calls");
    }

    private void popUpClear(){
        popUpCall.getStyleClass().clear();
        popUpCall.getStyleClass().add("none");
        popUpLabel.getStyleClass().clear();
        popUpLabel.getStyleClass().add("none");
        popUpHbox.getStyleClass().clear();
        popUpHbox.getStyleClass().add("none");
        callButton.getStyleClass().clear();
        callButton.getStyleClass().add("none");
        endCallButton.getStyleClass().clear();
        endCallButton.getStyleClass().add("none");
    }

    public void callView(String nick){
        Stage stage = (Stage) connectButton.getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/call.fxml"));
            double height = background.getHeight();
            double width = background.getWidth();
            Call callController = new Call(this, nick);
//            connection.connectTo(nick);  TODO
            loader.setController(callController);
            Parent root = loader.load();
            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.show();
        }catch (IOException e){
            System.err.println("Could not find call.fxml");
        }
    }

    public void listClients(){
        contacts.getChildren().clear();
        for(String nick : connection.getUsers()){
//            if(nick.equals(connection.getNick())){    TODO: Don't show yourself
//                continue;
//            }
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
            mainButton.setOnAction(e->popUp(nick));
            callButton.setOnAction(e->callView(nick));
            hbox.getChildren().addAll(mainButton, callButton);
            hbox.getStyleClass().add("hbox");
            VBox container = new VBox();
            Label nickLabel = new Label(nick);
            container.getChildren().addAll(hbox, nickLabel);
            contacts.getChildren().add(container);
        }

    }

    @FXML
    public void rejectCall(){
        popUpClear();
    }

    public void stopApp(){
        if(connected)
            try {
                connection.disconnect();
            }catch (Exception ignored){ }
    }

    public void unlistClients(){
        contacts.getChildren().clear();
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

    private void connectedView(){
        while (connectButton.getStyleClass().remove("connect")) ;
        connectButton.getStyleClass().add("disconnect");
        connectButton.setText("disconnect");
        info.setText("Connected!");
        while (info.getStyleClass().remove("not_connected")) ;
        info.getStyleClass().add("connected");
        ip.setText(ipString);
        port.setText(portString);
        nick.setText(nickString);
        listClients();
    }

    @FXML
    public void connect(){
        if(!connected) {
            boolean exit = false;
            if (!validIp(ip.getText())) {
                while (ip.getStyleClass().remove("input")) ;
                ip.getStyleClass().add("wrong");
                exit = true;
            } else {
                while (ip.getStyleClass().remove("wrong")) ;
                ip.getStyleClass().add("input");
            }

            if (!validPort(port.getText())) {
                while (port.getStyleClass().remove("input")) ;
                port.getStyleClass().add("wrong");
                exit = true;
            } else {
                while (port.getStyleClass().remove("wrong")) ;
                port.getStyleClass().add("input");
            }

            if (nick.getText().isEmpty() || nick.getText().length() > 15) {
                while (nick.getStyleClass().remove("input")) ;
                nick.getStyleClass().add("wrong");
                exit = true;
            } else {
                while (nick.getStyleClass().remove("wrong")) ;
                nick.getStyleClass().add("input");
            }
            if (exit) {
                info.setText("Wrong configuration");
                while (info.getStyleClass().remove("connected")) ;
                info.getStyleClass().add("not_connected");
                return;
            }
            try {
                ipString = ip.getText();
                portString = port.getText();
                nickString = nick.getText();
                connection.connectSocket(ipString, Integer.parseInt(portString));
                connection.setNick(nickString);
                isReceivingConn = new SimpleBooleanProperty(connection.isWantsToConnect());
                isReceivingConn.addListener(e->popUp(connection.getNickFrom()));
                thread = new Thread(connection);
                thread.setDaemon(true);
                thread.start();
                connected = true;
                while (connectButton.getStyleClass().remove("connect")) ;
                connectButton.getStyleClass().add("disconnect");
                connectButton.setText("disconnect");
            } catch (IOException e) {
                info.setText("Wrong configuration");
                while (info.getStyleClass().remove("connected")) ;
                info.getStyleClass().add("not_connected");
                return;
            }

            info.setText("Connected!");
            while (info.getStyleClass().remove("not_connected")) ;
            info.getStyleClass().add("connected");
        }else{
            stopApp();
            unlistClients();
            connected = false;
            while (connectButton.getStyleClass().remove("disconnect"));
            connectButton.getStyleClass().add("connect");
            connectButton.setText("connect");
            info.setText("Disconnected");
        }

    }

    @FXML
    public void initialize(){
        if(connected)
            connectedView();
        connection.getUsers().addListener((ListChangeListener<String>) c -> Platform.runLater(() -> listClients()));
    }

}
