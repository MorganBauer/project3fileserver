/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Morgan
 */
public class RequestAPut extends Request
{
private static final String Type = "\"Req Aput\" ";
  public RequestAPut(int ReqNo,String filname, int numChunks)
   {
    super(ReqNo);
   }
  public String toString()
   {
  return "This is an aput (aborted put) request";
}

}
