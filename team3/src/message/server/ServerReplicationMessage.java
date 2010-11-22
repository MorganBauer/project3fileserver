package team3.src.message.server;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import team3.src.message.Message;

/**
 * Create a message that will request files on another server that isnt on this one
 * to maintain redundancy
 * @author Joir-dan Gumbs
 *
 */
@XmlRootElement(name="ReplicationMessage")
@Message(Message.Type.REPLICATE)
public class ServerReplicationMessage extends AbstractServerMessage {

    @XmlElement(required=true)
    private String filename;
    
    public String read() {
        return filename;
    }
    
    private ServerReplicationMessage(){}
    
    private ServerReplicationMessage(String host, int port, String filename){
        super(host, port);
        this.filename = filename;
        
    }
    /**
     * Create a replication request Message
     * @param host who is this from
     * @param port location
     * @param filename file we want data from
     * @return new replication message
     */
    public static ServerReplicationMessage buildReplicationMessage(String host, int port, String filename){
        return new ServerReplicationMessage(host, port, filename);
    }
}
