package team3.src.message;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * @author Joir-dan Gumbs
 * This annotation will hold information for checking type 
 * of message.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MsgType {
	enum IS{ATOMIC, READ, WRITE, ERROR,UNKNOWN, VOTE, UPDATE};
	IS value();
}
