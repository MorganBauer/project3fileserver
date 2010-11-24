package team3.src.protocol;

import team3.src.exception.IllegalCommandException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import team3.src.message.AbstractMessage;
import team3.src.message.ClientMessageFactory;
import team3.src.message.response.AbstractResponse;
import team3.src.message.response.FileGetResponse;
import team3.src.message.response.FilePutResponse;
import team3.src.message.response.SimpleResponse;
import team3.src.util.Data2MsgUtil;

import static java.lang.System.out;

/**
 * Handles the business logic behind the client process
 * @author Joir-dan Gumbs
 *
 */
public final class ClientProtocol extends AbstractProtocol{

    private ClientMessageFactory messageFactory;
    private int chunkNo;
    private int chunkSize;
    private int priority;
    
    /**
     * Returns a new protocol object
     * @param id identifier of protocol user
     * @param chunkSize max size data chunk to be sent
     * @return new protocol object
     */
    public static final ClientProtocol getProtocol(String id, int chunkSize){
        return new ClientProtocol(id, chunkSize);
    }
    
    private ClientProtocol(String clientID, int chunkSize) {
        super(clientID);
        dataToMsgUtil = Data2MsgUtil.getUtil();
        messageFactory = ClientMessageFactory.getFactory();
        backupDirectory();
        this.chunkSize = chunkSize;
    }
    
    /**
     * Handles input from the client
     * @param args string array of arguments
     * @param requestID identifier for client request
     * @return new AbsractMessage
     * @throws IllegalCommandException if malformed command
     */
    public AbstractMessage handleInput(String[] args, String requestID) throws IllegalCommandException{
        setRequestID(requestID);
        AbstractMessage msg;
        msg = (args[0].equals("hello"))?          messageFactory.createHelloMessage(id):
              (args[0].equals("directory list"))? buildDListMsg(args):
              (args[0].equals("file put"))?       buildFilePutMsg(args):
              (args[0].equals("file get"))?       buildFileGetMsg(args):
              (args[0].equals("delete"))?         buildDeleteMsg(args):
              (args[0].equals("terminate"))?      messageFactory.createTerminateMessage(id): null;
        if (msg == null) throw new IllegalCommandException();
        else return msg;
    }
    
    /**
     * Allows the client to set chunksize
     * @param chunkSize - max size of data chunks being sent.
     */
    public void setCurrentChunkSize(int chunkSize){
        this.chunkSize = chunkSize;
    }
    
    /**
     * Handles the next message to be sent
     * @param msg response from server
     * @return new AbstractMessage
     */
    public AbstractMessage handleNext(AbstractResponse msg){
        switch(msg.getType()){
            case DATA_IN:  return handleNextGet((FileGetResponse) msg);
            case DATA_OUT: return handleNextPut((FilePutResponse) msg);
            default: throw new AssertionError("ONLY SHOULD BE CALLED FOR DATA RESPONSES");
        }
    }
    
    /**
     * Creates next message for FileGet sequence
     * @param msg response from the server
     * @return new FileGet message
     */
    private AbstractMessage handleNextGet(FileGetResponse msg){
        return getNextFileGetMsg(msg);
    }
    
    /**
     * Creates next message for FilePut sequence
     * @param msg response from the server
     * @return new FilePut message
     */
    private AbstractMessage handleNextPut(FilePutResponse msg){
        return getNextFilePutMsg(msg);
    }
    
    /**
     * Handles simpleResponse actions from messages received from the server
     * @param msg msg that was recieved
     */
    public void handleSimpleResponse(AbstractResponse msg){
        if(!msg.read().equals("Directory")) out.println(msg.toString());
        else{
            String[] dir = ((SimpleResponse) msg).readDir();
            String format = "%1$-40s%2$-40s\n";
            for(int i = 0; i < dir.length; i=i+2){
                out.format(format, dir[i], ((i + 1 < dir.length) && (i+1 < dir.length))?dir[i+1]:" ");
            }
        }
    }
    
    /**
     * Sets the requestID to id
     * @param requestID client's identifier
     */
    private void setRequestID(String requestID){
        this.id = requestID;
    }
    
    /**
     * Builds a Directory List message
     * @param args string array of arguments
     * @return a new Abstract Message
     * @throws IllegalCommandException if malformed command
     */
    private AbstractMessage buildDListMsg(String[] args) throws IllegalCommandException {
        if(args.length < 3 || args.length > 4){
            out.println("Invalid number of arguments.");
            throw new IllegalCommandException();
        }else
            try{
                int startNo = Integer.parseInt(args[1]);
                int nMax = Integer.parseInt(args[2]);
                priority = (args.length == 3)? 100: Integer.parseInt(args[3]);
                return messageFactory.createDirListMessage(id, priority, startNo, nMax);
            }catch(NumberFormatException e){ 
                out.println("Invalid Params given for directory list command.");
                throw new IllegalCommandException(); 
            }
        
    }
    
    /**
     * Creates a FilePut message
     * @param args string array of arguments
     * @return new Abstract message
     * @throws IllegalCommandException if malformed command
     */
    private AbstractMessage buildFilePutMsg(String[] args) throws IllegalCommandException {
        if(args.length != 3) {
            out.println("Invalid number of arguments");
            throw new IllegalCommandException(); // Not of form "file put" <filename>
        }
        try{
            priority = Integer.parseInt(args[2]);
            if(!exists(args[1])){
                out.println("File not Found");
                throw new IllegalCommandException();
            }
            else return messageFactory.createFilePutMessage(id, args[1], priority);
        }catch(NumberFormatException e){ 
            out.println("Priority must be a number.");
            throw new IllegalCommandException();
        }
    }
    
    /**
     * Create a File Get Message
     * @param args string array of arguments
     * @return new AbstractMessage
     * @throws IllegalCommandException if malformed commad
     */
    private AbstractMessage buildFileGetMsg(String[] args) throws IllegalCommandException {
        if(args.length != 3){
            out.println("Invalid number of arguments");
            throw new IllegalCommandException(); // Not of form "file get" <filename>
        }
        else
            try{
                priority = Integer.parseInt(args[2]);
                return messageFactory.createFileGetMessage(id, args[1], priority); 
            }catch(NumberFormatException e){
                out.println("Priority must be a number");
                throw new IllegalCommandException();
            }
    }
    /**
     * Builds a delete message
     * @param args string array of arguments
     * @return new Abstract Message
     * @throws IllegalCommandException if malformed command
     */
    private AbstractMessage buildDeleteMsg(String[] args) throws IllegalCommandException{
        if(args.length != 3){
            out.println("Invalid number of arguments");
            throw new IllegalCommandException();
        }else
            try{
                priority = Integer.parseInt(args[2]);
                return messageFactory.createDeleteMessage(id, priority, args[1]);
            }catch(NumberFormatException e){
                out.println("Priority must be a number");
                throw new IllegalCommandException();
            }
    }
    
    /**
     * Creates next filePut message
     * @param msg response message to previous file get msg
     * @return new AbstractMessage
     */
   public AbstractMessage getNextFilePutMsg(FilePutResponse msg){ 
       if(outfile == null){
           chunkNo = 0;
           outfile = msg.read();
       }
       out.println(msg);
       if(msg.isLast()){
           out.println(msg);
           out.println("Msg is Last");
           return null;
       }
       try{
           String encoded = dataToMsgUtil.data2Base64(msg.read(), chunkNo++, chunkSize, true);
           if(encoded == null) return null;
           if((((4/3)*encoded.length()/KILOBYTE) < chunkSize)) dataToMsgUtil.cleanup();
           return messageFactory.createFilePutMessage(id, outfile, priority, java.net.URLEncoder.encode(encoded,"UTF-8"), chunkNo-1, chunkSize, ((4/3)*encoded.length()/KILOBYTE < chunkSize));
       }catch(FileNotFoundException e){
           out.println("Can't find file for transmitting");
           return messageFactory.createErrorMessage(id, msg, FILE_NOT_FOUND, "Unable to find file to transmit");
       }
       catch(IOException e){
           out.println("Error preparing file for transfer");
           return messageFactory.createErrorMessage(id, msg, IO_ERROR, "Error preparing file for transfer");
       }
    }
   
   /**
    * Creates next File Get message
    * @param msg response message to previous file get msg
    * @return new AbstractMessage
    */
   public AbstractMessage getNextFileGetMsg(FileGetResponse msg){
       if(!msg.isData()) return firstNextFileGet(msg);
       else return nextFileGet(msg);    
   }
   
   /**
    * Creates the first next File get message
    * @param msg response message to previous file get msg
    * @return new AbstractMessage
    */
   private AbstractMessage firstNextFileGet(FileGetResponse msg){
       infile = msg.read();
       outfile = String.format("%s-%s", id, infile);
       File file = new File(String.format("%s-%s", id, infile));
       try{
           file.createNewFile();
           return messageFactory.createFileGetMessage(id, infile, priority, chunkNo = 0, chunkSize);
       }catch(IOException e){
           out.println("Unable to create temp file");
           return messageFactory.createErrorMessage(id, msg, IO_ERROR, "Unable to create temp file");
       }
   }
   
   /**
    * Creates other next File get messages
    * @param msg response message to other previous file get msg
    * @return new AbstractMessage
    */
   private AbstractMessage nextFileGet(FileGetResponse msg){
       try{
           out.println(outfile);
           out.println(dataToMsgUtil.base64toData(outfile, java.net.URLDecoder.decode(msg.read(),"UTF-8")));
           if(msg.isLast()){
               out.println("LAST!!");
               if(exists(infile)) delete(infile);
               if(rename(outfile)) return null;
               else out.println("Unable to rename, using default filename.");
               return null;
           }
           return messageFactory.createFileGetMessage(id, infile, priority, ++chunkNo, chunkSize);
       }catch(IOException e){
           delete(outfile);
           out.println("Unable to transmit data");
           return messageFactory.createErrorMessage(id, msg, IO_ERROR, "Unable to transmit data");
       }  
   }
   
   /**
    * Deletes file
    * @param infile name of file to delete
    * @return true if deleted false otherwise
    */
   private boolean delete(String infile){
       File file = new File(infile);
       return file.delete();
   }
   
   /**
    * Renames file 
    * @param tmpName from name
    * @return true if renamed, false otherwise
    */
   private boolean rename(String tmpName){
       File file = new File(tmpName);
       return file.renameTo(new File(infile));
   }
    
    
}
