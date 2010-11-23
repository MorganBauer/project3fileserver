/**
 * 
 */
package team3.src.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLSocket;

/**
 * @author Hanabi
 *
 */
public final class SSLEncryptor {
    
    public static final String AES = ".*_AES_.*";
    public static final String RC4 = ".*_RC4_.*";
    public static final String DES = ".*_DES_.*";
    public static final String SanDES = ".*_3DES_.*";
    
    /**
     * Deals with encryption methds for sockets
     * @author Morgan Bauer
     * @param socket the client socket
     * @param algoPattern what alogrithm are we using
     * @param verbose do we want extraneous info printed??
     * @return new SSLSocket
     */
    public static SSLSocket encrypt(SSLSocket socket, String algoPattern, boolean verbose){
        // Pick all AES algorithms of 256 bits key size
        Pattern pattern = Pattern.compile(algoPattern);
        Matcher matcher;
        boolean matchFound;

        String str[] = socket.getSupportedCipherSuites();
        int len = str.length;
        String set[] = new String[len];

        int j = 0, k = len - 1;
        for (int i = 0; i < len; i++) {
                matcher = pattern.matcher(str[i]);
                matchFound = matcher.find();
                if (matchFound) set[j++] = str[i];
                else set[k--] = str[i];
        }

        socket.setEnabledCipherSuites(set);

        if(verbose){
            str = socket.getEnabledCipherSuites();
            System.out.println("Available Suites after Set:");
            for (int i = 0; i < str.length; i++)
                    System.out.println(str[i]);
            System.out.println("Using cipher suite: "
                            + ((socket).getSession()).getCipherSuite());
        }
        return socket;
    }
}
