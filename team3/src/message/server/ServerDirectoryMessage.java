package team3.src.message.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import team3.src.message.Message;

/**
 * @author Hanabi
 *
 */
@XmlRootElement(name="ServerDirectoryMessage")
@Message(Message.Type.UPDATE)
public class ServerDirectoryMessage extends AbstractServerMessage {

    @XmlElement(required=true)
    private String[] files;
    @XmlElement(required=true)
    private long[] lastModified;
    @XmlElement()
    private String originalMsg;
    @XmlAttribute()
    private String host;
    @XmlAttribute()
    private int port;
    
    public String read() {
        return originalMsg;
    }
    
    public String getHostname() {
        return host;
    }

    public HashMap<String, Long> getDirList() {
        HashMap<String,Long> retVal = new HashMap<String, Long>();
        for(int i=0; i < files.length; i++) retVal.put(files[i], lastModified[i]);
        return retVal;
    }

    public int getPort() {
        return port;
    }

    private ServerDirectoryMessage(){ }
    
    private ServerDirectoryMessage(String hostname, int port, ConcurrentHashMap<String, Long> filenameAndDate, String msg){
        super(hostname, port);
        this.host = hostname;
        this.port = port;
        this.originalMsg = msg;
        files = new String[filenameAndDate.size()];
        lastModified = new long[filenameAndDate.size()];
        ArrayList<String> list = new ArrayList<String>();
        for(Entry<String, Long> pair : filenameAndDate.entrySet()) list.add(pair.getKey());
        for(int i = 0; i < list.size(); i++) {
            files[i] = list.get(i);
            lastModified[i] = filenameAndDate.get(files[i]);
        }
    }

    public static ServerDirectoryMessage buildDirectoryMessage(String host, int port, ConcurrentHashMap<String, Long> filenameAndDate, String msg){
        return new ServerDirectoryMessage(host, port, filenameAndDate, msg);
    }
}
