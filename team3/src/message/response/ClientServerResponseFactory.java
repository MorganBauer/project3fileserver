package team3.src.message.response;

import static team3.src.message.response.ErrorResponse.*;
import static team3.src.message.response.DeleteResponse.*;
import static team3.src.message.response.FileGetResponse.*;
import static team3.src.message.response.FilePutResponse.*;
import static team3.src.message.response.WaitResponse.*;
import static team3.src.message.response.SimpleResponse.*;
import team3.src.message.AbstractMessage;
import team3.src.message.ErrorMessage;

/**
 * This is the factory that should be used by the server to 
 * send out response messages to clients AND servers
 * @author Joir-dan Gumbs
 *
 */
public class ClientServerResponseFactory {
	private static ClientServerResponseFactory singleton;
	
	/**
	 * Grabs the factory for making response messages
	 * @return a factory
	 */
	public static final synchronized ClientServerResponseFactory getFactory(){
		return (singleton!=null)?singleton:(singleton = new ClientServerResponseFactory());
	}
	/**
	 * Create a Delete response message
	 * @param filename the filename that was deleted
	 * @return new delete response
	 */
	public final DeleteResponse createDeleteResponse(String filename){
	    return buildDeleteResponse(filename);
	}
	
	/**
	 * Create a FileGet initial response message
	 * @return new response
	 */
	public final FileGetResponse createFileGetInitResponse(String filename){
		return buildFileGetInitResponse(filename);
	}
	/**
	 * Creates a File Get data Response message
	 * @param data data to be sent
	 * @param chunkSize how large of a chunk the data is
	 * @param isLast is this the last chunk?
	 * @return new response 
	 */
	public final FileGetResponse createFileGetDataResponse(String data, int chunkSize, boolean isLast){
		return buildFileGetDataResponse(data, chunkSize, isLast);
	}
	/**
	 * Creates a response message to a File Put command
	 * @param filename the file the client wants to add/update
	 * @return new response object
	 */
	public final FilePutResponse createFilePutResponse(String filename, boolean isLast){
	    return buildFilePutResponse(filename, isLast);
	}
	/**
	 * Creates a wait response message for the client
	 * @return new response message
	 */
	public final WaitResponse createWaitResponse(){
	    return buildWaitResponse();
	}
	/**
	 * Create a Hello Message Response
	 * @return new response message
	 */
	public final SimpleResponse createHelloResponse(){
	    return buildResponseHello();
	}
	/**
	 * Create a Terminate Message Response
	 * @return new response message
	 */
	public final SimpleResponse createTerminateResponse(){
	    return buildResponseTerminate();
	}
	/**
	 * Create a Directory List Response message
	 * @param directory the directory of the distributed System
	 * @return new Response message with directory
	 */
	public final SimpleResponse createDirListResponse(String[] directory){
	    return buildResponseDirList(directory);
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
