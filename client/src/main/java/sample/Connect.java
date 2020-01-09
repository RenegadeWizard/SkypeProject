package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Connect implements Runnable{
    private Socket socket;
    private ObservableList<String> users;
    private boolean wantsToConnect = false;
    private String nick;

    public String getNick() { return nick; }
    public void setNick(String nick) { this.nick = nick; }

    public ObservableList<String> getUsers() {
        return users;
    }

    public Connect(){
        users = FXCollections.observableArrayList();
    }

    public void connectSocket(String serverIp, int port) throws IOException{
        socket = new Socket(serverIp, port);
    }

    public void disconnect() throws IOException{
        String encapsulatedNick = "Disconnect";
        OutputStream os = socket.getOutputStream();
        os.write(encapsulatedNick.getBytes());  // TODO: receive message from server (success)
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String msg = reader.readLine();
        if(msg.charAt(0) == 'D'){
            System.out.println("Disconnected!");
        }
        socket.close();
    }

    public void sendNick() throws IOException{
        String encapsulatedNick = "N" + nick;
        OutputStream os = socket.getOutputStream();
        os.write(encapsulatedNick.getBytes());
    }

    public void requestUsersList() throws IOException{
        String encapsulatedNick = "Lrequest";
        OutputStream os = socket.getOutputStream();
        os.write(encapsulatedNick.getBytes());
    }

    public void connectTo(String nick) throws IOException{
        String encapsulatedNick = "C" + nick;
        OutputStream os = socket.getOutputStream();
        os.write(encapsulatedNick.getBytes());
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String msg = reader.readLine();
        if(msg.equals("ACC")){
            System.out.println("Connected");
        }else{
            System.err.println("Could not connect");
        }
    }

    public void receiveInfo() throws IOException{
        users.clear();
        while(true){
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String msg = reader.readLine();
            if(msg.charAt(0) == 'E')
                break;
            if(msg.substring(1).equals("clients")) {
//                availableClients();
                continue;
            }
            System.out.println(msg.substring(1));
            users.add(msg.substring(1));
        }
    }

    @Override
    public void run() {
        try {
            sendNick();
            requestUsersList();
            receiveInfo();
        }catch (IOException ignored) { }
    }
}
