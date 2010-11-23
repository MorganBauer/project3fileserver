package team3.test;

import javax.xml.bind.JAXBException;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import team3.src.message.response.AbstractResponse;
import team3.src.message.response.ClientServerResponseFactory;


public class ServerResponseFactoryTest extends TestCase {

    public ClientServerResponseFactory smf;
    AbstractResponse s;
    String XML;
    
    @Before
    public void setUp(){
        smf = ClientServerResponseFactory.getFactory();
    }
    
    @Test
    public void testHelloResponse() throws JAXBException{
        XML = (s = smf.createHelloResponse()).marshal();
        assertEquals(s.toString(), AbstractResponse.unmarshal(XML).toString());
    }
    
    @Test
    public void testTerminateResponse() throws JAXBException{
        XML = (s = smf.createTerminateResponse()).marshal();
        assertEquals(s.toString(), AbstractResponse.unmarshal(XML).toString());
    }
    
    @Test
    public void testDirListResponse() throws JAXBException{
        String[] dir = {"心一つ.mp3", "GatorChomp.png"};
        XML = (s = smf.createDirListResponse(dir)).marshal();
        assertEquals(s.toString(), AbstractResponse.unmarshal(XML).toString());
    }
    
    @Test
    public void testWaitResponse() throws JAXBException{
        XML = (s = smf.createWaitResponse()).marshal();
        assertEquals(s.toString(), AbstractResponse.unmarshal(XML).toString());
    }
    
    @Test
    public void testFilePutResponse() throws JAXBException{
        XML = (s = smf.createFilePutResponse("GatorChomp.mp3", false)).marshal();
        assertEquals(s.toString(), AbstractResponse.unmarshal(XML).toString());
    }
    
    @Test
    public void testDataResponse() throws JAXBException{
        XML = (s = smf.createFileGetDataResponse("B64Data", 768, true)).marshal();
        assertEquals(s.toString(), AbstractResponse.unmarshal(XML).toString());
    }
    
    @Test
    public void testFileGetResponse() throws JAXBException{
        XML = (s = smf.createFileGetInitResponse("STuff")).marshal();
        assertEquals(s.toString(), AbstractResponse.unmarshal(XML).toString());
    }
    
    @Test
    public void testDeleteResponse() throws JAXBException{
        XML = (s = smf.createDeleteResponse("BCS_Champs_2011.tiff")).marshal();
        System.out.println(XML);
        System.out.println(AbstractResponse.unmarshal(XML).toString());
        assertEquals(s.toString(), AbstractResponse.unmarshal(XML).toString());
    }
    
    @After
    public void tearDown(){
        smf = null;
    }
    
}
