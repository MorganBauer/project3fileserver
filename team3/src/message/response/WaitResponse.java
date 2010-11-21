package team3.src.message.response;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Very simple response object used for waiting.
 * Think of this as just an empty container;
 * @author Joir-dan Gumbs
 *
 */
@XmlRootElement(name="WaitResponse")
@Response(Response.Type.WAIT)
public final class WaitResponse extends AbstractResponse {
    
    /**
     * This shouldn't be called...
     */
    public String read() {
        throw new AssertionError("NOTHING TO BE READ IN WAIT RESPONSE!!");
    }
    
    public String toString(){
        return "Waiting...";
    }
    
    private WaitResponse(){}
    
    /**
     * Builds a waiting response
     * @return new WaitResponse object
     */
    public static WaitResponse buildWaitResponse(){
        return new WaitResponse();
    }

}
