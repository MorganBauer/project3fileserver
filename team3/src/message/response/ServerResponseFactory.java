package team3.src.message.response;

import static team3.src.message.response.FileGetResponse.*;
import static team3.src.message.response.FilePutResponse.*;
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
	 * 
	 * @return
	 */
	public static FileGetResponse createFileGetInitResponse(){
		return buildFileGetInitResponse();
	}
	/**
	 * 
	 * @param data
	 * @param chunkSize
	 * @param isLast
	 * @return
	 */
	public static FileGetResponse createFileGetDataResponse(String data, int chunkSize, boolean isLast){
		return buildFileGetDataResponse(data, chunkSize, isLast);
	}
	
	
}
