package team3.src.message.server;

import java.io.ByteArrayInputStream;

import team3.src.message.AbstractMessage;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
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
    
    protected AbstractServerMessage(){}
    
    /**
     * Converts xml message into a message object 
     * @param xml - Message received from client (or other servers)
     * @return IMessage object
     * @throws JAXBException  if unable to convert
     */
    public static AbstractServerMessage unmarshal(String xml) throws JAXBException{
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        JAXBContext jc = JAXBContext.newInstance(AbstractServerMessage.class.getPackage().getName());
        Unmarshaller u = jc.createUnmarshaller();
        return (AbstractServerMessage) u.unmarshal(inputStream); 
    }
    
}
