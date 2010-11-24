package team3.src.message.response.server;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import team3.src.message.response.AbstractResponse;
import team3.src.message.response.Response;

/**
 * Messages sent between machines for replication of data...
 * @author Joir-dan Gumbs
 *
 */
@XmlRootElement(name="DataReplicationMessage")
@Response(Response.Type.DATA_IN)
public final class ServerReplicationResponse extends AbstractResponse {

    @XmlElement(required=true)
    private String base64Data;
    @XmlElement(required=true)
    private String filename;
    @XmlAttribute(required=true)
    private boolean isLast;
    
    public boolean isLast(){
        return isLast;
    }
    
    /**
     * Gets the filename of this chunk
     * @return filename as string
     */
    public final String getFilename(){
        return filename;
    }
    
    /**
     * Read the data...
     */
    public String read() {
        return base64Data;
    }
    
    private ServerReplicationResponse(String filename, String base64Data, boolean isLast){
        this.filename = filename;
        this.base64Data = base64Data;
        this.isLast = isLast;
    }
    
    private ServerReplicationResponse(){}
    /**
     * Creates a new ServerDataReplicationMessage
     * @param host who is sending it
     * @param port from what port
     * @param filename the name of the chunk file
     * @param base64Data the data
     * @return new replication message
     */
    public static ServerReplicationResponse buildReplicationMessage(String filename, String base64Data, boolean isLast){
        return new ServerReplicationResponse(filename, base64Data, isLast);
    }
}
