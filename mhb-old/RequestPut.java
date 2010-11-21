
import java.util.concurrent.locks.ReentrantReadWriteLock;

/*
 * @author Morgan
 */
public class RequestPut extends Request
 {

  private static final String Type = "\"Req put\" ";
  private final String filename;
  private final int chunksize;
  private final int prio;

  public RequestPut(int ReqNo, String filename, int chunksize)
   {
    super(ReqNo, true);
    this.filename = filename;
    this.chunksize = chunksize;
    prio = 0;
   }

  RequestPut(int ReqNo, String filename, int chunksize, int priority)
   {
    super(ReqNo, true);
    this.filename = filename;
    this.chunksize = chunksize;
    prio =priority;
   }

  public ResponsePut handle(ServerThread s) throws Exception
   {
    System.out.print("locking for file get read");
    if (!s.map.containsKey(filename))
     {
      s.map.put(filename, new ReentrantReadWriteLock(true));
     }
    s.map.get(filename).writeLock().lock();


    super.handle(s.log);
    return new ResponsePut(ReqNo, filename, chunksize);
   }

  public String toString()
   {
    return "This is a put request";
   }
 }
