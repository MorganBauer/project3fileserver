/**
 * 
 */
package team3.src.protocol;

import java.io.File;
import java.io.IOException;

import team3.src.message.AbstractMessage;
import team3.src.message.DeleteMessage;

import team3.src.message.response.AbstractResponse;
import team3.src.message.response.IntraServerResponseFactory;
import team3.src.message.server.ServerReplicationMessage;
import team3.src.message.server.ServerVotingMessage;
import team3.src.util.Data2MsgUtil;

/**
 * @author Joir-dan Gumbs
 *
 */
public class IntraServerProtocol extends AbstractProtocol {

    private IntraServerResponseFactory responseFactory;
    private Data2MsgUtil dataToMsgUtil; 
    private String filename;
    private int chunkNo;
    private int chunkSize;
    
    public static final IntraServerProtocol getProtocol(String id, int chunkSize){
        return new IntraServerProtocol(id, chunkSize);
    }
    
    public AbstractResponse generateResponse(AbstractMessage msg){
        switch(msg.getMsgType()){
            case VOTE:
                return handleVote((ServerVotingMessage) msg);
            case REPLICATE:
                return handleDataTransfer((ServerReplicationMessage) msg);
            case PULSE:
                throw new AssertionError("PULSE MESSAGES DONT NEED RESPONSES!!!");
            case DELETE:
                return handleDelete((DeleteMessage) msg);
            default: return null; //Ignore stuff we aren't interested in...   
        }
    }
   
    
    private AbstractResponse handleVote(ServerVotingMessage msg){
        //TODO: FIGURE THIS OUT...
        return null;
    }
    
    private AbstractResponse handleDataTransfer(ServerReplicationMessage msg){
        if(exists(msg.read())) 
            try { 
                String data = dataToMsgUtil.data2Base64(msg.read(), (msg.read()!= filename)?(chunkNo=0):(++chunkNo), chunkSize, false);
                return responseFactory.createReplicationResponse(msg.read(), data, (((4/3)*data.length())/KILOBYTE < chunkSize)); } 
            catch (IOException e) { return responseFactory.createErrorMessage(this.id, msg, IO_ERROR, "Unable to generate data stripe"); }
        else return responseFactory.createErrorMessage(this.id, msg, FILE_NOT_FOUND, "Unable to find file");
    }
    
    /**
     * Handles Delete Messages
     * @param msg what was sent
     * @return Abstract Response
     */
    private AbstractResponse handleDelete(DeleteMessage msg){
        if(exists(msg.read())) 
            return (delete(msg.read()))?
                    responseFactory.createDeleteResponse(msg.read()):
                    responseFactory.createErrorMessage(id, msg, DEL_ERROR, "Unable to delete file");
        return responseFactory.createErrorMessage(id, msg, FILE_NOT_FOUND, "File couldn't be found");    
    }
    
    private IntraServerProtocol(String id, int chunkSize) {
        super(id);
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
