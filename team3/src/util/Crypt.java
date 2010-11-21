/**
 * 
 */
package team3.src.util;

/**
 * @author aswin
 *
 */
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
public class Crypt
{
   
   static Cipher ecipher; 
   static Cipher dcipher; 
   // Takes a 7-byte quantity(shared secret) and returns a valid 8-byte DES key. with parity added for every 7 bit sequence 
   public static byte[] addParity(byte[] in)
   {
      byte[] result = new byte[8];
      int resultIx = 1;
      int bitCount = 0;
      for (int i=0; i<56; i++) 
      {
         boolean bit = (in[6-i/8]&(1<<(i%8))) > 0; 
         { 
            result[7-resultIx/8] |= (1<<(resultIx%8))&0xFF;
             bitCount++; 
         } 
         //Set the parity bit after every 7 bits
         if((i+1) % 7 == 0)
         {
            if (bitCount % 2 == 0) 
            { 
            // Set low-order bit (parity bit) if bit count is even 
               result[7-resultIx/8] |= 1; 
            } resultIx++;
            bitCount = 0; 
         }
         resultIx++; 
      }
      return result;  
   }
   public static String encrypt(String data, byte[] secret)
   {
      try
      {
         //add parity to make 56bit secret to 64 bit key
         byte[] keyBytes = addParity(secret);
         // create key
         SecretKey key = new SecretKeySpec(keyBytes, "DES");
         ecipher = Cipher.getInstance("DES");
         ecipher.init(Cipher.ENCRYPT_MODE, key);
         //Get bytes from string to be encoded
         byte[] utf8 = data.getBytes("UTF8");
         // Encrypt 
         byte[] enc = ecipher.doFinal(utf8); 
         // Encode bytes to base64 to get a string 
         return new sun.misc.BASE64Encoder().encode(enc);
      }
      catch (javax.crypto.BadPaddingException e) 
      {
         System.out.println("BadPaddingException");
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      return null;
   }
   public static String decrypt(String data, byte[] secret)
   {
      try
      {
         //add parity to make 56bit secret to 64 bit key
         byte[] keyBytes = addParity(secret);
         // create key
         SecretKey key = new SecretKeySpec(keyBytes, "DES");
         dcipher = Cipher.getInstance("DES");
         dcipher.init(Cipher.DECRYPT_MODE, key);
         byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(data);
         // Decrypt
         byte[] utf8 = dcipher.doFinal(dec);
         // Decode using utf-8
         return new String(utf8, "UTF8");  
      }
      catch (javax.crypto.BadPaddingException e) 
      {
         System.out.println("BadPaddingException");
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      return null;
   }
   //test method
   public static void main(String args[])
   {
      byte[] sec = new byte[]{0x01, 0x72, 0x43, 0x3E, 0x1C, 0x7A, 0x55}; 
      byte[] sec2 = new byte[]{0x01, 0x72, 0x43, 0x3E, 0x1C, 0x75, 0x55}; 
      String test = "It has to be hello world";
      System.out.println( test +" is encrypted to");
      String encrypted = encrypt(test,sec);
      System.out.println(encrypted);
      System.out.println( "after decryption");
      System.out.println(decrypt(encrypted,sec));
      String test2 = "hi there";
      System.out.println( test2 +" is encrypted to");
      String encrypted2 = encrypt(test2,sec);
      System.out.println(encrypted2);
      System.out.println( "after decryption");
      System.out.println(decrypt(encrypted2,sec));
   }
}