package solution.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public interface MySocketInterface {

    void write(byte[] var1) throws IOException;

    int read() throws IOException;

    int read(byte[] var1) throws IOException;

    void close() throws IOException;

}
