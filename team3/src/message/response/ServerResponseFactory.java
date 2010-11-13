package team3.src.message.response;

import static team3.src.message.response.FileGetResponse.*;
import static team3.src.message.response.FilePutResponse.*;
import static team3.src.message.response.WaitResponse.*;
import static team3.src.message.response.SimpleResponse.*;
import team3.src.message.AbstractMessageFactory;

/**
 * This is the factory that should be used by the server to 
 * send out response messages to clients AND servers
 * @author Joir-dan Gumbs
 *
 */
public class ServerResponseFactory extends AbstractMessageFactory {
	private static ServerResponseFactory singleton;
	
	/**
	 * Grabs the factory for making response messages
	 * @return a factory
	 */
	public static final ServerResponseFactory getFactory(){
		return (singleton!=null)?singleton:(singleton = new ServerResponseFactory());
	}
	/**
	 * Create a FileGet initial response message
	 * @return new response
	 */
	public static final FileGetResponse createFileGetInitResponse(){
		return buildFileGetInitResponse();
	}
	/**
	 * Creates a File Get data Response message
	 * @param data data to be sent
	 * @param chunkSize how large of a chunk the data is
	 * @param isLast　is this the last chunk?
	 * @return new response 
	 */
	public static final FileGetResponse createFileGetDataResponse(String data, int chunkSize, boolean isLast){
		return buildFileGetDataResponse(data, chunkSize, isLast);
	}
	/**
	 * Creates a response message to a File Put command
	 * @param filename the file the client wants to add/update
	 * @return new response object
	 */
	public static final FilePutResponse createFilePutResponse(String filename){
	    return buildFilePutResponse(filename);
	}
	/**
	 * Creates a wait response message for the client
	 * @return new response message
	 */
	public static final WaitResponse createWaitResponse(){
	    return buildWaitResponse();
	}
	/**
	 * Create a Hello Message Response
	 * @return new response message
	 */
	public static final SimpleResponse createHelloResponse(){
	    return buildResponseHello();
	}
	/**
	 * Create a Terminate Message Response
	 * @return new response message
	 */
	public static final SimpleResponse createTerminateResponse(){
	    return buildResponseTerminate();
	}
	/**
	 * Create a Directory List Response message
	 * @param directory the directory of the distributed System
	 * @return new Response message with directory
	 */
	public static final SimpleResponse createDirListResposne(String[] directory){
	    return buildResponseDirList(directory);
	}
	
}
