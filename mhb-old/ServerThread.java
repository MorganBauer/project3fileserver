
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author Morgan
 */
public class ServerThread implements Runnable, Comparable
 {

  protected Logger log;
  private ServerSocket srvr;
  private int priority;
  private Date timestamp;
//  protected final Lock read;
//  protected final Lock write;
  protected final ConcurrentNavigableMap<String,ReentrantReadWriteLock> map;
  protected final PriorityBlockingQueue<ServerThread> pbq ;

  public ServerThread(ConcurrentNavigableMap<String,ReentrantReadWriteLock> map, Logger log, PriorityBlockingQueue<ServerThread> pbq) throws Exception
   {
    this.log = log;
    this.srvr = new ServerSocket(0);
    this.map = map;
    this.pbq = pbq;
   }

  public int getLocalPort()
   {
    return srvr.getLocalPort();
   }

  public void run()
   {
    try
     {
      Socket socket = srvr.accept();
      System.out.format("ServerThread listening on port %d\n", srvr.getLocalPort());
      assert (socket != null) : "Socket was null after accepting";
      System.out.print("ServerThread has connected to client!\n");

      ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
      ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
      while (true)
       {
        Object o = ois.readObject();

        Request req = (Request) o;
        System.out.format("Requesst is %s", req.toString());
        Response rsp = req.handle(this);
        log.newLine();
        Thread.sleep(50);
        oos.writeObject(rsp);
       }
     }
    catch (SocketException se)
       {
         System.out.format("Server has disconnected from client due to a \n" +
                 "SocketException, most likely reason is client was finished communicating.\n");
         //se.printStackTrace();
       }
    catch (Exception e)
     {
      System.out.format("Unhandled Exception caught.");
      e.printStackTrace();
     }
    finally
     {
     }
   }

  public int compareTo(Object o)
   {
    ServerThread st = (ServerThread)o;
    if (st.priority>this.priority)
     {
      return -1;
      }
    else if (st.priority == this.priority)
     {
      if (st.timestamp.before(this.timestamp))
       {
        return -1;
        }
      else
        return 1;
       }
    else if (st.priority<this.priority)
     {
      return 1;
       }
    return 0; //this should never happen
   }
 }
