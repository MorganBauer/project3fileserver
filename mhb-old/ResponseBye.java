/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Morgan
 */
public class ResponseBye extends Response {
  private static final String Type = "\"Rsp Hello\" ";
  public ResponseBye(int ReqNo)
   {
    super(ReqNo);
   }
  public RequestBye handle(Logger log) throws Exception
   {
          log.write(Type + ReqNo);
          System.exit(0);
          return new RequestBye(ReqNo);
   }
    public String toString()
   {
    return "I am a ResponseBye!\n";
  }


}
