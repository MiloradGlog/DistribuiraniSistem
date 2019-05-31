package solution.socket;

import com.google.gson.Gson;
import solution.peer.commPackage.CommPackage;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class MySocket {

    private Gson gson;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public MySocket(String host, int port) throws IOException, UnknownHostException {
        this(new Socket(host, port));
    }

    public MySocket(Socket socket) throws IOException {
        this.gson = new Gson();
        this.socket = socket;
        this.inputStream = this.socket.getInputStream();
        this.outputStream = this.socket.getOutputStream();
        this.dataInputStream = new DataInputStream(inputStream);
        this.dataOutputStream = new DataOutputStream(outputStream);
    }

    public void close() throws IOException {
        this.dataInputStream.close();
        this.dataOutputStream.close();
        this.inputStream.close();
        this.outputStream.close();
        this.socket.close();
    }

    public CommPackage read() throws IOException {
        return gson.fromJson(this.dataInputStream.readUTF(), CommPackage.class);
    }

    public void write(CommPackage p) throws IOException {
        this.dataOutputStream.writeUTF(gson.toJson(p));
        this.dataOutputStream.flush();
    }

}
