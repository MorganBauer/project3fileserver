package team3.test;
import javax.xml.bind.JAXBException;

import junit.framework.TestCase;

import team3.src.message.AbstractClientMessage;
import team3.src.message.FileGetMessage;
import team3.src.message.FilePutMessage;
import team3.src.message.SimpleMessage;

import org.junit.Test;


public class SimpleMessageTest extends TestCase{

	AbstractClientMessage message;
	
	@Test
	public void testHello() throws JAXBException{
		SimpleMessage msg2;
		message = SimpleMessage.buildHelloMessage("890.001");
		msg2 =  (SimpleMessage) SimpleMessage.unmarshal(message.marshal());
		assertEquals(msg2.toString(), message.toString());
	}
	
	@Test
	public void testTerminate() throws JAXBException{
		SimpleMessage msg2;
		message = SimpleMessage.buildTerminateMessage("890.002");
		msg2 =  (SimpleMessage) SimpleMessage.unmarshal(message.marshal());
		assertEquals(msg2.toString(), message.toString());
	}
	
	@Test
	public void testDirList() throws JAXBException{
		SimpleMessage msg2;
		message = SimpleMessage.buildDirListMessage("890.003",1,2,5);
		msg2 =  (SimpleMessage) SimpleMessage.unmarshal(message.marshal());
		assertEquals(msg2.toString(), message.toString());
	}
	
	@Test
	public void testFileGetMessage1() throws JAXBException{
		FileGetMessage msg2;
		message = FileGetMessage.buildInitMessage("890.004","headphones.png",5);
		msg2 =  (FileGetMessage) FileGetMessage.unmarshal(message.marshal());
		assertEquals(msg2.toString(), message.toString());
	}
	
	@Test
	public void testFileGetMessage2() throws JAXBException{
		FileGetMessage msg2;
		message = FileGetMessage.buildPullMessage("890.005","headphones.png",5, 0, 1024);
		msg2 =  (FileGetMessage) FileGetMessage.unmarshal(message.marshal());
		assertEquals(msg2.toString(), message.toString());
	}
	
	@Test
	public void testFilePutMessage1() throws JAXBException{
		FilePutMessage msg2;
		message = FilePutMessage.buildFilePutRequestMessage("890.006", "headphones.png", 10);
		msg2 =  (FilePutMessage) FilePutMessage.unmarshal(message.marshal());
		assertEquals(msg2.toString(), message.toString());
	}
	
	@Test
	public void testFilePutMessage2() throws JAXBException{
		FilePutMessage msg2;
		message = FilePutMessage.buildFilePutDataMessage("890.007", "headphones.png", 10, "~~ SAMPLE BASE64DATA ~~", 0, 2048, false);
		msg2 =  (FilePutMessage) FilePutMessage.unmarshal(message.marshal());
		assertEquals(msg2.toString(), message.toString());
	}
}
