package team3.src.message.response;

import javax.xml.bind.annotation.XmlAttribute;

@Response(Response.Type.DATA)
public class FilePutResponse extends AbstractResponse {

    //TODO: FINISH THIS UP... 
    @XmlAttribute(required=true)
    private String filename;
    
    public String read() {
        // TODO Auto-generated method stub
        return null;
    }

}
