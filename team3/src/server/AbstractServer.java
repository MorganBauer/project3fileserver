package team3.src.server;

import static java.lang.System.out;
import static java.lang.System.getProperty;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.XMLGregorianCalendar;


import team3.src.message.AbstractMessage;
import team3.src.message.Message;
import team3.src.message.client.AbstractClientMessage;
import team3.src.util.ConfigData;
import team3.src.util.Triple;


/**
 * Abstraction of our server process
 * @author Joir-dan Gumbs
 *
 */
public abstract class AbstractServer {

    /**
     * Responsible for queuing client connections by their priorities
     */
    protected static PriorityBlockingQueue<PrioritySocket> priorityPool = new PriorityBlockingQueue<PrioritySocket>();
    
    /** Flag to check whether the server process is in a runnable state */
    private static volatile Boolean isRunning = true;
    /** Server Listening object. */
    protected static ServerSocket socket;
    /** Port of this server */
    protected static int port;
    /** The hostname of this server*/
    private static String hostname;
    
    private static ConfigData data;
    
    protected static void initData(){
        try { data = ConfigData.getConfigData(); } 
        catch (IOException e) { throw new AssertionError("WHERE IS UR CONFIGDATA FILE??"); }
        removeMe();
    }
    
    protected static void removeMe(){
        data.removeServer(hostname, port);
    }
    
    /**
     * Get the ConfigData object
     * @return
     */
    protected static ConfigData getData(){ return data; }
    
    protected static HashMap<String, Long> filenameAndDate = new HashMap<String,Long>();
    
    protected static final void updateFileTable(){
        File dir = new File(getProperty("user.dir"));
        for(File file:dir.listFiles() ) filenameAndDate.put(file.getName(), file.lastModified());
    }
    
    protected static final void printFileTable(){
        for(Map.Entry<String, Long> keyVal:filenameAndDate.entrySet()) out.println(String.format("%s: %d", keyVal.getKey(), keyVal.getValue()));
    }
    
    protected static void getServerEnvironment(){
        try{
            Runtime run = Runtime.getRuntime();
            Process proc = run.exec( "hostname" );
            BufferedInputStream in = new BufferedInputStream( proc.getInputStream() );
            byte [] b = new byte[256];
            in.read(b);
            hostname = new String(b).replace('\n', '\0');
            }catch(IOException e){ throw new AssertionError("U NOT WORKING IN UNIX??"); }
    }
    
    protected static String getHostname(){
       return hostname; 
    }
    
    
    /**
     * Checks to see if server is still in runnable state
     * @return True if it is, false otherwise
     */
    protected static boolean isRunning(){
        synchronized(isRunning){
            return isRunning;
        }
    }
    
    /** Sets our running flag to false, done to end server. */
    protected static void finished(){ 
        synchronized(isRunning){
            isRunning = false;
        }
    }
    
    /** Closes Server's connection listener. */
    protected static void closeConnection(){
        try{
            socket.close();
        }catch(IOException e){ 
            //logger.log(errorHandler.handleSocketIOException(SocketError.INACCESSIBLE_STREAM));
        }
    }
    
    protected static final class ServerInfo{
        private String hostname;
        private int load;
        private int port;
        private HashMap<String, Long> directory;
        
        public String getHostname(){ return hostname; }
        public int getLoad(){return load;}
        public int getPort(){ return port; }
        public HashMap<String, Long> getDir(){ return directory; }
        
        public Triple<ArrayList<String>,ArrayList<String>,ArrayList<String>> getDiffs(HashMap<String, Long> otherDir, AbstractClientMessage msg){
            ArrayList<String> iHaveRecent = new ArrayList<String>(),
                              theyHaveRecent = new ArrayList<String>(), 
                              theyHaveIt = new ArrayList<String>();
            for(Map.Entry<String, Long> keyVal: otherDir.entrySet()){
                if(directory.containsKey(keyVal.getKey())){
                    int comp = directory.get(keyVal.getKey()).compareTo(keyVal.getValue());
                    if(comp < 0)
                        theyHaveRecent.add(keyVal.getKey());
                    else if(comp > 0)
                        iHaveRecent.add(keyVal.getKey());
                    
                }else if(msg.getMsgType() == Message.Type.DELETE)
                    if(msg.read().equals(keyVal.getKey())) theyHaveIt.add(keyVal.getKey());
            }
            return new Triple<ArrayList<String>,ArrayList<String>,ArrayList<String>>(iHaveRecent, theyHaveRecent, theyHaveIt);
        }
        
    }
    
   
    
    /**
     * Abstraction of Server's thread of communication/execution.
     * @author Joir-dan Gumbs
     *
     */
    protected static abstract class AbstractServerThread extends Thread{
        /** Server's instream buffer. */
        private BufferedReader serverIn;
        /** Server's outstream buffer. */
        private PrintWriter serverOut;
        ///** Server protocol responsible for processing messages*/
        //protected AbstractServerProtocol protocol;
        ///** Responsible for logging this thread's activities */
        //protected Logger serverLogger;
        ///** Communication channel related to client. */
        protected Socket client;
        
        private boolean isDone;
        
        //protected AbstractErrorHandler errorHandler;
        
        /**
         * Check to see if this thread is done with it's current work
         * @return False if we are able to finish thread execution and true if not
         */
        protected boolean weCantStop(){ return !isDone; }
        /**
         * Prepares this thread for termination
         */
        protected void prepareToFinish(){ isDone = true; }
        /**
         * @param string
         */
        protected AbstractServerThread(String string) {
            super(string);
            isDone = false;
            //this.errorHandler = new ErrorHandler();
        }

        protected void setStreams(Socket client) throws IOException{
            serverOut = new PrintWriter(client.getOutputStream());
            serverIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
        }
        
        /** Responsible for reading server instream data.
         *  @return message from client.
         *  @throws IOException if unable to read from instream
         * @throws JAXBException 
         */
        protected AbstractMessage readInstream() throws IOException, JAXBException{
            String msg = serverIn.readLine();
            AbstractMessage ret = AbstractMessage.unmarshal(msg);
            return ret;
        }
        
        /**
         * Sends message to client
         * @param message that we want to send
         */
        protected void writeOutstream(String message){
            serverOut.println(message);
            serverOut.flush();
        }
        
        /**
         * Responsible for closing down client connection and streams.
         */
        protected void closeConnection(){
            try{
                client.close();
                serverIn.close();
                serverOut.close();  
            }catch(IOException e){ }    
        }
        
    }
    
    /**
     * Wrapper for our client, useful in establishing priority
     * @author Joir-dan Gumbs
     *
     */
    protected static final class PrioritySocket implements Comparable<PrioritySocket>{
        private Socket client;
        private int priority;
        private XMLGregorianCalendar timestamp;
        private AbstractMessage currentMsg;
        
        /**
         * Creates Priority Wrapper used in the priorityPool to aid in choosing clients to run on server
         * @param client Socket of client
         * @param currentMsg message sent by client
         * @return new PrioritySocket
         */
        public static PrioritySocket wrapSocket(Socket client,AbstractMessage currentMsg){
            return new PrioritySocket(client, currentMsg);
        }
        
        /**
         * Main Priority Socket Constructor
         * @param client Socket representing clients connection to server
         * @param msg Message that was sent by client
         */
        private PrioritySocket(Socket client, AbstractMessage msg){
            if(msg.getMsgType() != Message.Type.ATOMIC ||
               msg.getMsgType() != Message.Type.READ   ||
               msg.getMsgType() != Message.Type.WRITE  ||
               msg.getMsgType() != Message.Type.DELETE ||
               msg.getMsgType() != Message.Type.ERROR )
                this.priority = ((AbstractClientMessage) msg).getPriority();
            else this.priority = 100;
            this.timestamp = msg.getDateTime();
            this.client = client;
            this.currentMsg = msg;
        }
        
        /**
         * Gets the id of the sender
         * @return senders id
         */
        public String getID(){ return currentMsg.getID(); }
        
        /**
         * Gets the priority of this client request
         * @return integer representation of priority
         */
        public int getPriority(){ return priority; }
        /**
         * Grabs the timestamp
         * @return XMLGregorianCalendar timestamp
         */
        public XMLGregorianCalendar getTimestamp(){ return timestamp; }
        /**
         * Grabs the socket associated with the client
         * @return socket
         */
        public Socket getClient(){ return client; }
        
        /**
         * Gets the current message for this client
         * @return AbstractClientMessage
         */
        public AbstractMessage getCurrentMsg(){ return currentMsg; }
        /**
         * Grabs the type of message this is
         * @return the type of message
         */
        public Message.Type getMode(){ return currentMsg.getMsgType(); }
        
        public int compareTo(PrioritySocket socket) {
            /*
             * Priority is established in the following order
             * If priority is of type OTHER, then it has high priority
             * 
             */
            return 
                   (socket.getPriority() > priority)?  1:
                   (socket.getPriority() < priority)? -1: 
                   (socket.getTimestamp().compare(timestamp) > 0)?  -1: 1;
        }
        
        public String toString(){
            return String.format("Mode: %s\nPriority: %d\nTimestamp: %s.\n",getMode(), priority, (timestamp==null)?"Not Available":timestamp);
        } 
    }
    
}
