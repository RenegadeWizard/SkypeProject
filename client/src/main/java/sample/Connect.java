package sample;

import com.sun.corba.se.impl.orbutil.concurrent.Mutex;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class Connect implements Runnable{
    private Socket socket;
    private ObservableList<String> users;
    private Mutex mutex, accept;
    private String buffer = "";
    private int bytesToRead;

    public void nowIsAccepted() throws InterruptedException{
        accept.acquire();
    }

    public void nowAccept() throws InterruptedException{
        accept.release();
    }

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
    public Mutex clientsListMutex;


    public String getNick() { return nick; }
    public void setNick(String nick) { this.nick = nick; }

    public ObservableList<String> getUsers() {
        return users;
    }

    public Connect(){
        users = FXCollections.observableArrayList();
        mutex = new Mutex();
        accept = new Mutex();
        clientsListMutex = new Mutex();
        try {
            nowIsAccepted();
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
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
        while (readEverything().charAt(0) != 'D');
        System.out.println("Disconnected!");
        socket.close();
    }

    public void sendNick() throws IOException{
        String encapsulatedNick = "N" + nick;
        write(encapsulatedNick);
        System.out.println("Sent nick");
        while(readEverything().charAt(0) != 'Z');
    }

    public void requestUsersList() throws IOException{
        String encapsulatedNick = "Lrequest";
        write(encapsulatedNick);
        System.out.println("requesting clients list");
        while(readEverything().charAt(0) != 'S');
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


    public String readEverything() throws IOException{
        String tempBuffer = "";
        char[] temp = new char[1000];
        int n;
        while(buffer.indexOf('\n') < 0){
            n = reader.read(temp, 0, 999);
            if (n == -1)
                return "";
            temp[n] = 0;
            buffer += new String(temp);
        }
        if(buffer.indexOf('\0') > 0){
            buffer = buffer.substring(0, buffer.indexOf('\0'));
        }
        if(buffer.charAt(0) == 'O'){
            bytesToRead = Integer.parseInt(buffer.substring(1, buffer.indexOf('\n')));
            buffer = buffer.substring(buffer.indexOf('\n') + 1);
        }
        int count = buffer.length();
        while (buffer.length() < bytesToRead - 1){
            n = reader.read(temp, 0, 999);
            if (n == -1)
                return "";
            temp[n] = 0;
            buffer += new String(temp);
            count += n;
            if(buffer.indexOf('\0') > 0){
                buffer = buffer.substring(0, buffer.indexOf('\0'));
            }
        }

        tempBuffer = buffer.substring(0, bytesToRead - 1);
        buffer = buffer.substring(bytesToRead);
        System.out.print("Reading: ");
        System.out.println(tempBuffer.length());
        return tempBuffer;
    }



    public void write(String msg) throws IOException{
        OutputStream os = socket.getOutputStream();
        os.write(("O" + msg.length() + "\n").getBytes());
        System.out.print("Sending: ");
        System.out.println(msg.length());
        os.write(msg.getBytes());
    }

    public void receiveClients() throws IOException{
        try {
            clientsListMutex.acquire();
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
        users.clear();
        while(true){
            String msg = readEverything();
            if(msg.charAt(0) == 'E')
                break;
            if(msg.substring(1).equals("clients")) {
                continue;
            }
            System.out.println(msg.substring(1));
            users.add(msg.substring(1));
        }
        clientsListMutex.release();
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
            String s = readEverything();
            System.out.println("Receive good after read");
            Image img = null;
            if(s.charAt(0) == 'P'){
//                img = new Image(new ByteArrayInputStream(s.substring(1, s.length()-1).getBytes()));
//                Files.write((new File("/Users/krzysztof/Documents/Studia/semestr 5/sieci/client/src/main/resources/img/readImg.jpg")).toPath(), s.substring(1).getBytes());
                OutputStream os = new FileOutputStream(new File("/Users/krzysztof/Documents/Studia/semestr 5/sieci/client/src/main/resources/img/readImg.jpg"));
                os.write(s.substring(1).getBytes(), 0, s.substring(1).length());
                os.flush();
                os.close();
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

    public void sendText(){
        try {
            String s = new String(Files.readAllBytes(Paths.get("/Users/krzysztof/Documents/Studia/semestr 5/sieci/client/src/main/resources/lorem.txt")));
            s = "P" + s;
            write(s);
            System.out.println("Send good");
        }catch (IOException ignored) {
            System.err.println("Send image not good");
            ignored.printStackTrace();
        }
    }

    public void getText(){
        try {
            System.out.println("Receive good before read");
            String s = readEverything();
            System.out.println("Receive good after read");
            OutputStream os = new FileOutputStream(new File("/Users/krzysztof/Documents/Studia/semestr 5/sieci/client/src/main/resources/lorem2.txt"));
            os.write(s.substring(1).getBytes(), 0, s.substring(1).length());
//            System.out.println(s);
        }catch (IOException ignored){
            System.err.println("Receive image not good");
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
            msg = readEverything();
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
                accept.release();
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
