import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;

    public static void main(String[] args) throws IOException{
/*        try(ServerSocket serverSocket = new ServerSocket(8999)){
            System.out.println("listening to port:8999");
            Socket clientSocket = serverSocket.accept();
            System.out.println(clientSocket + " connected\n");
            dataInputStream = new DataInputStream(clientSocket.getInputStream());
            dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

            String message;
            while(true){
                message = dataInputStream.readUTF();
                System.out.println(message);
                if(message.equalsIgnoreCase("exit()"))
                    break;
            }
            clientSocket.close();
        }catch(Exception e){
            System.out.println(e.toString());
        }*/

/*        ServerSocket serverSocket = null;
        try{
            serverSocket = new ServerSocket(8999);
        }catch(IOException e){
            System.err.println("Port 8999 could not be listened");
            System.exit(-1);
        }
        while(true){
            new ServerThread(serverSocket.accept()).start();
        }*/

        try(ServerSocket serverSocket = new ServerSocket(8999)){
            while(true){
                try(Socket client = serverSocket.accept()){
                    handleClient(client);
                    //parseRequest(client);
                }catch(IOException e){
                    System.err.println("Port not listened");
                    System.exit(-1);
                }
            }
        }
    }

    private static void handleClient(Socket client) throws IOException{
        System.out.println("New client" + client.toString());
        BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));

        StringBuilder requestBuilder = new StringBuilder();
        String line;
        while(!(line = br.readLine()).isBlank()){
            requestBuilder.append(line + "\r\n");
        }
        String request = requestBuilder.toString();
        System.out.println(request);

        //String request = requestBuilder.toString();
        String[] requestsLines = request.split("\r\n");
        String[] requestLine = requestsLines[0].split(" ");
        String method = requestLine[0];
        String path = requestLine[1];
        String version = requestLine[2];
        String host = requestsLines[1].split(" ")[1];

        List<String> headers = new ArrayList<>();
        for(int i = 2; i < requestsLines.length; i++){
            String header = requestsLines[i];
            headers.add(header);
        }

        String accessLog = String.format("Client %s, method %s, path%s, version %s, host%s, headers %s",
                client.toString(), method, path, version, host, headers.toString());
        System.out.println(accessLog);

        OutputStream clientOutput = client.getOutputStream();
        clientOutput.write("HTTP/1.1 200 OK\r\n".getBytes());
        clientOutput.write(("ContentType: text/html\r\n").getBytes());
        clientOutput.write("\r\n".getBytes());
        clientOutput.write("<html>\r\n".getBytes());
        clientOutput.write("<head>\r\n".getBytes());
        clientOutput.write("<title> File Upload </title>\r\n".getBytes());
        clientOutput.write("</head>\r\n".getBytes());
        clientOutput.write("<body>\r\n".getBytes());
        clientOutput.write("<h1> Upload File </h1> \r\n".getBytes());
        clientOutput.write("<form method=\"POST\" action=\"upload\"".getBytes());
        clientOutput.write("\r\n\r\n".getBytes());
        clientOutput.write("enctype=\"multipart/form-data\">\r\n".getBytes());
        clientOutput.write("<input type=\"file\" name=\"fileName\"/><br/><br/>\r\n".getBytes());
        clientOutput.write("Caption: <input type=\"text\" name=\"caption\"<br/><br/>\r\n".getBytes());
        clientOutput.write("<br />\n".getBytes());
        clientOutput.write("Date: <input type=\"date\" name=\"date\"<br/><br/>\r\n".getBytes());
        clientOutput.write("<br />\n".getBytes());
        clientOutput.write("<input type=\"submit\" value=\"Submit\"/>\r\n".getBytes());
        clientOutput.write("</form>\r\n".getBytes());
        clientOutput.write("</body>\r\n".getBytes());
        clientOutput.write("</html>\r\n".getBytes());
        clientOutput.flush();
        client.close();
    }

/*    private static void parseRequest(Socket client) throws IOException{
        String request = requestBuilder.toString();
        String[] requestsLines = request.split("\r\n");
        String[] requestLine = requestsLines[0].split(" ");
        String method = requestLine[0];
        String path = requestLine[1];
        String version = requestLine[2];
        String host = requestsLines[1].split(" ")[1];

        List<String> headers = new ArrayList<>();
        for(int i = 2; i < requestsLines.length; i++){
            String header = requestsLines[i];
            headers.add(header);
        }

        String accessLog = String.format("Client %s, method %s, path%s, version %s, host%s, headers %s",
                client.toString(), method, path, version, host, headers.toString());
        System.out.println(accessLog);
    }*/
}
