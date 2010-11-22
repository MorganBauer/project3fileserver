// Morgan Bauer

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class server
 {

  public static void main(String args[]) throws Exception
   {
    //System.out.print(CR);
    ServerSocket srvr;
    srvr = null;
    Logger log = new Logger(java.net.InetAddress.getLocalHost().getHostName() + "-SERVER");
    System.out.print(java.net.InetAddress.getLocalHost().getHostName());
     //for (int i = 0; i < 1000; i++)
     {
      log.write("arst");
      log.newLine();
     }
    final PriorityBlockingQueue<ServerThread> PBQ =
            new PriorityBlockingQueue<ServerThread>();
    final ConcurrentNavigableMap<String,ReentrantReadWriteLock> map =
            new ConcurrentSkipListMap<String,ReentrantReadWriteLock>();
    //final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock(true);
    //final Lock read = rwl.readLock();
    //final Lock write = rwl.writeLock();

    srvr = new ServerSocket(0);//portNumber);
    srvr.setReuseAddress(true);
    int port = srvr.getLocalPort();
    ConfigWriter CW = new ConfigWriter("config.ini", java.net.InetAddress.getLocalHost().getHostName(), port);
    System.out.format("%s",CW);
    srvr.close();
    while (true)
     {
      try
       {
        System.out.print("Listening on port: " + port + "\n");
        // listen for a client
        // connect to client
        // create new thread with listener on random port
        // tell client to connect to other thread on the other port.
        srvr = new ServerSocket(port);
        Socket socket = srvr.accept();
        assert (socket != null) : "Socket was null after accepting";
        System.out.print("Server has connected!\n");

        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        // don't need input, only telling client to shove off to another place.
        // need to make a new thread, grab it's port, and send the move over packet to the clien
        log.newLine();
        //oos.writeObject(rsp);
        ServerThread st = new ServerThread(map, log, PBQ);
        Thread t = new Thread(st);
        t.start();

        oos.writeObject(new ConnectionStart(st.getLocalPort()));
        socket.close();
        srvr.close();

        // for the read write locks, have an array with the file names,
        // with extra entries (as counters) for readers and writers

       }
      catch (SocketException se)
       {
        System.out.println("SocketException");
        se.printStackTrace();
       }
      finally
       {
        if (srvr != null)
         {
          srvr.close();
         }
       }

      /*
      try {
      srvr = new ServerSocket(CR.serverPort);
      Socket socket = srvr.accept();
      assert (socket != null) : "Socket was null after accepting";
      System.out.print("server has connected!\n");

      ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
      ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
      Object o = ois.readObject();

      Request req = (Request)o;
      Response rsp = req.handle(log);
      log.newLine();
      //Thread.sleep(1000);
      oos.writeObject(rsp);

      socket.close();
      srvr.close();

      //log.flush();
      }
      catch (SocketException se)
      {
      srvr.close();
      }
      catch(Exception e) {
      e.printStackTrace();
      log.close();
      System.exit(-1);
      }*/
     }
   }
 }
