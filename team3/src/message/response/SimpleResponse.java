package team3.src.message.response;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="SimpleResponse")
@Response(Response.Type.END)
public final class SimpleResponse extends AbstractResponse {

    @XmlAttribute(required=true)
    protected String message;
    @XmlElement()
    private String[] directory;
    
    public String read() {
        return message;
    }
    public String[] readDir(){
        if(directory == null) throw new AssertionError("WASNT A DIRECTORY MESSAGE");
        return directory;
    }
    
    public String toString(){
        return String.format("%s response message", message);
    }
    
    private SimpleResponse(){ }
    
    private SimpleResponse(String message){
        this.message = message;
    }
    private SimpleResponse(String[] directory){
        this.message = "Directory";
        this.directory = directory;
    }
    
    public static SimpleResponse buildResponseHello(){
        return new SimpleResponse("Hello");
    }
    public static SimpleResponse buildResponseTerminate(){
        return new SimpleResponse("Terminate");
    }
    public static SimpleResponse buildResponseDirList(String [] dir){
        return new SimpleResponse(dir);
    }

}
