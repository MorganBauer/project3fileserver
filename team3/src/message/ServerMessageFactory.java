package team3.src.message;

import static team3.src.message.DeleteMessage.buildDeleteMessage;
import static team3.src.message.response.ErrorResponse.buildErrorMessage;
import static team3.src.message.server.ServerVotingMessage.*;
import static team3.src.message.server.ServerPulseMessage.*;
import static team3.src.message.server.ServerReplicationMessage.buildReplicationMessage;

import javax.xml.datatype.XMLGregorianCalendar;

import team3.src.message.response.ErrorResponse;
import team3.src.message.server.ServerPulseMessage;
import team3.src.message.server.ServerReplicationMessage;
import team3.src.message.server.ServerVotingMessage;

public final class ServerMessageFactory{
    public static ServerMessageFactory singleton;
    
    /**
     * Grab the factory for building server messages
     * @return server message factory
     */
    public static final synchronized ServerMessageFactory getFactory(){
        return (singleton != null)?singleton:(singleton = new ServerMessageFactory());
    }
    
    /**
     * Builds a message that asks for data that is not currently on this system
     * @param host who wants the data
     * @param port what location
     * @param filename name of the file we want
     * @return new replication message
     */
    public ServerReplicationMessage createReplicationMessage(String host, int port, String filename){
        return buildReplicationMessage(host, port, filename);
    }
    
    /**
     * Build a message that casts a vote for leadership within distributed system
     * @param host the hostname of this server
     * @param port the portnum of this server
     * @param timestamp When this server was "born." The older the machine, the higher chance it will be accepted as leader
     * @return a vote message
     */
    public ServerVotingMessage createCandidateMessage(String host, int port, XMLGregorianCalendar timestamp){
        return buildVotingMessage(host, port, timestamp);
    }
    
    /**
     * Create a delete message
     * @param clientID the client doing the deleting
     * @param priority how important this operation is
     * @param filename the file we want to delete
     * @return a new delete message
     */
    public final DeleteMessage createDeleteMessage(String clientID, int priority, String filename){
        return buildDeleteMessage(clientID, priority, filename);
    }
    
    /**
     * @param host who is sending this
     * @param port the port of the sender
     * @param load how many clients are currently enqueue
     * @return pulse message
     */
    public ServerPulseMessage createPulseMessage(String host, int port, int load){
    	return buildPulse(host, port, load);
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
