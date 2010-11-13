package team3.src.message.server;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import team3.src.message.Message;

@XmlRootElement(name="PulseMessage")
@Message(Message.Type.ATOMIC)
public final class ServerPulseMessage extends AbstractServerMessage {

    @XmlAttribute(required=true)
    private int load;
    @XmlElement()
    private String[] directory;
    
    /**
     * Gets the current capacity of the file server (waiting clients)
     * @return how many clients this server is currently serving
     */
    public int getLoad(){ return load; }
    /**
     * Gets the current directory of this server
     * @return
     */
    public String[] getDirectory(){
        return directory;
    }
    
    private ServerPulseMessage(){ }
    
    private ServerPulseMessage(String host, int port, int load) {
        super(host, port);
        this.load = load;
    }
    
    private ServerPulseMessage(String host, int port, int load, String[] currentDirectory){
        this(host, port, load);
        this.directory = currentDirectory;
    }
    
    public String read() {
        return (directory == null)?"Pulse":"Update";
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
    /**
     * Create a pulse message with the updated directory
     * @param host who is sending this
     * @param port the port of the sender
     * @param load how many clients are currently enqueue
     * @param currentDirectory what the current file directory looks like
     * @return pulse message
     */
    public static final ServerPulseMessage buildPulseWithUpdate(String host, int port, int load, String[] currentDirectory){
        return new ServerPulseMessage(host, port, load, currentDirectory);
    }
}
