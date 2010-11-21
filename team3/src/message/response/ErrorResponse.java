package team3.src.message.response;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import team3.src.message.AbstractMessage;

@XmlRootElement(name="ErrorResponse")
@Response(Response.Type.ERROR)
public class ErrorResponse extends AbstractResponse{

	@XmlAttribute(required=true)
	private String id;
	@XmlAttribute(required=true)
	private String code;
	@XmlAttribute(required=true)
	private String originMsgType;
	@XmlElement()
	private String details;
	
	public String getID(){ return id; }
	
	public String read() {
		// TODO Auto-generated method stub
		return String.format("Error from previous %s message sent to %s. ERROR CODE %s. %s", 
										originMsgType, id, code, details);
	}
	public String toString(){
		return String.format("Error from previous %s message sent to %s. ERROR CODE %s. %s", 
				originMsgType, id, code, details);
	}
	/**
	 * Creates an error message with detailed information
	 * @param id the sender's identifier
	 * @param sentMsg message that caused error
	 * @param ecode error code associated with error
	 * @param details extra information
	 */
	private ErrorResponse(String id, AbstractMessage sentMsg, String ecode, String details){
		this.id = id;
		this.code = ecode;
		this.originMsgType = sentMsg.getClass().getAnnotation(Response.class).value().toString();
		this.details=details;
	}
	
	private ErrorResponse(){}
	/**
	 * Creates an error message without detailed information
	 * @param id sender's identifier
	 * @param sentMsg message that caused error
	 * @param ecode error code associated with the error
	 * @return new Error Message
	 */
	public static ErrorResponse buildErrorMessage(String id, AbstractMessage sentMsg, String ecode){
		return buildErrorMessage(id, sentMsg, ecode, "");
	}
	/**
	 * 
	 * Creates an error message with detailed information
	 * @param id the sender's identifier
	 * @param sentMsg message that caused error
	 * @param ecode error code associated with error
	 * @param details extra information
	 * @return new Error Message
	 */
	public static ErrorResponse buildErrorMessage(String id, AbstractMessage sentMsg, String ecode, String details){
		return new ErrorResponse(id, sentMsg, ecode, details);
	}
	
}
