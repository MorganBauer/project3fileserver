// Morgan Bauer
// Whoever Designed Java's Exception System needs to die
// I am so not proud of this at all
import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

class client
 {

  private int sequenceNumber = 0;
  private Logger log;

  public static void main(String args[]) throws Exception
   {
    ConfigReader CR = new ConfigReader("config.ini");
    System.out.print(CR);
    client C = new client();
    C.log = new Logger(java.net.InetAddress.getLocalHost().getHostName() + "-CLIENT");
    C.log.write("Arst");
    C.log.newLine();
    // initial connect to server

    // get packet with next port to connect too.

    Socket socket = new Socket(CR.serverHostname, CR.serverPort);
    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
    ConnectionStart CS = (ConnectionStart) ois.readObject();
    socket.close();
    int newPort = CS.getPort();
    System.out.format("Connected on port %d\n", newPort);
    socket = new Socket(CR.serverHostname, newPort);
    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
    ois = new ObjectInputStream(socket.getInputStream());

    // now open new connection on next port over.

    PriorityHolder priority = new PriorityHolder();
    while (true)
     {
      Request req = null;
      Response rsp = null;
      rsp = new Response(0);
      System.out.format("< hello | directory list | file put | file get | terminate >\n");
      do
       {
        if (!rsp.furtherActionRequired)
         {
          req = C.handleCommand(C.getUserInput(), C.log, CR, priority);
         }
        System.out.format("priority is %s\n", priority);
        System.out.println("request " + req);
        oos.writeObject(req);
        oos.flush();

        Object o = ois.readObject();
        rsp = (Response) o;
        System.out.println("response " + rsp);
        req = rsp.handle(C.log);
        System.out.format("Is furthur action required %b\n", rsp.furtherActionRequired);
       }
      while (rsp.furtherActionRequired);
     }
    /*    while(true)    {
    do{    System.out.println("request " + req);
    /* {
    oos.writeObject(req);
    oos.flush();
    o = ois.readObject();
    rsp = (Response) o;
    System.out.println("response " + rsp);
    }*/
   }

  private String getUserInput() throws Exception
   {
    String command = null;
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    try
     {
      command = br.readLine();
     }
    catch (IOException ioe)
     {
      System.out.println("IO error trying to read your command!");
      System.exit(1);
     }
    return command;
   }

  private Request handleCommand(String command, Logger log, ConfigReader CR, PriorityHolder prio) throws Exception
   {
    //      String command = commands[0];
    StringTokenizer st = null;
    boolean goodCommand = false;
    int priority = 0;

    do
     {
      if (prio.getPrio() != 0)
       {
        priority = prio.getPrio();
       }
      //System.out.println("command prio = " + priority);
      if (command.startsWith("priority"))
       {
        st = new StringTokenizer(command, " ", false);
        int tokens = st.countTokens();
        if (tokens == 2)
         {
          st.nextToken();
          priority = Integer.parseInt(st.nextToken());
          prio.setPrio(priority); // because I want to make your life dificult for
         }
       }
      else
       {
        if (command.equalsIgnoreCase("hello"))
         {
          return new RequestHello(sequenceNumber++);
         }
        else
         {
          if (command.startsWith("directory list"))
           {
            st = new StringTokenizer(command, " ", false);
            int tokens = st.countTokens();
            int startFile = 0;
            int Nmax = 0;
            if (tokens == 2)
             {
              System.out.format("This is for debug purposes only!!!");
              return new RequestList(sequenceNumber, 0, Integer.MAX_VALUE);
             }
            if (tokens == 4)
             {
              st.nextToken();
              st.nextToken();
              startFile = Integer.parseInt(st.nextToken());
              Nmax = Integer.parseInt(st.nextToken());
              if (0 == priority)
               {
                log.write("Req list " + (sequenceNumber + 1) + " " + startFile + " " + Nmax);
                return new RequestList(sequenceNumber++, startFile, Nmax);
               }
              else
               {
                log.write("Req list " + (sequenceNumber + 1) + " " + startFile + " " + Nmax + " " + priority);
                return new RequestList(sequenceNumber++, startFile, Nmax);
               }
             }
            else
             {
              System.out.print("incorrect number of arguments to list, must be 2");
             }
           }
          else
           {
            if (command.startsWith("file put"))
             {
              st = new StringTokenizer(command, " ", false);
              int tokens = st.countTokens();
              String filename = null;

              if (tokens == 3)
               {
                st.nextToken();
                st.nextToken();
                filename = st.nextToken();
                File test = new File(filename);
                if (test.exists())
                 {
                  if (test.isDirectory())
                   {
                    System.out.print(filename + " is a directory, try another name.\n");
                   }
                  else
                   {

                    if (0 == priority)
                     {
                      return new RequestPut(sequenceNumber, filename, CR.chunkSize);
                     }
                    else
                     {
                      return new RequestPut(sequenceNumber, filename, CR.chunkSize, priority);
                     }
                   }
                 }
               }
              else
               {
                System.out.print("File does not exist, try another name\n");
               }
             }
            else
             {
              if (command.startsWith("file get"))
               {
                st = new StringTokenizer(command, " ", false);
                int tokens = st.countTokens();
                String filename = null;
                if (tokens == 3)
                 {
                  st.nextToken();
                  st.nextToken();
                  filename = st.nextToken();

                  System.out.print("getting " + filename + " from " + CR.serverHostname);
                  if (0 == priority)
                   {

                    return new RequestGet(sequenceNumber, filename, CR.chunkSize);
                   }
                  else
                   {
                    return new RequestGet(sequenceNumber, filename, CR.chunkSize, priority);
                   }
                 }
               }
              else
               {
                if (command.startsWith("file aput"))
                 {
                  st = new StringTokenizer(command, " ", false);
                  int tokens = st.countTokens();
                  String filename = null;
                  int numChunks;
                  if (tokens == 4)
                   {
                    st.nextToken();
                    st.nextToken();
                    filename = st.nextToken();
                    numChunks = Integer.parseInt(st.nextToken());
                    log.write("Client Server \"Req put\" " + (sequenceNumber) + filename);
                    log.newLine();
                    log.write("Client Server \"Rsp put\" " + (sequenceNumber) + filename + " READY " + filename.hashCode());
                    log.newLine();
                    for (int x = 0; x < numChunks; ++x)
                     {
                      log.write("Client Server \"Req Push\" " + (sequenceNumber) + filename.hashCode() + " NOTLAST ");
                      log.newLine();
                      log.write("Client Server \"Rsp Push\" " + (sequenceNumber) + filename.hashCode() + " READY " + filename.hashCode());
                      log.newLine();
                     }
                    return new RequestAPut(sequenceNumber, filename, numChunks);
                   }
                 }
                else
                 {
                  if (command.startsWith("file aget"))
                   {
                    st = new StringTokenizer(command, " ", false);
                    int tokens = st.countTokens();
                    String filename = null;
                    int numChunks;
                    if (tokens == 4)
                     {
                      st.nextToken();
                      st.nextToken();
                      filename = st.nextToken();
                      numChunks = Integer.parseInt(st.nextToken());
                      return new RequestAGet(sequenceNumber, filename, numChunks);
                     }
                   }
                  else
                   {
                    if (command.trim().startsWith("terminate"))
                     {
                      return new RequestBye(sequenceNumber);
                     }
                    else
                     {
                      if (command.startsWith("delete"))
                       {
                        st = new StringTokenizer(command, " ", false);
                        int tokens = st.countTokens();
                        String filename = null;
                        if (tokens == 2)
                         {
                          st.nextToken();
                          filename = st.nextToken();
                          return new RequestDelete(sequenceNumber++, filename, priority);
                         }
                       }
                     }
                   }
                 }
               }
             }
           }

         }




       }
      command = new client().getUserInput();




     }
    while (!goodCommand);




    return new Request(-1);


   }
 }
