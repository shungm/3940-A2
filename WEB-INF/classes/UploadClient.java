import java.io.*;
import java.net.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;
import java.util.Base64;


public class UploadClient {

    private String rawFileName;
    private String rawDate;
    private String rawCaption;
    private String fileName;
    private String boundary = "--------------1234578890567";

    public UploadClient() {
        fileName = "empty";
    }
    public UploadClient(String fileString) {
        try {
            fileName = fileString;

            String[] brokenData = fileString.split("_");
            rawFileName = brokenData[0];
            rawDate = brokenData[1];
            rawCaption = brokenData[2];
            String fileExtension = "";
            int index = rawFileName.lastIndexOf('.');
            if (index > 0) fileExtension = rawFileName.substring(index + 1);
            if (!(fileExtension.matches("(?i)png|jpeg|jpg|gif"))) {
                throw new IncorrectFileTypeException("Can only upload png/jpeg/jpg/gif images");
            }
        } catch (IncorrectFileTypeException e) {
            System.out.println("Exception caught - " + e);
        }

    }

        public String uploadFile() {
        
            String listing = "";
            String endMessage = "\r\n\r\n";
    
            try {
                Socket socket = new Socket("localhost", 8999);
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
                OutputStream out = socket.getOutputStream();
    
                FileInputStream fis = new FileInputStream(rawFileName);
                int bufLength = 2048;
                byte[] buffer = new byte[2048];
                byte[] data;
    
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int readLength;
                while ((readLength = fis.read(buffer, 0, bufLength)) != -1) {
                    baos.write(buffer, 0, readLength);
                }
    
                data = baos.toByteArray();
    
                String imageString = Base64.getEncoder().withoutPadding().encodeToString(data);
    
                fis.close();
    
                //Get the length of the message for the header
                int messageLength = getMessageBody(fileName, imageString).length();
    
                //Start the POST Message
                out.write(getHeaderMessage(messageLength).getBytes()); 
                
                //Write thr fileName and image as parameters in the header
                out.write(getMessageBody(fileName, imageString).getBytes());
                
                //End the POST
                out.write(endMessage.getBytes());
                socket.shutdownOutput();
    
    
                //Get the return from the server as a listing of all images
                System.out.println("Getting reply from server...\n");
                String filename = "";
                while ((filename = in.readLine()) != null) {
                    listing += filename;
                }
                //Shutdown the output to the server
                socket.shutdownInput();
                socket.close();
            } catch (Exception e) {
                System.err.println(e);
            }
            return listing;
        }

        public String getHeaderMessage(int messageLen) {

            StringBuilder builder = new StringBuilder();
            builder.append("POST /asn2/upload HTTP/1.0");
            builder.append("\r\n");
    
            builder.append("Host: localhost:8999");
            builder.append("\r\n");
    
            builder.append("Content-Type: multipart/form-data;boundary=" + boundary);
            builder.append("\r\n");
    
            builder.append("Content-Length: " + messageLen);
            builder.append("\r\n");
            builder.append("\n");
            
            return builder.toString();
        }
        private String getMessageBody(String fileName, String image) {
            return 
                boundary + "\n"
                + "Content-Disposition: form-data; name=\"fileName\"" + "\n"
                + "\n"
                + rawFileName + "\n"
    
            + boundary + "\n"
                + "Content-Disposition: form-data; name=\"image\"" + "\n"
                + "Content-Type: image/base64" + "\n"
                + "\n"
                + image + "\n"
    
                + boundary + "\n"
                + "Content-Disposition: form-data; name=\"date\"" + "\n"
                + "\n"
                + rawDate + "\n"
    
                + boundary + "\n"
                + "Content-Disposition: form-data; name=\"caption\"" + "\n"
                + "\n"
                + rawCaption + "\n"
    
                + boundary + "--";
       }
}
