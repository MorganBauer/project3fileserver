
import java.io.File;
import java.io.FileInputStream;

/**
 *
 * @author Morgan
 */
public class RequestPull extends Request
 {

  private static final String Type = "\"Rsp Pull\" ";
  private final String filename;
  private final int chunksize;
  private final int startByte;

  public RequestPull(int ReqNo, String filename, int startByte, int chunksize)
   {
    super(ReqNo);
    this.filename = filename;
    this.startByte = startByte;
    this.chunksize = chunksize;
   }

  public ResponsePull handle(ServerThread s) throws Exception
   {
    super.handle(s.log);
    File file = new File(filename);

    long fl = file.length();
    FileInputStream fis = new FileInputStream(file);

    int amountToGrab = chunksize * 1024;

    int nextGrabStart = startByte + chunksize * 1024;
    boolean keepGoing = true;
    if ((fl - startByte) < amountToGrab)
     {
      amountToGrab = (int) fl - startByte;
      keepGoing = false;
     }

    byte[] data = new byte[amountToGrab];

    System.out.println("Grabbing " + amountToGrab + " starting at " + startByte);
    fis.skip(startByte);
    fis.read(data, 0, amountToGrab);
    fis.close();

    if (!keepGoing)
     {
      System.out.print("unlocking for file get read");

      s.map.get(filename).readLock().unlock();
     }

    s.log.write(Type + ReqNo + " SUCCESS " + (keepGoing ? "NOTLAST " : "LAST ") + startByte + " " + amountToGrab + " " + data);
    s.log.newLine();
    return new ResponsePull(ReqNo, filename, nextGrabStart, chunksize, data, keepGoing);
   }

  public String toString()
   {
    return "This is a pull request";
   }
 }
