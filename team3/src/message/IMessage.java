package team3.src.message;

import javax.xml.bind.JAXBException;


/**
 * 
 * @author Joir-dan Gumbs
 * This is the interface for all messages to follow
 * A Message MUST have an ID, and a Message must be READABLE
 * The {@link MsgType} Annotation will denote whether a Message
 * is asking for atomic privileges, read privileges, or write privileges.
 */
public interface IMessage {
	/**
	 * Allows a recipient to get the id of the sender
	 * @return Sender's ID
	 */
	public abstract String getID();
	/**
	 * Allows recipient to read message contents
	 * @return contents of Message
	 */
	public abstract String read();
	/**
	 * Converts Message Object into xml string
	 * @return xml string for sending out
	 * @throws JAXBException -  if unable to convert
	 */
	public abstract String marshal() throws JAXBException;
	
}
