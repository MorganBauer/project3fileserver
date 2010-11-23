
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 *
 * @author Morgan
 */
public class ResponsePush extends Response
 {

  private static final String Type = "\"Rsp Push\" ";
  private final String filename;
  private final int chunksize;
  private final int startByte;
//private final byte[] data;

  public ResponsePush(int ReqNo, String filename, int startByte, int chunksize/*,byte[] data*/, boolean fAR)
   {
    super(ReqNo, fAR);
    this.filename = filename;
    this.startByte = startByte;
    System.out.println("creating rsp push with startbyte as " + startByte);
    this.chunksize = chunksize;
    System.out.println("is there more data? "+ fAR);
//        this.data = data;
   }

  public RequestPush handle(Logger log) throws Exception
   {
    super.handle(log);


    File file = new File(filename);

    long fl = file.length();
    FileInputStream fis = new FileInputStream(file);

    int amountToGrab = chunksize * 1024;
//     int startByte = 0;
    boolean keepGoing = true;
    if ((fl - startByte) < amountToGrab)
     {
      amountToGrab = (int) fl - startByte;
      keepGoing = false;
     }

    System.out.println("File length " + fl + " starting position to get bytes from " + startByte + " number of bytes left to get " + (fl - startByte));
    System.out.println("Grabbing " + amountToGrab + " starting at " + startByte);
    System.out.println("Will I keep going?" + keepGoing);
    byte[] data = new byte[amountToGrab];
    int nextGrabStart = startByte + amountToGrab;
    fis.skip(startByte);
    fis.read(data, 0, amountToGrab);
    fis.close();

    log.write(Type + ReqNo + " SUCCESS " + chunksize);

//     log.write(Type+ReqNo + " SUCCESS " +(keepGoing?"NOTLAST ":"LAST ")+ startByte + " "+ amountToGrab+" " + data);
    log.newLine();
     //this.furtherActionRequired = keepGoing;
    return new RequestPush(ReqNo, filename, nextGrabStart, chunksize, data, keepGoing);
   }

  public String toString()
   {
    return "This is a push response";
   }
 }
