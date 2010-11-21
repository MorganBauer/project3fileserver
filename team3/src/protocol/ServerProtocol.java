/**
 * 
 */
package team3.src.protocol;

import javax.xml.bind.JAXBException;

import team3.src.message.AbstractMessage;
import team3.src.message.ServerMessageFactory;
import team3.src.message.response.AbstractResponse;
import team3.src.message.response.IResponse;
import team3.src.message.response.ServerResponseFactory;
import team3.src.util.Data2MsgUtil;

/**
 * @author Joir-dan Gumbs
 *
 */
public class ServerProtocol extends AbstractProtocol {
	
	private ServerResponseFactory responseFactory;
	
	public static final ServerProtocol getProtocol(String id){
		return new ServerProtocol(id);
	}
	
	public AbstractResponse generateResponse(String xmlMsg){
		try {
			AbstractMessage msg = AbstractMessage.unmarshal(xmlMsg);
			switch(msg.getMsgType()){
			case UNKNOWN:
			default: 
				return responseFactory.createErrorMessage(id, msg, "errorcode", "Unknown Message type");
			}
		} catch (JAXBException e) {
			//TODO: Handle Error here...
			responseFactory.createErrorMessage(id, null, "errorcode", "Malformed Message");
		}
	}
	
	private ServerProtocol(String id){
		super(id);
		this.messageFactory = ServerMessageFactory.getFactory();
		this.dataToMsgUtil = Data2MsgUtil.getUtil();
		backupDirectory();
	}
}
