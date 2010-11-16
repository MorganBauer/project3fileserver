package team3.src.message.response;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="DeleteResponse")
@Response(Response.Type.END)
public class DeleteResponse extends AbstractResponse {

    @XmlElement(required=true)
    private String filename;
    
    public String read() {
        return filename;
    }
    
    private DeleteResponse(){ }
    
    private DeleteResponse(String filename){
        this.filename = filename;
    }
    
    /**
     * Creates a response to a delete message (if successful)
     * @param filename the file that was deleted
     * @return new delete response
     */
    public static DeleteResponse buildDeleteResponse(String filename){
        return new DeleteResponse(filename);
    }
    
    public String toString(){
        return String.format("Deleted %s.", filename);
    }

}
