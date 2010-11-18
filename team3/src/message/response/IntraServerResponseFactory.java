package team3.src.message.response;

import static team3.src.message.response.server.ServerDataReplicationResponse.*;


/**
 * @author Joir-dan Gumbs
 */
public final class IntraServerResponseFactory {

    private static IntraServerResponseFactory singleton;
    
    public static final IntraServerResponseFactory getFactory(){
        return (singleton != null)?singleton:(singleton = new IntraServerResponseFactory());
    }
    
    
}
