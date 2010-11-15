/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Morgan
 */
public class ResponseGet extends Response{
private static final String Type = "\"Rsp get\" ";
private final String filename;
private final int chunksize;

  public ResponseGet(int ReqNo, String filename, int chunksize)
   {
    super(ReqNo, true);
    this.filename = filename;
    this.chunksize = chunksize;
   }
public RequestPull handle(Logger log) throws Exception
   {
  super.handle(log);
  log.write(Type+ReqNo+filename+" READY" + filename.hashCode());
  return new RequestPull(ReqNo,filename,0,chunksize);
}
  public String toString()
   {
  return "This is a get response";
}
}
