package sample;

import com.sun.corba.se.impl.orbutil.concurrent.Mutex;
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
    private Mutex mutex;

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
        mutex = new Mutex();
    }

    public void lockMutex() throws InterruptedException{
        mutex.acquire();
    }

    public void unlockMutex() throws InterruptedException{
        mutex.release();
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
        System.out.println(line);
        return line.toString();
    }

    public String readLow(int bytes) throws IOException{
        char[] buff = new char[bytes];
        int n = reader.read(buff, 0, bytes-1);
        try {
            buff[n] = 0;
        }catch(ArrayIndexOutOfBoundsException ex){
            System.out.println("n = " + n);
            ex.printStackTrace();
        }
        return new String(buff);
    }

    private String readPhoto() throws IOException{
        StringBuilder buff = new StringBuilder(readLow(1000));
        StringBuilder photo;
        if(buff.toString().indexOf(0) != 'O')
            return "Fail";
        int i;
        i = buff.toString().indexOf('P');
        if(i < 0){
            while ((i = buff.toString().indexOf('P')) < 0){
                buff.append(readLow(100));
            }
        }

        if(i >=0 )
            photo = new StringBuilder(buff.substring(i));
        else
            photo = new StringBuilder("");
        buff = new StringBuilder(buff.substring(1, i - 1));
        int bytes = Integer.parseInt(buff.toString());
        int juz = photo.length();
        while (juz < bytes - 1){
            if(bytes - juz < 1000)
                buff = new StringBuilder(readLow(bytes - juz + 1));
            else
                buff = new StringBuilder(readLow(1000));
            photo.append(buff);
            juz += buff.length();
        }
        return photo.toString();
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
        try {
            System.out.println(nick);
            nickFrom = nick;
            wantsToConnect.setValue(true);
            lockMutex();
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }

    public Image getImage(){
        try {
            System.out.println("Receive good before read");
            String s = readPhoto();
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
        String msg = "";
        try {
            lockMutex();
            msg = read();
            unlockMutex();
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
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
    public void run(){
        try {
            sendNick();
            while(receiveFromServer()) ;
        }catch (IOException ignored) { }
    }
}
