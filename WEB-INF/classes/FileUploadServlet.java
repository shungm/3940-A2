import java.io.*;
import java.net.*;
import java.security.DrbgParameters.Capability;
import java.util.Date;

public class FileUploadServlet {
    private String fileName = null;
    private String dateCreated = null;
    private String keyword = null;
    public Socket socket = null;

    public FileUploadServlet() {

        fileName = "empty";
        dateCreated = new Date().toString();
        keyword = "";

        setSocket();
    }

    public FileUploadServlet(String name, String date, String key) {
        
        this.fileName = name;
        this.dateCreated = date;
        this.keyword = key;

        setSocket();
    }

    public void setSocket() {
        try {
            socket = new Socket("localhost", 8999);
        } catch (Exception e) {}
    }

    public void setSocket(String hostName, int portnum) {
        try {
            socket = new Socket(hostName, portnum);
        } catch (Exception e) {}
    }

    public String uploadFile() {
        
        System.out.println("Uploading file..");

        String uploadData = getUploadData();

        DataOutputStream out;

        try {

            out = new DataOutputStream(socket.getOutputStream());

            out.writeBytes("POST /midp/upload HTTP/1.0");
            out.writeBytes("\r\n");

            out.writeBytes("Host: localhost:8999");
            out.writeBytes("\r\n");

            out.writeBytes("Content-Type: multipart/form-data; boundary=----WebKitFormBoundaryD9JJQuX4ZDbHlYQy");
            out.writeBytes("\r\n");

            out.writeBytes("Content-Length: " + uploadData.length());
            out.writeBytes("\r\n");

            out.writeBytes(uploadData);

            out.writeBytes("\r\n\r\n");
            out.close();


        } catch (IOException ioe) {
            System.out.println("Fail to upload: " + ioe);
        }

        return fileName;

    }

    private String getUploadData() {
        return 
           "------WebKitFormBoundaryD9JJQuX4ZDbHlYQy\n"
           + "Content-Disposition: form-data; name=\"fileName\"\n"
           + "\n"
           + fileName + "\n\n"

           + "------WebKitFormBoundaryD9JJQuX4ZDbHlYQy\n"
           + "Content-Disposition: form-data; name=\"date\"\n"
           + "\n"
           + dateCreated + "\n\n"

           + "------WebKitFormBoundaryD9JJQuX4ZDbHlYQy\n"
           + "Content-Disposition: form-data; name=\"caption\"\n"
           + "\n"
           + keyword + "\n\n"
           
           + "------WebKitFormBoundaryD9JJQuX4ZDbHlYQy--\n";
           
   }

}
