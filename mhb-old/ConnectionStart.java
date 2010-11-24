/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.Serializable;
/**
 * @author Morgan
 */
public class ConnectionStart implements Serializable
 {

  private final int port;

  public ConnectionStart(int port)
   {
    this.port = port;
  }
  public int getPort()
   {
    return this.port;
  }
 }
