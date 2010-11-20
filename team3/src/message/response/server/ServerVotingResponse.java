/**
 * 
 */
package team3.src.message.response.server;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import team3.src.message.response.AbstractResponse;
import team3.src.message.response.Response;

/**
 * Response that holds information on who the best suited 
 * master server should be.
 * @author Joir-dan Gumbs
 *
 */
@XmlRootElement(name="VotingResponse")
@Response(Response.Type.END)
public class ServerVotingResponse extends AbstractResponse {

    @XmlElement(required=true)
    private String vote;
    
    /**
     * Returns the id of the server this server elected as leader
     */
    public String read() {
        // TODO Auto-generated method stub
        return vote;
    }
    
    private ServerVotingResponse(){}
    
    private ServerVotingResponse(String vote){
        this.vote = vote;
    }
    
    /**
     * Creates a response message that casts a vote for the oldest server
     * @param vote the id of most suited server
     * @return new Vote message
     */
    public static final ServerVotingResponse buildVote(String vote){
        return new ServerVotingResponse(vote);
    }

}
