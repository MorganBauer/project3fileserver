package team3.test;
import javax.xml.bind.JAXBException;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;

import team3.src.message.client.AbstractClientMessage;
import team3.src.message.ClientMessageFactory;

import org.junit.Test;

public class ClientMessageFactoryTest extends TestCase{
	public ClientMessageFactory cmf;
	AbstractClientMessage m;
	String XML;
	
	@Before
	public void setUp(){
		cmf = ClientMessageFactory.getFactory();
	}
	
	@Test
	public void testHelloMessage() throws JAXBException{
		XML = (m= cmf.createHelloMessage("800.001")).marshal();
		assertEquals(m.toString(), AbstractClientMessage.unmarshal(XML).toString());
	}
	
	@Test
	public void testTerminateMessage() throws JAXBException{
		XML = (m = cmf.createTerminateMessage("800.002")).marshal();
		assertEquals(m.toString(), AbstractClientMessage.unmarshal(XML).toString());
	}
	@Test
	public void testDeleteMessage() throws JAXBException{
	    XML = (m = cmf.createDeleteMessage("900.001", 2, "UFBCSChamps2011.mov")).marshal();
	    assertEquals(m.toString(), AbstractClientMessage.unmarshal(XML).toString());
	}
	
	@Test
	public void testDirMessage() throws JAXBException{
		XML = (m = cmf.createDirListMessage("800.003", 4, 0, 5)).marshal();
		assertEquals(m.toString(), AbstractClientMessage.unmarshal(XML).toString());
	}
	@Test
	public void testFileGetInitMessage() throws JAXBException{
		XML = (m = cmf.createFileGetMessage("800.005", "headphones.png", 3)).marshal();
		assertEquals(m.toString(), AbstractClientMessage.unmarshal(XML).toString());
	}
	@Test
	public void testFileGetPullMessage() throws JAXBException{
		XML = (m = cmf.createFileGetMessage("800.005", "headphones.png", 3, 1, 1024)).marshal();
		assertEquals(m.toString(), AbstractClientMessage.unmarshal(XML).toString());
	}
	@Test
	public void testFilePutInitMessage() throws JAXBException{
		XML = (m = cmf.createFilePutMessage("800.006", "夏のうねり.mp3", 10)).marshal();
		assertEquals(m.toString(), AbstractClientMessage.unmarshal(XML).toString());
	}
	@Test
	public void testFilePutDataMessage() throws JAXBException{
		XML = (m = cmf.createFilePutMessage("800.006", "花火.tiff", 10, "**BASE64DATA**", 0, 2048, false)).marshal();
		assertEquals(m.toString(), AbstractClientMessage.unmarshal(XML).toString());
	}
	@After
	public void tearDown(){
		cmf = null;
	}
}
