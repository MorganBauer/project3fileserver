/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Morgan
 */
public class RequestAGet extends Request
{
private static final String Type = "\"Req get\" ";
  public RequestAGet(int ReqNo,String filname, int numChunks)
   {
    super(ReqNo);   
   }
  public String toString()
   {
  return "This is an aget (aborted get) request";
}
}
