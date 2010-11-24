
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author Morgan
 */
public class RequestList extends Request
 {

  private static final String Type = "\"Req list\" ";
  private final int startFile;
  private final int Nmax;
  private final int prio;

  public RequestList(int ReqNo)//, int startFile, int Nmax)
   {
    super(ReqNo);
    this.startFile = 0;
    this.Nmax = 0;
    prio =0;
   }

  public RequestList(int ReqNo, int startFile, int Nmax)
   {
    super(ReqNo);
    this.startFile = startFile;
    this.Nmax = Nmax;
    prio =0;
   }

  public RequestList(int ReqNo, int startFile, int Nmax, int priority)
   {
    super(ReqNo);
    this.startFile = startFile;
    this.Nmax = Nmax;
    prio =priority;
   }

  public ResponseList handle(ServerThread s) throws Exception
   {
    super.handle(s.log);
    s.log.write(Type + ReqNo + " ");
    if (!s.map.containsKey("list"))
     {
      s.map.put("list", new ReentrantReadWriteLock(true));
     }
    s.map.get("list").readLock().lock();

    System.out.print("locked for dir list read");
    try
     {

      return new ResponseList(ReqNo, this.startFile, this.Nmax, s.log);
     }
    finally
     {
      s.map.get("list").readLock().unlock();
      System.out.print("unlocked for dir list read");
     }
   }

  public String toString()
   {
    return "RequestList!\n";



   }
 }
