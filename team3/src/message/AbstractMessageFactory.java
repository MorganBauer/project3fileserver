package team3.src.message;
import static team3.src.message.ErrorMessage.*;

/**
 * Abstraction of our message factory
 * @author Joir-dan Gumbs
 *
 */
public abstract class AbstractMessageFactory {
	/**
	 * Builds an error message to be sent back to client or server
	 * @param id who is sending this message
	 * @param sentMsg the previous message that caused the error
	 * @param ecode the error code associated with this.
	 * @return an {@link ErrorMessage} to be sent
	 */
	public ErrorMessage createErrorMessage(String id, AbstractMessage sentMsg, String ecode){
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
	public ErrorMessage createErrorMessage(String id, AbstractMessage sentMsg, String ecode, String details){
		return buildErrorMessage(id, sentMsg, ecode, details);
	}
}
