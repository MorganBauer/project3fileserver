package team3.src.message.response;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.JAXBContext;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

/**
 * This will generate the xsd structure for the response objects... useful 
 * if we want outside applications to know our messaging structure.
 * @author Joir-dan Gumbs
 *
 */
public class MySchemaOutputResolver extends SchemaOutputResolver {

	@Override
	public Result createOutput(String namespaceURI, String suggestedFileName) throws IOException {
		return new StreamResult(new File(new File("."),"responseSchema.xsd"));
	}

	/**
	 * @param args
	 * @throws JAXBException 
	 */
	public static void main(String[] args) throws IOException, JAXBException{
		JAXBContext context = JAXBContext.newInstance(AbstractResponse.class.getPackage().getName());
		context.generateSchema(new MySchemaOutputResolver());
		String data = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><SimpleResponse message=\"Directory\"><directory>.classpath</directory><directory>.DS_Store</directory><directory>.hg</directory></SimpleResponse>";
		AbstractResponse.unmarshal(data);
		System.out.println(data);
	}

}
