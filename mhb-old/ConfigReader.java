import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.*;
import java.util.regex.Pattern;
/**
 *
 * @author Morgan
 */
public class ConfigReader{
  public String serverHostname;
  public int serverPort;
  public int chunkSize = 1024;
  public String clientHostname;
  
  public ConfigReader(String filename) throws Exception
   {
     BufferedReader stream = new BufferedReader(new FileReader(new File(filename)));
    String line;
    while((line = stream.readLine()) != null )
    {
    Pattern p; Matcher m; String [] strs; 
      p = Pattern.compile("server-hostname:.*", Pattern.CASE_INSENSITIVE);
      m = p.matcher(line);
      if (m.matches())
       {
         strs = line.split(":\\s*");
         serverHostname = strs[1];
       }
      p = Pattern.compile("server-port:.*", Pattern.CASE_INSENSITIVE);
      m = p.matcher(line);
      if (m.matches())
       {
         strs = line.split(":\\s*");
         //System.out.println("sp is" + strs[0]);
         serverPort = Integer.parseInt(strs[1]);
       }
      p = Pattern.compile("chunk-size:.*", Pattern.CASE_INSENSITIVE);
      m = p.matcher(line);
      if (m.matches())
       {
         strs = line.split(":\\s*");
         chunkSize = Integer.parseInt(strs[1]);
       }
      p = Pattern.compile("client-hostname:.*", Pattern.CASE_INSENSITIVE);
      m = p.matcher(line);
      if (m.matches())
       {
         strs = line.split(":\\s*");
         clientHostname = strs[1];
       }
    }
   stream.close();
 //   String str = sb.toString();
    //p = Pattern.compile("h.*");
   // Boolean b = m.matches();
    //System.out.print( b +"\n" + str + "\n");

    //Scanner scanner = new Scanner(aLine);
    //scanner.useDelimiter("=");
    //Pattern.compile("arst", )
   }
  
  @Override
  public String toString()
   {
    return "server hostname is "+serverHostname + "\nserver port is " +serverPort+ "\nchunk size is " +chunkSize+ "\nclient hostname is " +clientHostname+ "\n";
  }
}
