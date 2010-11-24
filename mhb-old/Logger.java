
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Morgan
 */
public class Logger {
  private BufferedWriter log;
public Logger(String filename) throws Exception
   {
  this.log =
                new BufferedWriter(
                new FileWriter(
                new File(filename+".log")));
}
public void write(String msg) throws Exception
   {
  this.log.write(msg); this.log.flush();
}
public void newLine() throws IOException
   {
  this.log.newLine(); this.log.flush();
}
public void close() throws Exception
   {
  this.log.close();
}
}
