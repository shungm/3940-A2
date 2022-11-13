import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class UploadServlet extends HttpServlet {
   private OutputStream servletBaos;
   private BufferedReader br;

   public UploadServlet() {
      servletBaos = new ByteArrayOutputStream();

   }

   public void requestHandler(HttpRequest req, HttpResponse res) throws IOException {
      br = new BufferedReader(new InputStreamReader(req.getInputStream()));
      String inputLine = br.readLine();;

      if (inputLine.contains("GET")) {
         doGet(req,res);

      }
      if (inputLine.contains("POST")) {
         doPost(req, res);
      }
   }
   public void doPost(HttpRequest request, HttpResponse response) {
      try {
         //baos used to write to file
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         String inputLine;
         String requestString = "";
         while (br.ready() && (inputLine = br.readLine()) != null) {
            requestString += inputLine + "\n";
         }


         baos.write(requestString.getBytes());

         //Parse request
         ParsedRequest parsedRequest = new ParsedRequest(requestString);
         String caption = "";
         String date = "";
         String imgName = "";
         String file = "";
         String extension = "";

         for(Part part: parsedRequest.getParts()) {
            String header = part.getHeader().replace("\"", "");
            if (header.equals("caption") || header.equals("rawCaption")) {
               caption = part.getContent();
            }
            else if (header.equals("date") || header.equals("rawDate")) {
               date = part.getContent();
            }
            else if (header.equals("fileName")){
               imgName = part.getContent();
               extension = imgName.substring(imgName.lastIndexOf(".") + 1);
            } else if (header.equals("image")){
               file = part.getContent();
            } else {
               imgName = header;
               file = part.getContent();
               extension = imgName.substring(imgName.lastIndexOf(".") + 1);
            }
         }
         if (!(extension.matches("(?i)png|jpeg|jpg|gif"))) {
            throw new IncorrectFileTypeException("Only file formats: png/jpg/jpeg/gif are allowed.");
         }
         String newFileName = imgName + "_" + date + "_" + caption + "." + extension;


         //FILE WRITING STARTS HERE
         if (parsedRequest.getBase64Encoded()) {

            byte[] data = Base64.getMimeDecoder().decode(file.getBytes(StandardCharsets.UTF_8));

            OutputStream outputStream = new FileOutputStream(new File("..\\..\\images\\"+ newFileName));

            outputStream.write(data);
            outputStream.close();

            //NOT BASE ENCODED FILE WRITE HERE
         } else {
            OutputStream newImg = new BufferedOutputStream(new FileOutputStream("..\\..\\images\\" + newFileName));

            newImg.write(file.getBytes());
            newImg.close();
         }



            File dir = new File("..\\..\\images\\");

            String[] child = dir.list();
            List<String> toBeSorted = Arrays.asList(child);
            List<String> sortedList = toBeSorted.stream().sorted((s1, s2) -> s1.compareTo(s2)).collect(Collectors.toList());
            String[] sortedChild = new String[sortedList.size()];
            sortedChild = sortedList.toArray(sortedChild);
      

            boolean isBrowser = !parsedRequest.getBase64Encoded();
            if (isBrowser) {
               String body = getListing("..\\..\\images\\");
               int bodyLength = body.length();
               String header = createGetHeader(bodyLength);

               servletBaos.write(header.getBytes());
               servletBaos.write(body.getBytes());
            } else {
               String jsonObj = "{\"fileNames\":[";
               for (String fileName : sortedChild) {
                  jsonObj += "{\"fileName\":\"" + fileName + "\"},";
               }
               jsonObj = jsonObj.substring(0, jsonObj.length()-1);
               jsonObj += "]}";

               servletBaos.write(jsonObj.getBytes());
            }

      } catch (Exception ex) {
         System.err.println(ex);
      }
   }

   public void doGet(HttpRequest request, HttpResponse response) {
      try {
         int messageLen = createGetMessageBody().length();
         String header = createGetHeader(messageLen);
         String body = createGetMessageBody();

         servletBaos.write(header.getBytes());
         servletBaos.write(body.getBytes());

      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   private String getListing(String path) {
      String dirList =  "<!DOCTYPE html>" +
              "<html><head><title>File Upload Results</title></head>" +
              "<body><ul>";
      File dir = new File(path);
      String[] child = dir.list();
      List<String> toBeSorted = Arrays.asList(child);
      List<String> sortedList = toBeSorted.stream().sorted((s1, s2) -> s1.compareTo(s2)).collect(Collectors.toList());
      String[] sortedChild = new String[sortedList.size()];
      sortedChild = sortedList.toArray(sortedChild);


      for (String string : sortedChild) {
         if ((new File(path + string)).isDirectory())
            dirList += "<li><button type=\"button\">" + string + "</button></li>";
         else
            dirList += "<li>" + string + "</li>";
      }
      dirList += "</ul></body></html>";
      return dirList;
   }

   private String createGetHeader(int messageLen) {
      String endLine = "\r\n";
      SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
      sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
      String httpDate = sdf.format(new Date());

      StringBuilder builder = new StringBuilder();
      builder.append("HTTP/1.1 200 OK")
              .append(endLine);
      builder.append("Date: ").append(httpDate)
              .append(endLine);
      builder.append("Server: MyLaptop").append(httpDate)
              .append(endLine);
      builder.append("Connection: close")
              .append(endLine);
      builder.append("Content-Length: ").append(messageLen)
              .append(endLine);
      builder.append("Content-Type: text/html")
         .append(endLine)
         .append("\n");

      return builder.toString();
   }

   private String createGetMessageBody() {
      return
      "<!DOCTYPE html>" +
              "<html><head><title>File Upload Form</title></head>" +
              "<body><h1>Upload file</h1>" +
              "<form method=\"POST\" action=\"/\" " +
              "enctype=\"multipart/form-data\">" +
              "<input type=\"file\" name=\"fileName\"/><br/><br/>" +
              "Caption: <input type=\"text\" name=\"caption\"<br/><br/>" +
              "<br />" +
              "Date: <input type=\"date\" name=\"date\"<br/><br/>" +
              "<br />" +
              "<input type=\"submit\" value=\"Submit\"/>" +
              "</form>" +
              "</body></html>"
              + "\r\n\n";
   }

   public ByteArrayOutputStream getServletBaos() {
      return (ByteArrayOutputStream) servletBaos;
   }
}