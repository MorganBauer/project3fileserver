/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Morgan
 */
public class ResponseDelete extends Response{
private static final String Type = "\"Rsp get\" ";
private final String filename;

  public ResponseDelete(int ReqNo, String filename)
   {
    super(ReqNo, true);
    this.filename = filename;
   }
public Request handle(Logger log) throws Exception
   {
  super.handle(log);
  log.write(Type+ReqNo+filename+" SUCCESS");
  return new Request(ReqNo);
}
  public String toString()
   {
  return "This is a get response";
}
}
