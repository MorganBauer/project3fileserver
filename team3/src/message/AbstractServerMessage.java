package team3.src.message;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "AbstractServerMessage")
public abstract class AbstractServerMessage extends AbstractMessage {

    @XmlAttribute(required = true)
    private String host;
    @XmlAttribute(required = true)
    private int port;

    public String getID() { return host + ":" + port; }

    protected AbstractServerMessage(String host, int port) {
        super();
        this.host = host;
        this.port = port;
    }
}
