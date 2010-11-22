package team3.src.message.response;

import static team3.src.message.response.server.ServerReplicationResponse.*;
import static team3.src.message.response.server.ServerVotingResponse.*;
import static team3.src.message.response.DeleteResponse.buildDeleteResponse;
import static team3.src.message.response.ErrorResponse.buildErrorMessage;

import team3.src.message.AbstractMessage;
import team3.src.message.ErrorMessage;
import team3.src.message.response.server.ServerReplicationResponse;
import team3.src.message.response.server.ServerVotingResponse;


/**
 * Factory for creating server-to-server responses
 * @author Joir-dan Gumbs
 */
public final class IntraServerResponseFactory {

    private static IntraServerResponseFactory singleton;
    
    /**
     * Creates a factory instance for the system to use.
     * @return factory instance
     */
    public static final IntraServerResponseFactory getFactory(){
        return (singleton != null)?singleton:(singleton = new IntraServerResponseFactory());
    }
    
    /**
     * Creates a vote response that casts vote for oldest server
     * @param vote the name of the best candidate
     * @return new vote response
     */
    public ServerVotingResponse createVoteMessage(String vote){
        return buildVote(vote);
    }
    
    /**
     * Creates a response message with replication data, and the filename
     * @param filename what to name this data stripe
     * @param base64Data the data stripe
     * @return new replication response
     */
    public final ServerReplicationResponse createReplicationResponse(String filename, String base64Data, boolean isLast){
        return buildReplicationMessage(filename, base64Data, isLast);
    }
    
    /**
     * Creates a response message for a server delete operation.
     * @param filename
     * @return
     */
    public final DeleteResponse createDeleteResponse(String filename){
        return buildDeleteResponse(filename);
    }
    
    /**
     * Builds an error message to be sent back to client or server
     * @param id who is sending this message
     * @param sentMsg the previous message that caused the error
     * @param ecode the error code associated with this.
     * @return an {@link ErrorMessage} to be sent
     */
    public ErrorResponse createErrorMessage(String id, AbstractMessage sentMsg, String ecode){
        return buildErrorMessage(id, sentMsg, ecode);
    }
    /**
     * builds an error message to be sent back to the client or server with details
     * @param id who sent the message
     * @param sentMsg the message that caused the failure
     * @param ecode the error code associated with the error
     * @param details extra information that would be useful
     * @return new error message
     */
    public ErrorResponse createErrorMessage(String id, AbstractMessage sentMsg, String ecode, String details){
        return buildErrorMessage(id, sentMsg, ecode, details);
    }

    
}
