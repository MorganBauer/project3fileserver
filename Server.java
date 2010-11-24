import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.PriorityBlockingQueue;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.bind.JAXBException;

import team3.src.message.AbstractMessage;
import team3.src.message.Message;
import team3.src.message.ServerMessageFactory;
import team3.src.message.client.AbstractClientMessage;
import team3.src.message.client.FilePutMessage;
import team3.src.message.response.AbstractResponse;
import team3.src.message.response.FileGetResponse;
import team3.src.message.response.Response;
import team3.src.message.response.server.ServerReplicationResponse;
import team3.src.message.server.AbstractServerMessage;
import team3.src.message.server.ServerDirectoryMessage;
import team3.src.message.server.ServerPulseMessage;
import team3.src.message.server.ServerReplicationMessage;
import team3.src.protocol.IntraServerProtocol;
import team3.src.protocol.ServerClientProtocol;
import team3.src.server.AbstractServer;
import team3.src.util.SSLEncryptor;
import team3.src.util.Triple;



import static java.lang.System.out;

/**
 * Priority based server implementation
 * @author Joir-dan Gumbs
 *
 */
public class Server extends AbstractServer {
    /**
     * Responsible for queuing client connections by their priorities
     */
    private static PriorityBlockingQueue<PrioritySocket> priorityPool = new PriorityBlockingQueue<PrioritySocket>();
    /**
     * General Protocol object
     */
    private static ServerClientProtocol protocol = ServerClientProtocol.getProtocol("");
    
    private static IntraServerProtocol serverProtocol = IntraServerProtocol.getProtocol(getHostname(), port, 1024);
    
    @SuppressWarnings(value = { "unused" })
    private static UpdatePulse pulsor;
    
    /**
     * Gets message from the client
     * @param client
     * @return message sent from client
     * @throws IOException
     */
    private static String getMessage(final Socket client) throws IOException{
        final BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        return reader.readLine();
    }


    private static void sendWait(final Socket client, final AbstractResponse msg) throws IOException{
        out.println(msg);
        final PrintWriter writer = new PrintWriter(client.getOutputStream());
        writer.println(msg);
        writer.flush();
    }
    
    /**
     * Main thread of execution...
     * 
     * @param args one arg
     * @throws UnknownHostException 
     */
    public static void main(String[] args) throws UnknownHostException {
        setSSLProperties();
        getServerEnvironment(args);
        initData();
        updateFileTable();
        out.println(InetAddress.getLocalHost());
        //printFileTable();
        pulsor = UpdatePulse.getPulsor();
        SSLSocket client;
        PrioritySocket newSocket;
        PriorityServerThread socketConsumer = new PriorityServerThread();
        AbstractMessage message;
        try{
            out.println("Accepting connections!");
            out.format("Hostname: %s. Port: %d\n", getHostname(), port);
            socket = (SSLServerSocket) SSLServerSocketFactory.getDefault().createServerSocket(port);
            out.println("Server Socket Initialized");
            socketConsumer.start();
            while(isRunning()){
                try{
                    client = SSLEncryptor.encrypt((SSLSocket) socket.accept(), SSLEncryptor.AES, false);
                    String msg = getMessage(client);
                    message = AbstractMessage.unmarshal(msg);
                    newSocket = PrioritySocket.wrapSocket(client, message);
                    // If this is a terminating message, then prevent further client requests from hitting the server
                    if(message.getMsgType() == Message.Type.ATOMIC && message.read().equals("Terminate")){
                        finished();
                        closeConnection();
                    }
                    /* Check to see if we need to have this client wait on message
                     * If No clients are waiting, do not send wait message
                     * If clients are waiting and this is a read/write operation
                     *     If running protocol on this message would result in failure
                     *         Send failure message (DO NOT QUEUE)
                     *     Otherwise send wait message and place in queue
                     * If this is an OTHER type message, then we do not send wait message
                     * but put it in the queue
                     */
                    if(priorityPool.size() > 0 && (   newSocket.getMode() == Message.Type.READ 
                                                   || newSocket.getMode() == Message.Type.WRITE
                                                   || newSocket.getMode() == Message.Type.DELETE
                                                   || newSocket.getMode() == Message.Type.ATOMIC && newSocket.getPriority() > 0)){
                        AbstractResponse sendMsg = protocol.generateWaitMessage();
                        //logger.log(sendMsg);
                        try{
                            out.println(sendMsg.toString());
                            sendWait(newSocket.getClient(), sendMsg);
                            priorityPool.add(newSocket);
                        }catch(IOException e){ /*logger.log(errorHandler.handleSocketIOException(SocketError.INACCESSIBLE_STREAM));*/ }
                    }else priorityPool.add(newSocket);
                    //logger.log("Priority Pool SIZE: "+priorityPool.size());
                }catch(IOException e){ /*logger.log(errorHandler.handleSocketIOException(SocketError.UNKNOWN_HOST));*/ }  
                 catch(JAXBException e){out.println("JAXB!"); e.printStackTrace(); }
            }
            out.println("Server Channels Closed");
            socketConsumer.join();
        }catch(IOException e){ /*logger.log(errorHandler.handleSocketIOException(SocketError.CONNECTION_INIT_ERROR));*/ }
         catch (InterruptedException e) { /*logger.log("Unable to join on threads... Alert Admin about this."); */}
        out.println("Server Terminated...");
    }
    
    
    protected static final class UpdatePulse{
        private static UpdatePulse pulsor;
        Timer timer;
        TimerTask task = new TimerTask(){
            public void run(){
               for(ServerInfo server:servers){
                   try {
                    SSLSocket otherServer = (SSLSocket)SSLSocketFactory.getDefault().createSocket(server.getHostname(), server.getPort());
                    otherServer = SSLEncryptor.encrypt(otherServer, SSLEncryptor.AES, false);
                    PrintWriter outbound = new PrintWriter(otherServer.getOutputStream());
                    ServerPulseMessage msg = ServerPulseMessage.buildPulse(getHostname(), port, priorityPool.size());
                    outbound.println(msg.marshal());
                    outbound.flush();
                }catch (UnknownHostException e) {
                    out.format("Cant find %s:%d", server.getHostname(), server.getPort());
                } catch (IOException e) {
                    out.println("I have no idea what happened here");
                    e.printStackTrace();
                } catch (JAXBException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
               }
            }
        };
        private UpdatePulse(){
            timer = new Timer();
            timer.schedule(task, 10000, 5000);
        }
        public static final UpdatePulse getPulsor(){
            return (pulsor != null)?pulsor:(pulsor = new UpdatePulse());
        }
        
    }
    
    
    /**
     * Handles the listening for clients to the system, parses the input
     * for priority
     * @author Joir-dan 
     *
     */
    protected static class PriorityServerThread extends Thread{
        private volatile Integer readSemaphore = 0;
        private volatile Boolean writerIn = false;
        private final Object writerInLock = new Object();
        private volatile Boolean readerIn = false;
        private final Object readerInLock = new Object();
        private volatile Boolean isAlive = true;
        private final Object isAliveLock = new Object();
        private boolean alreadySentMsg = false;
        
        private static PriorityServerThread singleton;
        
        /**
         * Gets the priority handling thread for this server
         * @return priority server thread
         */
        public static final synchronized PriorityServerThread getThread(){
            return (singleton !=null)?singleton:(singleton = new PriorityServerThread());
        }
        
        /**
         * Constructor for PriorityServerThread
         */
        private PriorityServerThread() {
            super("PriorityServerThread");
        }
        /**
         * Check if we can continue running the server
         * @return true if yes, false if no
         */
        public boolean canRun(){
            synchronized(isAliveLock){
                return isAlive;
            }
        }
        
        public void run(){
            while(true){
                while(canRun() || priorityPool.size() > 0){
                    PrioritySocket first;
                    first = priorityPool.poll();
                    if(first != null){
                    	out.println("first is " + first);
                        //logger.log("Priority Queue Size: "+priorityPool.size());
                        /*
                         * In case you are wondering why I make this if statement call
                         * even though I have a switch-case statement involving the SocketMode enum, consider this:
                         * If we are working with a "Req Hello",  or a "Req Bye", we don't want to go through the process
                         * of running through embedded synchronization blocks... too much overhead and locking resources
                         * that the worker thread may need. Because of this, I decided to just throw an AssertionError on
                         * the variable first.mode if OTHER or null are found as first.mode's value. 
                         */
                        if(first.getMode() != Message.Type.ATOMIC && first.getMode() != Message.Type.PULSE){
                            synchronized(readerInLock){
                                synchronized(writerInLock){
                                    if(readerIn || (!readerIn && !writerIn)){
                                        //TODO: Deal with locks and priorities...
                                        switch(first.getMode()){
                                        case READ:
                                        
                                            if(getReadLock()){
                                                //README!! WORKER MUST RELEASE LOCK IN WORKER THREAD!!
                                                WorkerThread thread = new WorkerThread(first);
                                                thread.start();
                                                //README!! WORKER MUST RELEASE LOCK IN WORKER THREAD!!
                                            }else priorityPool.add(first);
                                            break; 
                                        case UPDATE:
                                        case DELETE:
                                        case WRITE:
                                            if(getWriteLock()){
                                                //README!! WORKER MUST RELEASE LOCK IN WORKER THREAD!!!
                                                WorkerThread thread = new WorkerThread(first);
                                                thread.start();
                                                //README!! WORKER MUST RELEASE LOCK IN WORKER THREAD!!!
                                            }else 
                                            	{priorityPool.add(first);}
                                            break;
                                        case REPLICATE:
                                            ServerReplicationMessage msg = (ServerReplicationMessage) first.getCurrentMsg();
                                            if(msg.getHost().equals(getHostname()) && msg.getPort() == port){
                                                if(getReadLock()){
                                                  //README!! WORKER MUST RELEASE LOCK IN WORKER THREAD!!!
                                                    WorkerThread thread = new WorkerThread(first);
                                                    thread.start();
                                                  //README!! WORKER MUST RELEASE LOCK IN WORKER THREAD!!!
                                                }else priorityPool.add(first);
                                                break;
                                            }else{
                                                if(getWriteLock()){
                                                  //README!! WORKER MUST RELEASE LOCK IN WORKER THREAD!!!
                                                    WorkerThread thread = new WorkerThread(first);
                                                    thread.start();  
                                                  //README!! WORKER MUST RELEASE LOCK IN WORKER THREAD!!!
                                                }else priorityPool.add(first);
                                                break;
                                            }
                                        default: throw new AssertionError(first.getMode());
                                        }
                                    }else{ priorityPool.add(first); }
                                }
                            }
                        }else{
                            // Its a socket of type SocketMode.OTHER... so, no worries about modifying data...
                            //TODO: Just create workerThread... and let it go...
                            WorkerThread thread = new WorkerThread(first);
                            thread.start();
                        }
                    }
                }
                //Empty the priorityQueue
                //Wait on all other threads to be finished with their work
                if(priorityPool.size() == 0) out.println("NOBODY IN HERE!");
                if(canExit()) break;
                out.println("FINISHED!!");
            }
        }
        
        /**
         * Checks whether our Server can exit
         * @return True if we can exit, false otherwise
         */
        public boolean canExit(){
            if(!alreadySentMsg){
                out.println("Waiting for other threads to finish execution...");
                alreadySentMsg = true;
            }
            synchronized(readerInLock){
                synchronized(writerInLock){
                    return (!readerIn && !writerIn);
                }
            }
        }
        /**
         * Method called to gain access to read lock for reading data from this server's directory
         * (read directory, or read files)
         * @return true if able to obtain read lock, false otherwise
         */
        public boolean getReadLock(){
            synchronized(readerInLock){
                synchronized(writerInLock){
                    if(writerIn)return false;
                    else{
                        readerIn = true;
                        readSemaphore++;
                        //logger.log("READ LOCK ++"+"READERS: "+readSemaphore);
                        return true;
                    }
                }
            }
        }
        
        /**
         * Method called to gain access to write lock for editing this server's directory 
         * (either adding or deleting data)
         * @return true if able to obtain write lock, false otherwise
         */
        public boolean getWriteLock(){
            synchronized(readerInLock){
                synchronized(writerInLock){
                    //logger.log("WRITE LOCK OBTAINED? "+ !(readerIn || readSemaphore > 0 || writerIn));
                    return (writerIn = !(readerIn || readSemaphore > 0 || writerIn));
                }
            }
        }
        /**
         * Method that is called upon completing a read to decrement reader semaphore.
         * If the worker calling this thread is the last reader, it is responsible for 
         * reader cleanup (releasing read lock for writer to come in).
         */
        
        public void releaseReadLock(){
            synchronized(readerInLock){
                //logger.log("READ LOCK --"+"READERS: "+(readSemaphore-1));
                if(--readSemaphore == 0) readerIn = false;
            }
        }
        /**
         * Method that is called upon completing a write to release lock and allow other
         * clients to have access to data
         */
        public void releaseWriteLock(){
            synchronized(writerInLock){
                writerIn = false;
                //logger.log("WRITE LOCK RELEASED");
            }
        }
       
        
        
        /**
         * Thread responsible for the communication with the client
         * @author Joir-dan Gumbs
         *
         */
        private class WorkerThread extends AbstractServerThread{
            private PrioritySocket socket;
            private AbstractMessage message;
            private SSLSocket serverSocket;
            private ServerClientProtocol protocol;
            private ServerMessageFactory serverFactory;
            
            private WorkerThread(final PrioritySocket pSocket){
                super(pSocket.getID());
                this.socket = pSocket;
                this.protocol = ServerClientProtocol.getProtocol(pSocket.getID());
            }
            
            /**
             * This is the main communication method of the WorkerThread.
             * It will grab a message from the thread's client's instream,
             * log the message, process message, log outgoing message,
             * send message to 
             */
            public void work(){
                AbstractResponse response;
                message = socket.getCurrentMsg();
                while(weCantStop()){
                    out.println("Not ended...");
                    out.println(message);
                    try{
                        message = (message == null)? AbstractMessage.unmarshal(readInstream()):message;
                        switch(message.getMsgType()){
                            case ATOMIC:
                            case DELETE:
                                response = protocol.generateResponse(message);
                                prepareToFinish();
                                out.println("Finishing!");
                                message = null;
                                break;
                            case READ:
                            case WRITE:
                                response = protocol.generateResponse(message);
                                switch(response.getType()){
                                    case DATA_IN:
                                        out.println("DATA IN!!");
                                        out.println(response);
                                        if(((FileGetResponse) response).isLast()){
                                            out.println("Is Last message");
                                            prepareToFinish();
                                        }
                                        break;
                                    case DATA_OUT:
                                        out.println("DATA OUT!!");
                                        out.println(response);
                                        out.println(((FilePutMessage) message).isLast());
                                        if(((FilePutMessage) message).isLast()) {
                                            out.println("Is Last Message");
                                            prepareToFinish();
                                        }
                                        break;
                                    default:
                                        prepareToFinish();
                                        response = protocol.createErrorMessage(message);
                                        break;
                                    
                                }
                                if(response.getType() == Response.Type.ERROR) prepareToFinish();
                                message = null;
                                break;
                            case PULSE:
                                out.println("GOT PULSE!");
                                out.println(message);
                                response = null;
                                prepareToFinish();
                                message = null;
                                break;
                            case UPDATE:
                                out.println("GOT UPDATE");
                                updateDirMsg((ServerDirectoryMessage) message);
                                response = null;
                                message = null;
                                break;
                            case REPLICATE:
                                ServerReplicationMessage msg = (ServerReplicationMessage) message;
                                if(msg.isCreator(getHostname(), port)){
                                    response = AbstractResponse.unmarshal(readInstream());
                                    switch(response.getType()){
                                        case DATA_IN:
                                            message = serverProtocol.receiveServerData(msg.getHost(), msg.getPort(), (ServerReplicationResponse)response);
                                            out.println(message.toString());
                                            writeOutstream(message.marshal());
                                            if(((ServerReplicationResponse) response).isLast()){
                                                prepareToFinish();
                                            }
                                            response = null;
                                    }
                                }else{
                                    response = serverProtocol.generateResponse(message);
                                    out.println(response);
                                    if(((ServerReplicationResponse) response).isLast()){
                                        out.println("Last Server Replication Message");
                                        prepareToFinish();
                                        message = null;
                                    }
                                }
                                break;
                            default:
                                response = null;
                                prepareToFinish();
                        }
                        
                        if(response!= null){
                            writeOutstream(response.marshal());
                        }
                        synchronized(isAliveLock){
                            isAlive = (isAlive)?!protocol.isTerminated():isAlive;
                        }
                    }
                    catch(IOException e){ out.println("IO");}
                    catch(JAXBException e){ out.println("JAXB!!!");}
                }
            }
            
            public void run(){
                out.println(socket.toString());
                try{ setStreams(socket.getClient());
                }catch(IOException e){ /*TODO: Use that AbstractErrorHandler... catch this error */}
                switch(socket.getMode()){
                case READ:
                    work();
                    releaseReadLock();
                    break;
                case WRITE:
                case DELETE:
                    AbstractClientMessage msg = (AbstractClientMessage)socket.getCurrentMsg();
                    protocol.backupDirectory();
                    work();
                    releaseWriteLock();
                    protocol.backupDirectory();
                    try { sendUpdateMsg(msg); } 
                    catch (UnknownHostException e) { e.printStackTrace(); } 
                    catch (IOException e) { e.printStackTrace(); } 
                    catch (JAXBException e) { e.printStackTrace(); }
                    break;
                case ATOMIC:
                case PULSE:
                    work();
                    break;
                case UPDATE:
                    protocol.backupDirectory();
                    work();
                    releaseWriteLock();
                case REPLICATE:
                    if(!((ServerReplicationMessage) socket.getCurrentMsg()).isCreator(getHostname(), port)){
                        protocol.backupDirectory();
                        work();
                        releaseWriteLock();
                        protocol.backupDirectory();
                    }else{
                        work();
                        releaseReadLock();
                    }
                    break;
                default: throw new AssertionError(socket.getMode()); // WE SHOULDNT GET HERE... FAIL FAST...
                }
            }
            
            /**
             * Responsible for sending update messages to 
             * other servers so they know about the changes...
             * @throws IOException 
             * @throws UnknownHostException 
             * @throws JAXBException 
             */
            private final void sendUpdateMsg(AbstractMessage msg) throws UnknownHostException, IOException, JAXBException{
                synchronized(servers){
                    for(ServerInfo server: servers){
                        AbstractServerMessage dirMsg = serverFactory.createDirectoryMessage(getHostname(), port, filenameAndDate, msg);
                        serverSocket = (SSLSocket)SSLSocketFactory.getDefault().createSocket(server.getHostname(), server.getPort());
                        PrintWriter outStream = new PrintWriter(serverSocket.getOutputStream());
                        outStream.println(dirMsg.marshal());
                        outStream.flush();
                    }
                }
            }
            private final void updateDirMsg(ServerDirectoryMessage msg) throws JAXBException, UnknownHostException, IOException{
                ServerInfo updatedServer = null;
                AbstractMessage embedMsg = null;
                Triple<ArrayList<String>, ArrayList<String>, ArrayList<String>> diffs;
                synchronized(servers){
                    for(ServerInfo server : servers)
                        if(server.getHostname().equals(msg.getHostname()))
                            if(server.getPort() == msg.getPort()){
                                server.updateDirectory(msg.getDirList());
                                updatedServer = server;
                                break;
                            }
                    if(updatedServer == null){
                        updatedServer = ServerInfo.makeServerInfo(msg.getHostname(), msg.getPort());
                        servers.add(updatedServer);
                    }
                    embedMsg = AbstractMessage.unmarshal(msg.read());
                    diffs = updatedServer.getDiffs(filenameAndDate, embedMsg);
                }
                for(String file : diffs.getSecond()){
                    SSLSocket server = (SSLSocket) SSLSocketFactory.getDefault().createSocket(msg.getHostname(), msg.getPort());
                    AbstractMessage request = serverFactory.createReplicationMessage(getHostname(), port, file);
                    PrioritySocket socket = PrioritySocket.wrapSocket(server, request);
                    priorityPool.add(socket);
                }
                for(String file : diffs.getThird()){
                    if(file.equals(embedMsg.read()))
                        new File(file).delete();
                        out.println(String.format("%s was deleted.", file));
                }
            }
        }
        
    }
}
