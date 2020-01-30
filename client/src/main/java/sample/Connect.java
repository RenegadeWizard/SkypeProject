package sample;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class Connect implements Runnable{
    private Socket socket;
    private ObservableList<String> users;

    public BooleanProperty isWantsToConnect() {
        return wantsToConnect;
    }

    private BooleanProperty wantsToConnect = new SimpleBooleanProperty(false);

    public String getNickFrom() {
        return nickFrom;
    }

    private String nickFrom;
    private String nick;
    private BufferedReader reader;
    private int howMuch = -1;


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
        while (read().charAt(0) != 'D');
        System.out.println("Disconnected!");
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
        StringBuilder line = new StringBuilder(reader.readLine());
        int ile = line.length();
        if(line.charAt(0) == 'O'){
            System.out.println("Bytes: " + line.substring(1));
            howMuch = Integer.parseInt(line.substring(1)) - 1;
        }else{
            while (ile < howMuch){
                line.append(reader.readLine());
                ile += line.length();
            }
        }
        return line.toString();
    }

    public void write(String msg) throws IOException{
        OutputStream os = socket.getOutputStream();
        os.write(("O" + msg.length() + "\n").getBytes());
        os.write(msg.getBytes());
    }

    public void receiveClients() throws IOException{
        users.clear();
        while(true){
            String msg = read();
            if(msg.charAt(0) == 'E')
                break;
            if(msg.substring(1).equals("clients") || msg.charAt(0) == 'O') {
                continue;
            }
            System.out.println(msg.substring(1));
            users.add(msg.substring(1));
        }
    }

    public void connectionFrom(String nick){
        System.out.println(nick);
        nickFrom = nick;
        wantsToConnect.setValue(true);
    }

    public Image getImage(){
        try {
            System.out.println("Receive good before read");
            String s = read();
            System.out.println("Receive good after read");
            Image img = null;
            if(s.charAt(0) == 'P'){
                img = new Image(new ByteArrayInputStream(s.substring(1).getBytes()));
            }else if(s.charAt(0) == 'G'){
                img = null;
            }

            return img;
        }catch (IOException ignored){
            System.err.println("Receive image not good");
        }
        return null;
    }

    public void sendImage(){
        try {
            Image img = imgFromVideo("img/pewdiepie2.jpg");
            BufferedImage image = ImageIO.read(new File("img/pewdiepie2.jpg"));
            String d = image.toString();
            int w = (int)img.getWidth();
            int h = (int)img.getHeight();
            byte[] buf = new byte[w * h * 4];
            img.getPixelReader().getPixels(0, 0, w, h, PixelFormat.getByteBgraInstance(), buf, 0, w * 4);
            String s = new String(buf);
            s = "P" + s;
            write(s);
            System.out.println("Send good");
        }catch (IOException ignored) {
            System.err.println("Send image not good");
            ignored.printStackTrace();
        }
    }

    private Image imgFromVideo(String file) throws IOException{
        Image frame = new Image(file);
        return frame;
    }

    private Image imgFromCam() throws IOException{
        return null;
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
            case 'R':
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
