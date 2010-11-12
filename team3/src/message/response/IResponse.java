package team3.src.message.response;

import javax.xml.bind.JAXBException;

public interface IResponse {
    /**
     * Allows recipient to read response contents
     * @return contents of response
     */
    public abstract String read();
    /**
     * Converts response Object into xml string
     * @return xml string for sending out
     * @throws JAXBException -  if unable to convert
     */
    public abstract String marshal() throws JAXBException;
}
