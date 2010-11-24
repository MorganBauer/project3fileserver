
import java.io.File;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Morgan
 */
public class ResponseList extends Response {
  private static final String Type = "\"Rsp Hello\" ";
  private final String[] filenames;
  private final int Nmax;
  public ResponseList(int ReqNo, int startFile, int Nmax, Logger log) throws Exception
   {
    super(ReqNo);
    this.Nmax = Nmax;
    File f = new File(".");
     String[] tempfilenames = f.list();
     if (startFile < tempfilenames.length)
      {
             if(Nmax > (tempfilenames.length - startFile))
      {
       Nmax = tempfilenames.length - startFile;
     }
      }
 else Nmax = 0;

     
     String[] resultFilenames = new String[Nmax];

     for (int i = startFile, j = 0; i < tempfilenames.length; i++, j++)
      {
        if (j >= Nmax)
          break;
        resultFilenames[j] = tempfilenames[i];
      }

     filenames = resultFilenames;
     
      for (String filename : filenames)
      {
        log.write(filename + " ");
      }
   }
  public Request handle(Logger log) throws Exception
   {
     log.write(Type + ReqNo);
     //System.out.print(ReqNo);
     int count = 0;
     for (String filename : filenames)
      {
        log.write(filename + " ");

        System.out.format("%3d: %s\n", count++, filename);
      }
     log.newLine();
     return new Request(ReqNo);
   }
  public String toString()
   {
    return "I am a ResponseList!\n";
  }
}
