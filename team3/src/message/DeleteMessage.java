package team3.src.message;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import team3.src.message.Message;

/**
 * Message that is sent to request that a file be removed
 * @author Joir-dan Gumbs
 *
 */
@XmlRootElement(name="DeleteMessage")
@Message(Message.Type.DELETE)
public class DeleteMessage extends AbstractMessage {

    @XmlElement(required=true)
    private String filename;
    
    @XmlAttribute(required=true)
    private String id;
    
    @XmlAttribute(required=true)
    private int priority;
    /**
     * Gives the name of the file we would like to delete
     */
    public String read() {
        return filename;
    }
    
    /**
     * Get the priority of a client sending delete message
     * <br> WILL RETURN 0 for Server sending message
     * @return priority
     */
    public int getPriority(){
        return priority;
    }
    
    private DeleteMessage() { }
    
    private DeleteMessage(String id, int priority, String filename){
        this.id = id;
        this.priority = priority;
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

    public String getID() {
        return id;
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
