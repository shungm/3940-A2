package aspects;
import aspects.IncorrectFileTypeException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.sql.Timestamp;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.JoinPoint;

@Aspect
public class ErrorLogAspect {
    
    @After(value = "execution(* *.*(..))")
    public void functionCallLog(JoinPoint joinPoint) {
        System.out.println("Class: " + joinPoint.getSignature().getDeclaringTypeName());
        System.out.println("Function " + joinPoint.getSignature());
        System.out.println("");
    }

	@Before("handler(*) && args(e)")
    public void logCaughtException(JoinPoint thisJoinPoint, Exception e) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println("Exception caught and handled");
        System.out.println(thisJoinPoint + " -> " + e);
       
        File myObj = new File("ErrorLog.txt");
        try (PrintWriter writer = new PrintWriter(new FileWriter("ErrorLog.txt", true))) {
            writer.println(timestamp + ": Error -> " + e);
        } catch (IOException err) {
            err.printStackTrace();
        }
    }
    
}
