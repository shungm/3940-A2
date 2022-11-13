import java.net.*;
import java.io.*;
import java.time.Clock;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class UploadServerThread extends Thread {
   private Socket socket = null;
   public UploadServerThread(Socket socket) {
      super("DirServerThread");
      this.socket = socket;
   }
   public void run() {
      try {
         InputStream in = socket.getInputStream(); 
         HttpRequest req = new HttpRequest(in);
         OutputStream baos = new ByteArrayOutputStream(); 
         HttpResponse res = new HttpResponse(baos);
         UploadServlet httpServlet = new UploadServlet();
         Class<?> c = httpServlet.getClass();
         Method method1 = c.getDeclaredMethod("getServletBaos");

         method1.invoke(httpServlet);
         Method method2 = c.getDeclaredMethod("requestHandler", HttpRequest.class, HttpResponse.class);
         method2.invoke(httpServlet, req, res);

         ByteArrayOutputStream uploadServletResponse = ((UploadServlet) httpServlet).getServletBaos();
         baos.write(uploadServletResponse.toByteArray());
         OutputStream out = socket.getOutputStream(); 
         out.write(((ByteArrayOutputStream) baos).toByteArray());
         socket.close();
      } catch (Exception e) { e.printStackTrace(); }
   }
}