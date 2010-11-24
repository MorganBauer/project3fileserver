
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 *
 * @author Morgan
 */
public class ConfigWriter
 {

  public String serverHostname;
  public int serverPort;
  //public int chunkSize;
  //public String clientHostname;

  public ConfigWriter(String filename, String serverHostname, int serverPort) throws Exception
   {
    this.serverHostname = serverHostname;
    this.serverPort = serverPort;

    ConfigReader CR = new ConfigReader(filename);
    int chunkSize = CR.chunkSize;
    CR = null;
    BufferedWriter stream = new BufferedWriter(new FileWriter(new File(filename)));

    stream.write("server-hostname: " + serverHostname);
    stream.newLine();
    stream.write("Server-port: " + serverPort);
    stream.newLine();
    stream.write("chunk-size: " + chunkSize);
    stream.newLine();
    stream.flush();
    stream.close();
   }

  @Override
  public String toString()
   {
    return "server hostname is " + serverHostname + "\nserver port is " + serverPort //+ "\nchunk size is " +chunkSize+ "\nclient hostname is " +clientHostname+ "\n"
            ;
   }
 }
