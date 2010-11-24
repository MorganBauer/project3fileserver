/**
 * 
 */
package team3.src.protocol;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import team3.src.message.AbstractMessage;
import team3.src.message.client.DeleteMessage;
import team3.src.message.client.FileGetMessage;
import team3.src.message.client.FilePutMessage;
import team3.src.message.client.SimpleMessage;
import team3.src.message.response.AbstractResponse;
import team3.src.message.response.ClientServerResponseFactory;
import team3.src.util.Data2MsgUtil;

import static java.lang.System.out;

/**
 * @author Joir-dan Gumbs
 *
 */
public class ServerClientProtocol extends AbstractProtocol {
	
	private ClientServerResponseFactory responseFactory;
	private boolean isTerminated;
	
	public boolean isTerminated(){
	    return isTerminated;
	}
	
	/**
	 * Prepares the thread working 
	 */
	private void terminate(){
	    isTerminated = true;
	}
	
	/**
	 * Returns a new protocol object
	 * @param id identifier of protocol user
	 * @return new protocol
	 */
	public static final ServerClientProtocol getProtocol(String id){
		return new ServerClientProtocol(id);
	}
	
	/**
	 * Takes an xmlString representation of a message and generates a response object
	 * THIS SHOULD BE USED FOR RESPONDING TO CLIENT MESSAGES
	 * @param msg the message that was received 
	 * @return response message
	 */
	public AbstractResponse generateResponse(AbstractMessage msg){
		switch(msg.getMsgType()){
		    case ATOMIC:
		        return handleSimpleMessage((SimpleMessage) msg);
		    case READ:
		        return handleFileRead((FileGetMessage) msg);
		    case WRITE:
		        return handleFileWrite((FilePutMessage) msg);
		    case DELETE:
		        return handleFileDelete((DeleteMessage) msg);
		    case ERROR:
		        return null;
		    case UNKNOWN:
		    default: 
		        return responseFactory.createErrorMessage(id, msg, UNKNOWN_MSG, "Unknown Message type");
		}
	}
	
	public AbstractResponse createErrorMessage(AbstractMessage msg){
	    return responseFactory.createErrorMessage(id, msg, CORRUPTED_MSG);
	}
	
	
	/**
	 * Handles SimpleMessage requests (Hello, Terminate, Directory List)
	 * @param msg the message that was sent
	 * @return an AbstractResponse 
	 */
	private AbstractResponse handleSimpleMessage(SimpleMessage msg){
	    String message = msg.read();
	    out.println(message);
	    String[] dirList;
	    if(message.equals("Dir")){
	        dirList = getDirectory(msg.getStart(), msg.getNumFiles());
	        out.println(dirList[0]);
	        return ((dirList = getDirectory(msg.getStart(), msg.getNumFiles())) != null)? 
                    responseFactory.createDirListResponse(dirList):
                    responseFactory.createErrorMessage(id, msg, INVALID_PARAMS, "Bad access to directory list");
	    }
	        
	    else if(message.equals("Terminate")){
	        terminate();
	        return responseFactory.createTerminateResponse();
	    }
	    else if(message.equals("Hello")) return responseFactory.createHelloResponse();
	    else return responseFactory.createErrorMessage(id, msg, UNKNOWN_MSG, "Corrupted message");
	}
	
	/**
	 * Create a wait response message to send to client
	 * @return new AbstractResponse
	 */
	public AbstractResponse generateWaitMessage(){
	    return responseFactory.createWaitResponse();
	}
	
	/**
	 * Handles FileGet Requests
	 * @param msg the message that was sent
	 * @return an AbstractResponse
	 */
	private AbstractResponse handleFileRead(FileGetMessage msg){
	    if(!msg.isInit())
            try{
                String data = dataToMsgUtil.data2Base64(msg.read(), msg.getChunkNo(), msg.getChunkSize(), false);
                out.println(((4/3)*data.length())/KILOBYTE);
                if(((data.length())/KILOBYTE < msg.getChunkSize())) dataToMsgUtil.cleanup();
                return responseFactory.createFileGetDataResponse(data, (4/3)*(data.length())/KILOBYTE, (((data.length())/KILOBYTE) < msg.getChunkSize()));    
            }catch(IOException e){
                return responseFactory.createErrorMessage(id, msg, FILE_NOT_FOUND, "File not in system");
            }
	    else{
	        out.println(msg.read());
	        return (exists(msg.read()))?
                    responseFactory.createFileGetInitResponse(msg.read()):
                    responseFactory.createErrorMessage(id, msg, "FILE_NOT_FOUND", "File not in system");  
	    }
	}
	
	/**
	 * Handles FilePut Messages
	 * @param msg the message sent
	 * @return AbstractResponse
	 */
	private AbstractResponse handleFileWrite(FilePutMessage msg){
        if(!msg.isInitMsg()){
            out.println("Data message");
            out.println(msg.isInitMsg());
            out.println(msg.isLast());
            String data;
            try {
                data = java.net.URLDecoder.decode(msg.read(),"UTF-8");
                out.println("IS LAST?"+msg.isLast());
                try{ dataToMsgUtil.base64toData(msg.getFilename(), data); }
                catch(IOException e){ return responseFactory.createErrorMessage(id, msg, IO_ERROR, "Unable to write data to file"); }
                return responseFactory.createFilePutResponse(msg.getFilename(), msg.isLast());
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            } 
        }else out.println("Is INIT MESSAGE");
        return responseFactory.createFilePutResponse(msg.getFilename(), false);
	}
	
	/**
	 * Handles Delete Messages
	 * @param msg what was sent
	 * @return Abstract Respone
	 */
	private AbstractResponse handleFileDelete(DeleteMessage msg){
	    if(exists(msg.read())) 
	        return (delete(msg.read()))?
	                responseFactory.createDeleteResponse(msg.read()):
	                responseFactory.createErrorMessage(id, msg, DEL_ERROR, "Unable to delete file");
	    return responseFactory.createErrorMessage(id, msg, FILE_NOT_FOUND, "File couldn't be found");    
	}
	

	/**
     * Get the directory subsequence for directory message
     * @param start start position in the list
     * @param nmax maximum entries to return
     * @return subsequence of directory list
     */
    // TODO: WILL NEED TO DO SOME MODIFYING LATER
    protected String[] getDirectory(int start, int nmax){
        ArrayList<String> list = new ArrayList<String>();
        if(getBackupDir().length < start || start < 0) return null;
        nmax = Math.min(nmax, getBackupDir().length);
        for(int i = start; i < nmax; i++) list.add(getBackupDir()[i]);
        String[] ret = new String[list.size()];
        for(int i = 0; i < list.size(); i++) ret[i] = list.get(i);
        return ret;
    }
	
	//TODO: THIS MAY NEED TO BE EDITED A BIT later
	/**
	 * Delete a file from this system
	 * @param filename file to be deleted
	 * @return true if deleted, false otherwise
	 */
	private boolean delete(String filename){
	    File file = new File(filename);
	    return file.delete();
	}
	
	private ServerClientProtocol(String id){
		super(id);
		this.dataToMsgUtil = Data2MsgUtil.getUtil();
		this.responseFactory = ClientServerResponseFactory.getFactory();
		backupDirectory();
	}
}
