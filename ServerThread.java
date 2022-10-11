import java.net.*;
import java.io.*;
import java.time.Clock;

public class ServerThread extends Thread{
    private Socket socket = null;
    public ServerThread(Socket socket){
        this.socket = socket;
    }

/*    public void run(){
        try{
         Inputstream in = socket.getInputStream();

        }
    }*/
}
