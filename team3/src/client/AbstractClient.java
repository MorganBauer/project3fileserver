package team3.src.client;

import static java.lang.System.in;
import static java.lang.System.out;
import static java.lang.System.getProperty;
import static java.lang.System.setProperty;
import sun.jkernel.DownloadManager;
import team3.src.exception.IllegalCommandException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
//import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;

import team3.src.message.AbstractMessage;
import team3.src.message.response.AbstractResponse;
import team3.src.protocol.ClientProtocol;
import team3.src.util.ConfigData;
import team3.src.util.SSLEncryptor;

import javax.net.ssl.SSLServerSocket;
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
    
    private static ConcurrentSkipListSet<Integer> downServers = new ConcurrentSkipListSet<Integer>();
    
    protected static String encrypt = SSLEncryptor.AES;
    
    /**
     * Checks to see whether our client process is done (terminate has been called)
     * @return true if our process is not done, false otherwise
     */
    protected static boolean checkIsNotDone(){ return isNotDone; }
    
    /**
     * Sets our isNotDone variable to false, to signal the end of this process
     */
    protected static void thenClientIsDone(){ isNotDone = false; }
    
    protected static void changeEncryptionAlgorithm(final String algo)
    {

    	if (algo.equals("DES"))
    	{
    		encrypt = SSLEncryptor.DES;
        	out.println("changing encryption to " + SSLEncryptor.DES);
        	//out.println("'encrypt' is " + encrypt);
    	}else if (algo.equals("3DES"))
    	{
    		encrypt = SSLEncryptor.SanDES;    		
        	out.println("changing encryption to " + SSLEncryptor.SanDES);
        	//out.println("'encrypt' is " + encrypt);
    	}else if (algo.equals("AES"))
    	{
    		encrypt = SSLEncryptor.AES;
        	out.println("changing encryption to " + SSLEncryptor.AES);
        	//out.println("'encrypt' is " + encrypt);
    	}else if (algo.equals("RC4"))
    	{
    		encrypt = SSLEncryptor.RC4;
        	out.println("changing encryption to " + SSLEncryptor.RC4);
        	//out.println("'encrypt' is " + encrypt);
    	}
    	//serverSocket = SSLEncryptor.encrypt(serverSocket, encrypt, false);
    }
    
    public static final void setSSLProperties(){
        setProperty("javax.net.ssl.trustStore", "mySrvKeystore");
        setProperty("javax.net.ssl.trustStorePassword", "123456");
    }
    
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
        out.println("hello, terminate, bye");
        out.println("directory list <Start> <Max> <Priority>");
        out.println("my directory <Start> <Max>");
        out.println("file put <File> <Priority>: Put a file on the server");
        out.println("file get <File> <Priority>: Get a file from the server");
        out.println("delete <File> <Priority>");
        out.println("encryptify <algoString>");
    }
    
    protected static final String[] parseCommand(String[] args) throws IllegalCommandException{
        if(args.length == 0) return parseCommand();
        if(args[0].matches("file (put|get)|directory list|hello|terminate|delete|my directory|bye|encryptify")) return args;
        else throw new IllegalCommandException();
    }
    
    /**
     * Parses a command that the user inputs...
     * @return String array of the command arguments
     * @throws IllegalCommandException if it is an ill-formed command
     */
    protected static final String[] parseCommand() throws IllegalCommandException{
        String command, inString;
        final ArrayList<String> cmdList = new ArrayList<String>();
        try{
            printMenu();
            inString = new BufferedReader(new InputStreamReader(in)).readLine();    
            final Scanner commandScanner = new Scanner(inString);
            if((command=commandScanner.findInLine("file (put|get)|directory list|hello|terminate|my directory|delete|bye|encryptify")) != null)
                cmdList.add(command);   
            else{ throw new IllegalCommandException(); }
            try{ while((command=commandScanner.useDelimiter(" ").next()) != null) cmdList.add(command); }
            catch(NoSuchElementException e){ /* Dont worry about it... */ }
            final String[] strList = new String[cmdList.size()];
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
        final String format = "%1$-40s%2$-40s\n";
        if(args.length != 3) throw new IllegalCommandException();
        try{
            final int start = Integer.parseInt(args[1]);
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
        protected SSLSocket serverSocket;
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
        /** the host number in config data that we are currently connected to*/
        protected int hostNumber;
        
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
            
        	//try
        		String message = clientIn.readLine();
                return AbstractResponse.unmarshal(message);
  
        	//catch (SocketException se)
        	//{
        		// server died
        		// set server status to dead, reinit connection
        		//initConnection();
        	//}
        	
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
        private int getPort(int server) throws NumberFormatException{
            String host = "server-port"+server;//new String("server-port"+server);
            return Integer.parseInt(data.get(host));
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
        private String getHost(int server){
            String host = "server-hostname"+server;//new String("server-hostname"+server);
            return data.get(host);
        }
        
        protected void blacklistCurrentServer()
        {
        	downServers.add(hostNumber);
        }
        
        /**
         * Initializes connection to server socket for communication
         * @throws UnknownHostException If we are unable to find server
         * @throws IOException if we cannot gain access to serverSocket I/O streams
         */
        protected void initConnection() throws UnknownHostException, IOException{
            //config data map has 2 entries per server and 1 for chunk size and 1 for client ID
            //maximum gives the max number of servers in config file
            int maximum = ((data.getSize()/2)-1);
            //int host;
            do{
            	hostNumber = (int)((Math.random()*maximum)+1);
            	if (data.getSize() == downServers.size())
            	{
            		out.println("There are no known servers left to connect to.");
            		break;
            	}
            } while (downServers.contains(hostNumber));
            out.println("initing connection");
            serverSocket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(getHost(hostNumber), getPort(hostNumber)); 
            serverSocket = SSLEncryptor.encrypt(serverSocket, encrypt, false);
            clientIn = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            clientOut = new PrintWriter(serverSocket.getOutputStream());
        }
        
        /**
         * Closes current connections held by this clientThread
         * @throws IOException 
         * @throws UnknownHostException 
         */
        protected void closeConnection(){
            try{
                clientIn.close();
                clientOut.close();
                serverSocket.close();
            }catch(IOException e){
            	e.printStackTrace();/* We will ignore */ }
            
        }
    }
    
}
