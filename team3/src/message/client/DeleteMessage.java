package team3.src.message.client;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import team3.src.message.Message;

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

}
