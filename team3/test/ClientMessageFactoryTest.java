package team3.test;
import javax.xml.bind.JAXBException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import team3.src.message.AbstractMessage;
import team3.src.message.ClientMessageFactory;
import team3.src.message.ErrorMessage;


public class ClientMessageFactoryTest {
	public ClientMessageFactory cmf;
	AbstractMessage m;
	String XML;
	@Before
	public void setup(){
		cmf = ClientMessageFactory.getFactory();
	}
	
	@Test
	public void createHelloMessage() throws JAXBException{
		XML = (m= cmf.createHelloMessage("800.001")).marshal();
		assertEquals(m.toString(), AbstractMessage.unmarshal(XML).toString());
	}
	
	@Test
	public void createTerminateMessage() throws JAXBException{
		XML = (m = cmf.createTerminateMessage("800.002")).marshal();
		assertEquals(m.toString(), AbstractMessage.unmarshal(XML).toString());
	}
	@Test
	public void createDirMessage() throws JAXBException{
		XML = (m = cmf.createDirListMessage("800.003", 4, 0, 5)).marshal();
		assertEquals(m.toString(), AbstractMessage.unmarshal(XML).toString());
	}
	@Test
	public void createErrorMessage() throws JAXBException{
		AbstractMessage e = cmf.createDirListMessage("800.003", 4, 0, 5);
		ErrorMessage n = cmf.createErrorMessage("800.004", e, "0x005", "Somethign happened");
		XML = n.marshal();
		assertEquals(n.toString(), AbstractMessage.unmarshal(XML).toString());
	}
	@Test
	public void createFileGetInitMessage() throws JAXBException{
		XML = (m = cmf.createFileGetMessage("800.005", "headphones.png", 3)).marshal();
		assertEquals(m.toString(), AbstractMessage.unmarshal(XML).toString());
	}
	@Test
	public void createFileGetPullMessage() throws JAXBException{
		XML = (m = cmf.createFileGetMessage("800.005", "headphones.png", 3, 1, 1024)).marshal();
		assertEquals(m.toString(), AbstractMessage.unmarshal(XML).toString());
	}
	@Test
	public void createFilePutInitMessage() throws JAXBException{
		XML = (m = cmf.createFilePutMessage("800.006", "眠れないよるわ.mp3", 10)).marshal();
		assertEquals(m.toString(), AbstractMessage.unmarshal(XML).toString());
	}
	@Test
	public void createFilePutDataMessage() throws JAXBException{
		XML = (m = cmf.createFilePutMessage("800.006", "眠れないよるわ.mp3", 10, "**BASE64DATA**", 0, 2048, false)).marshal();
		assertEquals(m.toString(), AbstractMessage.unmarshal(XML).toString());
	}
	@After
	public void cleanup(){
		cmf = null;
	}
}
