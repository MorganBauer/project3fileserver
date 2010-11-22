
import java.io.FileInputStream;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author Morgan
 */
public class RequestGet extends Request
 {

  private static final String Type = "\"Req get\" ";
  private final String filename;
  private final int chunksize;
  private final int prio;

  public RequestGet(int ReqNo, String filename, int chunksize)
   {
    super(ReqNo);
    this.filename = filename;
    this.chunksize = chunksize;
    prio = 0;
   }

  public RequestGet(int ReqNo, String filename, int chunksize, int priority)
   {
    super(ReqNo);
    this.filename = filename;
    this.chunksize = chunksize;
    prio = priority;
   }

  public String toString()
   {
    return "I am a RequestGet!\n";
   }

  public ResponseGet handle(ServerThread s) throws Exception
   {

    super.handle(s.log);

    s.log.write(ReqNo + " " + filename + " READY " + filename.hashCode());
    s.log.newLine();
    s.log.write(Type + ReqNo + " ");

    System.out.print("locking for file get read");

    if (!s.map.containsKey(filename))
     {
      s.map.put(filename, new ReentrantReadWriteLock(true));
     }
    s.map.get(filename).readLock().lock();

    return new ResponseGet(ReqNo, filename, chunksize);
   }
 }
