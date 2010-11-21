
import java.io.File;
import java.io.FileInputStream;

/**
 *
 * @author Morgan
 */
public class ResponsePut extends Response {
  private static final String Type = "\"Rsp put\" ";
  private final String filename;
  private final int chunksize;
  public ResponsePut(int ReqNo, String filename, int chunksize)
   {
    super(ReqNo, true);
    this.filename = filename;
    this.chunksize = chunksize;
   }
  public RequestPush handle(Logger log) throws Exception
   {


     File file = new File(filename);

  long fl = file.length();
  FileInputStream fis = new FileInputStream(file);

  int amountToGrab = chunksize*1024;
     int startByte = 0;
  boolean keepGoing = true;
  if ((fl-startByte) < amountToGrab)
   {
    amountToGrab = (int)fl-startByte;
    keepGoing = false;
      }
  System.out.println(fl+" "+startByte+" "+(fl-startByte));
  System.out.print("Grabbing " + amountToGrab + " starting at " + startByte);
System.out.println("Will I keep going?" + keepGoing);

  byte[] data = new byte[amountToGrab];

  int nextGrabStart = startByte + amountToGrab;

  fis.skip(startByte);
  fis.read(data, 0, amountToGrab);
     fis.close();

     log.write(Type + ReqNo + " " + filename + " READY " + filename.hashCode());
     log.newLine();
//     this.furtherActionRequired = keepGoing;
     return new RequestPush(ReqNo,filename,nextGrabStart,chunksize,data,keepGoing);
   }
    public String toString()
   {
    return "I am a ResponsePut!\n";
  }
}
