import java.io.*;

public class HttpRequest {
    private InputStream inputStream = null;

    public HttpRequest (InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public InputStream getInputStream() {
        return this.inputStream;
    }


}