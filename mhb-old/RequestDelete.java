
import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author Morgan
 */
public class RequestDelete extends Request
 {

  private static final String Type = "\"Req delete\" ";
  private final String filename;
  private final int priority;

  public RequestDelete(int ReqNo, String filename, int priority)
   {
    super(ReqNo);
    this.filename = filename;
    this.priority = priority;
   }

  public String toString()
   {
    return "RequestDelete!\n";
   }

  public ResponseDelete handle(ServerThread s) throws Exception
   {

    super.handle(s.log);

    //s.log.write(ReqNo + " " + filename);
    s.log.newLine();
    s.log.write(Type + ReqNo + " ");

    System.out.print("locking for file delete write");

    if (!s.map.containsKey(filename))
     {
      s.map.put(filename, new ReentrantReadWriteLock(true));
     }
    s.map.get(filename).writeLock().lock();
    try
     {
      File file = new File(filename);
      if (file.exists())
       {
        if (!file.isDirectory())
         {
          file.delete();
         }
       }
     }
    finally
     {
      s.map.get(filename).writeLock().unlock();
     }
    return new ResponseDelete(ReqNo, filename);
   }
 }
