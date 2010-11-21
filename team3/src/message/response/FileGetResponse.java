package team3.src.message.response;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Joir-dan Gumbs
 *
 */
@XmlRootElement(name="FileGetResponse")
@Response(Response.Type.DATA_IN)
public final class FileGetResponse extends AbstractResponse {
    
    @XmlAttribute(required=true)
    private boolean isLast;
    @XmlAttribute()
    private boolean isData;
    @XmlElement(required=true)
    private String base64data;
    @XmlElement()
    private int chunkSize;
    
    public String toString(){
        return (isData)?
                String.format("Init Response to file Get"):
                String.format("Data Response to file Get. ISLAST:%b, SIZE:%d", isLast, chunkSize);
    }
    
    /**
     * Checks to see if this is the last message being sent
     * @return true if so, false otherwise
     */
    public boolean isLast(){
        return isLast;
    }
    
    /**
     * Gets the size of the data (uncompressed size)
     * @return the size of our data chunk
     */
    public final int getChunksize(){ return chunkSize; }
    
    /**
     * Checks to see if this response contains data
     * @return true if so, false otherwise
     */
    public final boolean isData(){
        return isData;
    }
    
    public String read() { return base64data; }
    
    private FileGetResponse(){
        this.isData = false;
        this.isLast = false;
        this.base64data = "READY";
    }
    private FileGetResponse( String data, int chunkSize, boolean isLast){
        this.base64data = data;
        this.chunkSize = chunkSize;
        this.isData = true;
        this.isLast = isLast;
    }
    /**
     * Build an initialization response to a File get command
     * @return new FileGetResponse
     */
    public static final FileGetResponse buildFileGetInitResponse(){
        return new FileGetResponse();
    }
    /**
     * Build a data response to a FileGet Command
     * @param data our base64 encoded data
     * @param chunkSize the size (decoded) of our data
     * @param isLast true if this is the last chunk, false otherwise
     * @return new FileGetResponse
     */
    public static final FileGetResponse buildFileGetDataResponse(String data, int chunkSize, boolean isLast){
        return new FileGetResponse(data, chunkSize, isLast);
    }
}
