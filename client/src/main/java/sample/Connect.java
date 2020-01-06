package sample;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Connect {
    private Socket socket;
    private ArrayList<String> users;

    public ArrayList<String> getUsers() {
        return users;
    }

    public Connect(String serverIp, int port) throws IOException {
        socket = new Socket(serverIp, port);
        users = new ArrayList<>();
    }

    public void disconnect() throws IOException{
        String encapsulatedNick = "Disconnect";
        OutputStream os = socket.getOutputStream();
        os.write(encapsulatedNick.getBytes());  // TODO: receive message from server (success)
        socket.close();
    }

    public void sendNick(String nick) throws IOException{
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
    }

    public void receiveInfo() throws IOException{
        while(true){
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String msg = reader.readLine();
            if(msg.charAt(0) == 'E')
                break;
            if(msg.substring(1).equals("clients")) {
//                availableClients();
                continue;
            }
//            System.out.println(msg.substring(1));
            users.add(msg.substring(1));
        }
    }

    public void availableClients() throws IOException{
        System.out.println("Available clients:");
    }

}
