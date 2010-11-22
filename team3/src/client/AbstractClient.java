package team3.src.client;

import static java.lang.System.in;
import static java.lang.System.out;
import static java.lang.System.getProperty;
import team3.src.exception.IllegalCommandException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.xml.bind.JAXBException;

import team3.src.message.AbstractMessage;
import team3.src.message.response.AbstractResponse;
import team3.src.protocol.ClientProtocol;
import team3.src.util.ConfigData;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class AbstractClient {
    /** Config.ini wrapper object containing data for initialization purposes. */
    protected static ConfigData configData;
    /** Current request number. */
    protected static int requestNo = 0;
    /** True if we have run requests before, false otherwise. */
    private static boolean isNotFirstRequest = false;
    /** True if terminate has not been called, false otherwise. */
    private static boolean isNotDone = true;
    
    
    /**
     * Checks to see whether our client process is done (terminate has been called)
     * @return true if our process is not done, false otherwise
     */
    protected static boolean checkIsNotDone(){ return isNotDone; }
    
    /**
     * Sets our isNotDone variable to false, to signal the end of this process
     */
    protected static void thenClientIsDone(){ isNotDone = false; }
    
    /**
     * Initialized the configData object
     */
    protected static void initData(){
        try{
            configData = ConfigData.getConfigData();
            configData.set("clientID", generateID());
        }catch(IOException e){ throw new AssertionError("WHERE IS THE CONFIG.INI FILE??"); }
    }
    
    /**
     * Generates the next request number to be assigned to a clientThread
     * @return String representation of requestNumber
     */
    private static String nextReqNum(){ return String.format("%03d", ++requestNo % 1000); }
    
    /**
     * Builds a requestID from the clientID
     * @return new requestID for connection
     */
    protected static String generateRequestID(){ 
        return String.format("%s.%s",configData.get("clientID"), nextReqNum()); 
    }
    
    /**
     * Generates an ID no. that identifies the client process
     * @return new clientID for this process
     */
    protected static final String generateID(){ return  String.format("%03d", (int)(1000 * Math.random()));  }
    
    /**
     * Tests whether we are on our first request. On the first time this is called, it sets the variable to true
     * but returns false.
     * @return True if it is not our first request, and false otherwise.
     */
    protected static final boolean testIsNotFirstRequest(){
        if(isNotFirstRequest) return isNotFirstRequest;
        else return !(isNotFirstRequest = true); 
    }
    
    /**
     * Prints out the list of commands to be used
     */
    private static final void printMenu(){
        out.println("Command List");
        out.println("hello, terminate");
        out.println("directory list <Start> <Max> <Priority>");
        out.println("my directory <Start> <Max>");
        out.println("file put <File> <Priority>: Put a file on the server");
        out.println("file get <File> <Priority>: Get a file from the server");
        out.println("delete <File> <Priority>");
    }
    
    protected static final String[] parseCommand(String[] args) throws IllegalCommandException{
        if(args.length == 0) return parseCommand();
        if(args[0].matches("file (put|get)|directory list|hello|terminate|delete|my directory")) return args;
        else throw new IllegalCommandException();
    }
    
    /**
     * Parses a command that the user inputs...
     * @return String array of the command arguments
     * @throws IllegalCommandException if it is an ill-formed command
     */
    protected static final String[] parseCommand() throws IllegalCommandException{
        String command, inString;
        ArrayList<String> cmdList = new ArrayList<String>();
        try{
            printMenu();
            inString = new BufferedReader(new InputStreamReader(in)).readLine();    
            Scanner commandScanner = new Scanner(inString);
            if((command=commandScanner.findInLine("file (put|get)|directory list|hello|terminate|my directory|delete")) != null)
                cmdList.add(command);   
            else{ throw new IllegalCommandException(); }
            try{ while((command=commandScanner.useDelimiter(" ").next()) != null) cmdList.add(command); }
            catch(NoSuchElementException e){ /* Dont worry about it... */ }
            String[] strList = new String[cmdList.size()];
            cmdList.toArray(strList);
            return strList;
        }catch(IOException e){ 
            System.out.println("NuLL!!");
            return null; 
        }
    }
    
    /**
     * Get a subsequence of this client's current directory
     * @param args Arguments for this command
     */
    // TODO: WILL NEED TO DO SOME MODIFYING LATER
    protected static void getDirectory(String[] args) throws IllegalCommandException{
        String format = "%1$-40s%2$-40s\n";
        if(args.length != 3) throw new IllegalCommandException();
        try{
            int start = Integer.parseInt(args[1]);
            int nmax = Integer.parseInt(args[2]);
            File file = new File(getProperty("user.dir"));
            if(file.list().length < start || start < 0){
                out.println("Malformed command: invalid numerical constraints");
                throw new IllegalCommandException();
            }
            nmax = Math.min(nmax, file.list().length);
            for(int i = start; i < nmax; i=i+2){
                out.format(format, file.list()[i], ((i + 1 < file.list().length) && (i+1 < nmax))?file.list()[i+1]:" ");
            }
        }catch(NumberFormatException e){
            out.println("Malformed command: args 2 and 3 require numbers ");
            throw new IllegalCommandException();
        }
    }
    
    /**
     * Abstraction of a client thread. This class will do the communication between the AbstractClient
     * and the Abstract Server.
     * @author Joir-dan Gumbs
     *
     */
    protected static abstract class AbstractClientThread extends Thread{
        /** Boolean flag that indicates whether this clientThread is still executing. */
        private boolean isDone;
        /** A config.ini wrapper containing initialization data. */
        protected ConfigData data;
        /** The communication socket. */
        //protected Socket serverSocket;
        SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket sslsocket;// = (SSLSocket) sslsocketfactory.createSocket("localhost", Integer.parseInt(arstring[0]));

        /** The instream for the client from the server. */
        private BufferedReader clientIn;
        /** The outstream for the client to the server. */
        private PrintWriter clientOut;
        /** The arguments within a command*/
        protected String[] commandArgs;
        /** The ClientProtocol that is being used by this clientThread. */
        protected ClientProtocol protocol;
        /** Filename associated with this clientThread action. */
        protected String filename;
        /** Max chunk size this clientThread will send/receive. */
        protected int maxChunkSize;
        
        /**
         * Abstract Constructor...
         * @param requestID the identifier for our thread
         * @param data extracted from config.ini wrapper ConfigData
         * @param message the command we are passing to the server
         */
        protected AbstractClientThread(String requestID, ConfigData data, String[] message) throws UnknownHostException, IOException{
            this(requestID, data, message, null);
        }
        
        /**
         * Abstract Constructor...
         * @param requestID the identifier for our thread
         * @param data extracted from config.ini wrapper ConfigData
         * @param message the command we are passing to the server
         * @param filename the name of the file we are either getting or receiving
         */
        protected AbstractClientThread(String requestID, ConfigData data, String[] message, String filename)
        throws UnknownHostException, IOException{
            super(requestID);
            //TODO: this.errorHandler = new ErrorHandler();
            this.data = data;
            this.commandArgs = message;
            this.filename = filename;
            this.maxChunkSize = Integer.parseInt(data.get("chunk-size"));
            
            //TODO: logger.log("Logger initialized for ClientThread: "+requestID);
        }
        
        /** 
         * Grabs data from client's in-stream buffer
         * @return the message string
         * @throws IOException if unable to read from in-stream buffer
         */
        protected AbstractResponse grabFromServer() throws IOException, JAXBException{
            String message = clientIn.readLine();
            return AbstractResponse.unmarshal(message);
        }
        
        /**
         * Writes message to the server's instream buffer
         * @param message that we want to send to the server
         */
        protected void writeToServer(AbstractMessage message) throws JAXBException{
            clientOut.println(message.marshal());
            clientOut.flush();
        }
        
        /**
         * Check to see if this thread is done with it's current work
         * @return False if we are able to finish thread execution and true if not
         */
        protected boolean weCantStop(){ return !isDone; }
        
        /**
         * Sets our termination flag to true
         */
        protected void prepareToFinish(){ isDone = true; }
        
        /**
         * retrieves port number from ConfigData object
         * @return port number
         * @throws NumberFormatException This means our config.ini file is corrupted
         */
        private int getPort() throws NumberFormatException{
            return Integer.parseInt(data.get("server-port1"));
        }
        
        /**
         * Grabs the maximum chunk size allowed, which is stored in config.ini
         * @return chunk size
         * @throws NumberFormatException if our config.ini file is corrupted
         */
        protected int getChunkSize() throws NumberFormatException{
            return Integer.parseInt(data.get("chunk-size"));
        }
        
        /**
         * Retrieves hostname from ConfigData object
         * @return hostname the name of the host
         */
        private String getHost(){
            return data.get("server-hostname1");
        }
        
        /**
         * Initializes connection to server socket for communication
         * @throws UnknownHostException If we are unable to find server
         * @throws IOException if we cannot gain access to serverSocket I/O streams
         */
        protected void initConnection() throws UnknownHostException, IOException{
        	sslsocket = (SSLSocket) sslsocketfactory.createSocket(getHost(), getPort());
            //serverSocket = new Socket(getHost(), getPort()); 
        	//clientIn = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        	clientIn = new BufferedReader(new InputStreamReader(sslsocket.getInputStream()));
            clientOut = new PrintWriter(sslsocket.getOutputStream());
        }
        
        /**
         * Closes current connections held by this clientThread
         */
        protected void closeConnection(){
            try{
                clientIn.close();
                clientOut.close();
                sslsocket.close();
            }catch(IOException e){/* We will ignore */ }
            
        }
    }
    
}
