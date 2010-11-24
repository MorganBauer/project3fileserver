package team3.src.message;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import team3.src.message.Message;
import team3.src.message.client.AbstractClientMessage;

/**
 * Message that is sent to request that a file be removed
 * @author Joir-dan Gumbs
 *
 */
@XmlRootElement(name="DeleteMessage")
@Message(Message.Type.DELETE)
public class DeleteMessage extends AbstractClientMessage {

    @XmlElement(required=true)
    private String filename;
    
    /**
     * Gives the name of the file we would like to delete
     */
    public String read() {
        return filename;
    }
    
    
    private DeleteMessage() { }
    
    private DeleteMessage(String id, int priority, String filename){
        super(id, priority);
        this.filename = filename;
    }
    /**
     * Factory Method for creating delete messages
     * <br>THIS IS THE CLIENT DELETE MESSAGE!
     * @param id client identifier
     * @param priority how important this task is
     * @param filename the name of the file
     * @return a new delete message
     */
    public static DeleteMessage buildDeleteMessage(String id, int priority, String filename){
        return new DeleteMessage(id, priority, filename);
    }
    
    public String toString(){
        return String.format("%s requesting to delete %s, with importance of %d.",getID(), filename, getPriority());
    }
    
    /**
     * Factory Method creating delete messages for intraserver communication
     * <br>THIS SHOULD ONLY BE USED FOR SERVER COMMUNICATION!!!
     * @param id who this is coming from
     * @param filename the name of the file to delete
     * @return new Server Delete Message
     */
    public static final DeleteMessage buildServerDeleteMessage(String id, String filename){
        return new DeleteMessage(id, 0, filename);
    }

}
