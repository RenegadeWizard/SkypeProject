package sample;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Connect {
    private String serverIp;
    private int port;
    private Socket socket;

    public Connect(String serverIp, int port) throws IOException {
        this.serverIp = serverIp;
        this.port = port;
        socket = new Socket(serverIp, port);
    }

    public void disconnect() throws IOException{
        socket.close();
    }

    public void sendNick(String nick) throws IOException{
        String encapsulatedNick = "N" + nick;
        OutputStream os = socket.getOutputStream();
        os.write(encapsulatedNick.getBytes());
    }

    public void connectTo(String nick) throws IOException{
        String encapsulatedNick = "C" + nick;
        OutputStream os = socket.getOutputStream();
        os.write(encapsulatedNick.getBytes());
    }

}
