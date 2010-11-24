/**
 *
 * @author Morgan
 */
public class RequestBye extends Request {
  private static final String Type = "\"Req Hello\" ";
  RequestBye(int ReqNo)
   {
    super(ReqNo);
  }
  
public ResponseBye handle(ServerThread srvr) throws Exception
   {
super.handle(srvr.log);
srvr.log.write(Type + ReqNo + " SUCCESS");
     return new ResponseBye(ReqNo);
   }
  public String toString()
   {
    return "I am a RequestBye!\n";
  }
}
