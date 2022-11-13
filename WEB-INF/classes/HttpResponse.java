import java.io.*;

public class HttpResponse {
    private OutputStream outputStream = null;

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }
    public OutputStream getOutputstream() {
        return this.outputStream;
    }
}