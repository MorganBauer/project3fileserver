/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Morgan
 */
public class RequestHello extends Request {
  private static final String Type = "\"Req Hello\" ";
  RequestHello(int ReqNo)
   {
    super(ReqNo);
  }
  
public ResponseHello handle(ServerThread s) throws Exception
   {
super.handle(s.log);
s.log.write(Type + ReqNo + " SUCCESS");
     return new ResponseHello(ReqNo);
   }
  public String toString()
   {
    return "I am a RequestHello!\n";
  }
}
