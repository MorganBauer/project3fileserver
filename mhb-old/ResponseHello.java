/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Morgan
 */
public class ResponseHello extends Response {
  private static final String Type = "\"Rsp Hello\" ";
  public ResponseHello(int ReqNo)
   {
    super(ReqNo);
   }
  public Request handle(Logger log) throws Exception
   {
          log.write(Type + ReqNo);
          return new Request(ReqNo);
   }
    public String toString()
   {
    return "I am a ResponseHello!\n";
  }


}
