package team3.src.message;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.JAXBContext;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;


public class MySchemaOutputResolver extends SchemaOutputResolver {

	@Override
	public Result createOutput(String namespaceURI, String suggestedFileName) throws IOException {
		return new StreamResult(new File(new File("."),suggestedFileName));
	}

	/**
	 * @param args
	 * @throws JAXBException 
	 */
	public static void main(String[] args) throws IOException, JAXBException{
		JAXBContext context = JAXBContext.newInstance(AbstractMessage.class.getPackage().getName());
		context.generateSchema(new MySchemaOutputResolver());
	}

}
