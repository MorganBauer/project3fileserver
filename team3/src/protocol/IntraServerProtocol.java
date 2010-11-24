/**
 * 
 */
package team3.src.protocol;

import java.io.File;
import java.io.IOException;

import team3.src.message.AbstractMessage;
import team3.src.message.ServerMessageFactory;

import team3.src.message.response.AbstractResponse;
import team3.src.message.response.IntraServerResponseFactory;
import team3.src.message.response.server.ServerReplicationResponse;
import team3.src.message.server.ServerReplicationMessage;
import team3.src.util.Data2MsgUtil;

/**
 * @author Joir-dan Gumbs
 *
 */
public class IntraServerProtocol extends AbstractProtocol {
    private ServerMessageFactory serverFactory;
    private IntraServerResponseFactory responseFactory;
    private Data2MsgUtil dataToMsgUtil; 
    private String filename;
    private int chunkNo;
    private int chunkSize;
    
    public static final IntraServerProtocol getProtocol(String id, int port, int chunkSize){
        return new IntraServerProtocol(id, port, chunkSize);
    }
    
    public AbstractResponse generateResponse(AbstractMessage msg){
        switch(msg.getMsgType()){
            case REPLICATE:
                return handleDataTransfer((ServerReplicationMessage) msg);
            case PULSE:
                throw new AssertionError("PULSE MESSAGES DONT NEED RESPONSES!!!");
            default: return null; //Ignore stuff we aren't interested in...   
        }
    }
   
    public AbstractMessage receiveServerData(String host, int port, ServerReplicationResponse response) {
        if(response.hasData()){
            try{
                dataToMsgUtil.base64toData(response.getFilename(), response.read());
                if(response.isLast()) return null;
                return serverFactory.createReplicationMessage(host, port, response.getFilename());
            }catch(IOException e){ return responseFactory.createErrorMessage(host, response, IO_ERROR, "Unable to update file"); }
        }
        return serverFactory.createReplicationMessage(host, port, response.getFilename());
    }
    
    private AbstractResponse handleDataTransfer(ServerReplicationMessage msg){
        if(exists(msg.read())) 
            try { 
                String data = dataToMsgUtil.data2Base64(msg.read(), (msg.read().equals(filename))?(chunkNo=0):(++chunkNo), chunkSize, false);
                if((((4/3)*data.length())/KILOBYTE < chunkSize)) dataToMsgUtil.cleanup();
                return responseFactory.createReplicationResponse(msg.read(), data, (((4/3)*data.length())/KILOBYTE < chunkSize)); } 
            catch (IOException e) { return responseFactory.createErrorMessage(this.id, msg, IO_ERROR, "Unable to generate data stripe"); }
        else return responseFactory.createErrorMessage(this.id, msg, FILE_NOT_FOUND, "Unable to find file");
    }
    
    
    private IntraServerProtocol(String id, int port,  int chunkSize) {
        super(id+":"+port);
        this.chunkSize = chunkSize;
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



}
