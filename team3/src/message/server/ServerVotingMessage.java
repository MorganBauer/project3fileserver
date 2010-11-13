package team3.src.message.server;

import team3.src.message.Message;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlRootElement(name="VotingMessage")
@Message(Message.Type.VOTE)
public class ServerVotingMessage extends AbstractServerMessage implements Comparable<ServerVotingMessage>{
    
    @XmlElement(required=true)
    public XMLGregorianCalendar timestamp;
    
    private ServerVotingMessage(String host, int port, XMLGregorianCalendar timestamp) {
        super(host, port);
        this.timestamp = timestamp;
    }
   /**
    * Gets the timestamp of the server that created this message
    * @return
    */
    public final XMLGregorianCalendar getTimestamp(){
        return timestamp;
    }
    
    public String toString(){
        return String.format("Vote Request from %s: %s", getID(), timestamp.toString());
    }
    
    public int compareTo(ServerVotingMessage other) {
        return timestamp.compare(other.getTimestamp());
    }
 
    public String read() {
        return timestamp.toString();
    }
    
    public static final ServerVotingMessage buildVotingMessage(String host, int port, XMLGregorianCalendar timestamp){
        return new ServerVotingMessage(host, port, timestamp);
    }

}
