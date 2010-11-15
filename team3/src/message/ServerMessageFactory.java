package team3.src.message;

import static team3.src.message.response.ErrorResponse.buildErrorMessage;
import static team3.src.message.server.ServerVotingMessage.*;
import static team3.src.message.server.ServerPulseMessage.*;

import javax.xml.datatype.XMLGregorianCalendar;

import team3.src.message.response.ErrorResponse;
import team3.src.message.server.ServerPulseMessage;
import team3.src.message.server.ServerVotingMessage;

public final class ServerMessageFactory{
    public static ServerMessageFactory singleton;
    
    /**
     * Grab the factory for building server messages
     * @return server message factory
     */
    public static final ServerMessageFactory getFactory(){
        return (singleton != null)?singleton:(singleton = new ServerMessageFactory());
    }
    /**
     * Build a message that casts a vote for leadership within distributed system
     * @param host the hostname of this server
     * @param port the portnum of this server
     * @param timestamp When this server was "born." The older the machine, the higher chance it will be accepted as leader
     * @return a vote message
     */
    public ServerVotingMessage createVotingMessage(String host, int port, XMLGregorianCalendar timestamp){
        return buildVotingMessage(host, port, timestamp);
    }
    /**
     * @see ServerPulseMessage.buildPulse
     * @param host who is sending this
     * @param port the port of the sender
     * @param load how many clients are currently enqueue
     * @return pulse message
     */
    public ServerPulseMessage createPulseMessage(String host, int port, int load){
    	return buildPulse(host, port, load);
    }
    /**
     * @see ServerPulseMessage.buildPulseWithUpdate
     * @param host who is sending this
     * @param port the port of the sender
     * @param load how many clients are currently enqueue
     * @param currentDirectory what the current file directory looks like
     * @return pulse message
     */
    public ServerPulseMessage createPulseWithUpdate(String host, int port, int load, String[] currentDir){
    	return buildPulseWithUpdate(host, port, load, currentDir);
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
