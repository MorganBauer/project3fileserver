/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Morgan
 */
public class PriorityHolder
 {

  private int prio;

  public PriorityHolder()
   {
    this.prio = 0;
   }

  public void setPrio(int prio)
   {
    this.prio = prio;
   }
  public int getPrio()
   {
    return this.prio;
  }
  public String toString()
   {
    return (Integer.valueOf(this.prio)).toString();
  }
 }
