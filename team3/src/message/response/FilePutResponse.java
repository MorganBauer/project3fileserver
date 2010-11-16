package team3.src.message.response;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Response to the FilePut message received by the server from the client.
 * @author Joir-dan Gumbs
 *
 */
@XmlRootElement(name="FilePutResponse")
@Response(Response.Type.DATA)
public class FilePutResponse extends AbstractResponse {

    //TODO: FINISH THIS UP... 
    @XmlAttribute(required=true)
    private String filename;
    
    /**
     * Returns the filename
     */
    public String read() {
        return filename;
    }

    public String toString(){
        return String.format("Response to File Put on %s", filename);
    }
    
    private FilePutResponse(){ }
    
    private FilePutResponse(String filename){
    	this.filename = filename;
    }
    /**
     * Creates a new FilePut response 
     * @param filename file that we are putting 
     * @return a response object
     */
    public static FilePutResponse buildFilePutResponse(String filename){
    	return new FilePutResponse(filename);
    }
}
