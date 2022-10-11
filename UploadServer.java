import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.nio.file.Path;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

public class UploadServer extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        System.out.println("DoGet working");
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        writer.append("<!DOCTYPE html>\r\n")
                .append("<html>\r\n")
                .append("    <head>\r\n")
                .append("        <title>File Upload Form</title>\r\n")
                .append("    </head>\r\n")
                .append("    <body>\r\n");
        writer.append("<h1>Upload file</h1>\r\n");
        writer.append("<form method=\"POST\" action=\"upload\" ")
                .append("enctype=\"multipart/form-data\">\r\n");
        writer.append("<input type=\"file\" name=\"fileToUpload\"/><br/><br/>\r\n");
        writer.append("Caption: <input type=\"text\" name=\"caption\"<br/><br/>\r\n");
        writer.append("<br />\n");
        writer.append("Date: <input type=\"date\" name=\"date\"<br/><br/>\r\n");
        writer.append("<br />\n");
        writer.append("<input type=\"submit\" value=\"Submit\"/>\r\n");
        writer.append("</form>\r\n");
        writer.append("</body>\r\n").append("</html>\r\n");
    }


    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try{
            Part filePart = request.getPart("fileToUpload");
            String fileName = filePart.getSubmittedFileName();
            InputStream fileInputStream = filePart.getInputStream();
            File fileToSave = new File("./images" + fileName + ".png");
            Files.copy(fileInputStream, fileToSave.toPath(), StandardCopyOption.REPLACE_EXISTING);
            /*OutputStream outputStream = new FileOutputStream(new File("./images/" + fileName + ".png"));
            outputStream.close();

            PrintWriter out = new PrintWriter(response.getOutputStream(), true);
            File dir = new File("./images");*/
        }catch(Exception e){
            System.err.println(e);
        }

    }
}
