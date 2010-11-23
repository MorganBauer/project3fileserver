package team3.src.message.server;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import team3.src.message.Message;

@XmlRootElement(name="PulseMessage")
@Message(Message.Type.PULSE)
public final class ServerPulseMessage extends AbstractServerMessage {

    @XmlElement(required=true)
    private int load;
    
    /**
     * Gets the current capacity of the file server (waiting clients)
     * @return how many clients this server is currently serving
     */
    public int getLoad(){ return load; }

    
    public String toString(){
         return String.format("Pulse Message from %s. Current Load is %d", getID(),load);
    }
    
    private ServerPulseMessage(){ }
    
    private ServerPulseMessage(String host, int port, int load) {
        super(host, port);
        this.load = load;
    }
    
    public String read() {
        return "Pulse";
    }
    /**
     * Create a simple pulse message for the master servers to use
     * @param host who is sending this
     * @param port the port of the sender
     * @param load how many clients are currently enqueue
     * @return pulse message
     */
    public static final ServerPulseMessage buildPulse(String host, int port, int load){
        return new ServerPulseMessage(host, port, load);
    }
}
