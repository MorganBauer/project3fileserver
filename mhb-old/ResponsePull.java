
import java.io.File;
import java.io.FileOutputStream;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Morgan
 */
public class ResponsePull extends Response{
private final String filename;
private final int chunksize;
private final int startByte;
private final byte[] data;

  public ResponsePull(int ReqNo, String filename,int startByte, int chunksize, byte[] data, boolean fAR)
   {
    super(ReqNo, fAR);
    this.filename = filename;
    this.startByte = startByte;
    this.chunksize = chunksize;
    this.data = data;
   }
public RequestPull handle(Logger log) throws Exception
   {
  log.write(ReqNo + " SUCCESS ");
     File file = new File(java.net.InetAddress.getLocalHost().getHostName()+"Client@" + filename);
     if(startByte <= 1024*chunksize)
       file.delete();
     FileOutputStream fos = new FileOutputStream(file, true);

     fos.write(data);
     fos.close();
     return new RequestPull(ReqNo, filename, startByte, chunksize);
}
public String toString()
   {
  return "This is a pull response";
}
}
