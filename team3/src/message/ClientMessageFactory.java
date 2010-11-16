package team3.src.message;

import static team3.src.message.client.DeleteMessage.*;
import static team3.src.message.ErrorMessage.*;
import static team3.src.message.client.SimpleMessage.*;
import static team3.src.message.client.FileGetMessage.*;
import static team3.src.message.client.FilePutMessage.*;
import team3.src.message.client.DeleteMessage;
import team3.src.message.client.FileGetMessage;
import team3.src.message.client.FilePutMessage;
import team3.src.message.client.SimpleMessage;
import team3.src.message.response.AbstractResponse;


/**
 *  Factory for client messaging services
 * @author Joir-dan Gumbs
 *
 */
public final class ClientMessageFactory{
	private static ClientMessageFactory singleton;
	/**
	 * Grabs the factory for making client messages
	 * @return a factory
	 */
	public static final ClientMessageFactory getFactory(){
		return (singleton!= null)?singleton:(singleton= new ClientMessageFactory());
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
	 * Build a Hello Message
	 * @param clientID
	 * @return hello message
	 */
	public final SimpleMessage createHelloMessage(String clientID){
		return buildHelloMessage(clientID);
	}
	/**
	 * Build a Terminate Message
	 * @param clientID 
	 * @return terminate message
	 */
	public final SimpleMessage createTerminateMessage(String clientID){
		return buildTerminateMessage(clientID);
	}
	/**
	 * Create a Directory List Message
	 * @param clientID
	 * @param priority how important this is
	 * @param start where in the directory list we would like to start
	 * @param nMax how many entries in the list we want sent back
	 * @return new directory list message
	 */
	public final SimpleMessage createDirListMessage(String clientID, int priority, int start, int nMax){
		return buildDirListMessage(clientID, priority, start, nMax);
	}
	/**
	 * Creates a file put message (for initializing the transaction)
	 * @param clientID 
	 * @param filename name of the file we want to send
	 * @param priority how important this transaction is
	 * @return new File Put initialization message
	 */
	public final FilePutMessage createFilePutMessage(String clientID, String filename, int priority){
		return buildFilePutRequestMessage(clientID, filename, priority);
	}
	/**
	 * Create new File Put Data Message
	 * @param clientID 
	 * @param filename 
	 * @param priority how important this transaction is
	 * @param data 
	 * @param chunkNo the chunk number that is being 
	 * @param chunkSize
	 * @param isLast is this the last data
	 * @return new File Put Data Message
	 */
	public final FilePutMessage createFilePutMessage(String clientID, String filename, int priority, String data, int chunkNo, int chunkSize, boolean isLast){
		return buildFilePutDataMessage(clientID, filename, priority, data, chunkNo, chunkSize, isLast);
	}
	/**
	 * Create new File get Init Message
	 * @param clientID 
	 * @param filename
	 * @param priority how important this transaction is
	 * @return new file get init message
	 */
	public final FileGetMessage createFileGetMessage(String clientID, String filename, int priority){
		return buildInitMessage(clientID, filename, priority);
	}
	/**
	 * Create new file get data message
	 * @param clientID
	 * @param filename
	 * @param priority how important this transaction is
	 * @param chunkNo the chunk number we are requesting
	 * @param chunkSize
	 * @return new file get data request message
	 */
	public final FileGetMessage createFileGetMessage(String clientID, String filename, int priority, int chunkNo, int chunkSize){
		return buildPullMessage(clientID, filename, priority, chunkNo, chunkSize);
	}
	
	/**
	 * Builds an error message to be sent back to client or server
	 * @param id who is sending this message
	 * @param sentMsg the previous message that caused the error
	 * @param ecode the error code associated with this.
	 * @return an {@link ErrorMessage} to be sent
	 */
	public ErrorMessage createErrorMessage(String id, AbstractResponse sentMsg, String ecode){
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
	public ErrorMessage createErrorMessage(String id, AbstractResponse sentMsg, String ecode, String details){
		return buildErrorMessage(id, sentMsg, ecode, details);
	}
}
