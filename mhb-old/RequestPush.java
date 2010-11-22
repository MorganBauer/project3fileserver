
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Morgan
 */
public class RequestPush extends Request
 {

  private static final String Type = "\"Req Push\" ";
  private final String filename;
  private final int chunksize;
  private final int startByte;
  private final byte[] data;

  public RequestPush(int ReqNo, String filename, int startByte, int chunksize, byte[] data, boolean fAR)
   {
    super(ReqNo, fAR);
    this.filename = filename;
    this.startByte = startByte;
    System.out.println("startbyte is " + startByte);

    this.chunksize = chunksize;
    this.data = data;
   }

  public ResponsePush handle(ServerThread s) throws Exception
   {
    super.handle(s.log);
    // open file
    // read data
    File file = new File("Server@" + filename);
    if (startByte <= 1024 * chunksize)
     {
      file.delete();
     }
    System.out.println("startbyte for new data placement is " + startByte);
    FileOutputStream fos = new FileOutputStream(file, true);
    fos.write(data);
    fos.close();
    System.out.format("data size is %d\n", data.length);

     if (!this.furtherActionRequired)
     {
      System.out.print("unlocking for file push write");

      s.map.get(filename).writeLock().unlock();
     }

    //System.out.println("Is fAR?" + furtherActionRequired);
    s.log.write(Type + ReqNo + (this.furtherActionRequired ? "NOTLAST " : "LAST "));
    s.log.newLine();
    return new ResponsePush(ReqNo, filename, startByte, chunksize, this.furtherActionRequired);
   }

  public String toString()
   {
    return "push request";
   }
 }
