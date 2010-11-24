
import java.io.Serializable;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Morgan
 */
public class Message implements Serializable {
public enum MSG_Type
{
  Req, Rsp
};
int ReqNo;
private String payload;
 Message(int ReqNo)
   {
this.ReqNo = ReqNo;
}
 public Message handle() throws Exception
   {
   return new Message(ReqNo);
 }
public String toString()
{
  return payload;
}

}

