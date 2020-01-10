package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.Socket;

public class Connect implements Runnable{
    private Socket socket;
    private ObservableList<String> users;

    public boolean isWantsToConnect() {
        return wantsToConnect;
    }

    private boolean wantsToConnect = false;

    public String getNickFrom() {
        return nickFrom;
    }

    private String nickFrom;
    private String nick;
    private BufferedReader reader;


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
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void disconnect() throws IOException{
        String encapsulatedNick = "Disconnect";
        write(encapsulatedNick);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String msg = reader.readLine();
        if(msg.charAt(0) == 'D'){
            System.out.println("Disconnected!");
        }
        socket.close();
    }

    public void sendNick() throws IOException{
        String encapsulatedNick = "N" + nick;
        write(encapsulatedNick);
        System.out.println("Sent nick");
        while(read().charAt(0) != 'Z');
    }

    public void requestUsersList() throws IOException{
        String encapsulatedNick = "Lrequest";
        write(encapsulatedNick);
        System.out.println("requesting clients list");
        while(read().charAt(0) != 'S');
    }

    public void connectTo(String nick) throws IOException{
        String encapsulatedNick = "C" + nick;
        write(encapsulatedNick);
    }

    public void connection(){
        System.out.println("connected!");

    }

    public void notConnected(){
        System.out.println("not connected!");
    }

    public String read() throws IOException{
        String line = reader.readLine();
        while (line.isEmpty()){
            line = reader.readLine();
        }
        return line;
    }

    public void write(String msg) throws IOException{
        OutputStream os = socket.getOutputStream();
        os.write(msg.getBytes());
    }

    public void receiveClients() throws IOException{
        users.clear();
        while(true){
            String msg = read();
            if(msg.charAt(0) == 'E')
                break;
            if(msg.substring(1).equals("clients")) {
                continue;
            }
            System.out.println(msg.substring(1));
            users.add(msg.substring(1));
        }
    }

    public void connectionFrom(String nick){
        System.out.println(nick);
        nickFrom = nick;
        wantsToConnect = true;
    }

    public boolean receiveFromServer() throws IOException{
        String msg = read();
        switch (msg.charAt(0)){
            case 'S':
                receiveClients();
                break;
            case 'C':
                connectionFrom(msg.substring(1));
                break;
            case 'A':
                connection();
                break;
            case 'F':
                notConnected();
                break;
            case 'D':
                return false;
            default:
                break;
        }
        return true;
    }

    @Override
    public void run() {
        try {
            sendNick();
            requestUsersList();
            receiveClients();
            while(receiveFromServer()) ;
        }catch (IOException ignored) { }
    }
}
