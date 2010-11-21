/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Morgan
 */
public class Request extends Message
 {

  public final boolean furtherActionRequired;

  public Request(int ReqNo)
   {
    super(ReqNo);
    this.furtherActionRequired = false;
   }

  public Request(int ReqNo, boolean furth)
   {
    super(ReqNo);
    this.furtherActionRequired = furth;
   }

  public Response handle(Logger log) throws Exception
   {
    log.write("Server Client ");
    return new Response(ReqNo);
   }

  public Response handle(ServerThread srvr) throws Exception
   {
    srvr.log.write("Server Client ");
    return new Response(ReqNo);
   }

  public String toString()
   {
    return "This is a request";
   }

  public boolean getFurtherActionRequired()
   {
    return this.furtherActionRequired;
   }
 }
