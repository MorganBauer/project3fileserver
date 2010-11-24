/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Morgan
 */
public class Response extends Message{
public final boolean furtherActionRequired;
  public Response(int ReqNo)
   {
    super(ReqNo);
    this.furtherActionRequired = false;
   }
  public Response(int ReqNo, boolean furth)
   {
    super(ReqNo);
          this.furtherActionRequired = furth;
   }

public Request handle(Logger log) throws Exception
   {
  log.write("Client Server ");
  return new Request(ReqNo);
}
public String toString()
   {
  return "This is a response.";
}
public boolean furtherActionRequired()
   {
     return this.furtherActionRequired;
   }
}
