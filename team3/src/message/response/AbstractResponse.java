package team3.src.message.response;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public abstract class AbstractResponse implements IResponse {
	
	public String marshal() throws JAXBException {
	    JAXBContext jc = JAXBContext.newInstance(this.getClass());
        StringWriter writer = new StringWriter();
        Marshaller m = jc.createMarshaller();
        m.marshal(this, writer);
        return writer.toString();
	}
	
	/**
     * Converts xml response into a response object 
     * @param xml - Message received from client (or other servers)
     * @return new Response object
     * @throws JAXBException  if unable to convert
     */
	public static AbstractResponse unmarshal(String xml) throws JAXBException{
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        JAXBContext jc = JAXBContext.newInstance(AbstractResponse.class.getPackage().getName());
        Unmarshaller u = jc.createUnmarshaller();
        return (AbstractResponse) u.unmarshal(inputStream); 
    }

}
