/**
 * Sample client for the SDMX RESTful API using stubs auto-generated from wadl2java 
 * @author Spyros Liapis
 * 
 */
package rest.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.List;

import javax.activation.DataSource;
import javax.xml.bind.JAXBException;

import org.estat.rest.ErrorException;
import org.estat.rest.HttpWwwSdmxOrgSdmxrestservice.AgencyschemeAgencyIDResourceIDVersion;
import org.estat.rest.HttpWwwSdmxOrgSdmxrestservice.CodelistAgencyIDResourceIDVersion;
import org.estat.rest.HttpWwwSdmxOrgSdmxrestservice.DataFlowRefKeyProviderRef;
import org.estat.rest.HttpWwwSdmxOrgSdmxrestservice.SchemaContextAgencyIDResourceIDVersion;
import org.sdmx.resources.sdmxml.schemas.v2_1.common.CodedStatusMessageType;
import org.sdmx.resources.sdmxml.schemas.v2_1.common.TextType;
import org.sdmx.resources.sdmxml.schemas.v2_1.message.ErrorType;
import org.sdmx.resources.sdmxml.schemas.v2_1.message.StructureType;

public class RestTestClient {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {

			// get Data
			DataFlowRefKeyProviderRef data = new DataFlowRefKeyProviderRef("ESTAT.B3,STS_IND_A,1.0", "A.FR+DE...WXIND", "FR1");
			DataSource dsdData = data.getAsApplicationVndSdmxStructurespecificdataXmlVersion21("2008", null, "2010-08-14T00:03:12+00:03", 12, null, null, null);

			/*
			 * the getAsApplication*() methods return the raw response in a Datasource.
			 * In this case no deserialisation occurs. The response is acquired by getting Datasource.getInputStream().
			 * For determining the response content the Datasource.getContentType() should be used to determine if the desired
			 * representation has been returned or an error occurred. 
			 */
			System.out.println("content type:" + dsdData.getContentType());
			if ( dsdData.getContentType().equals("application/vnd.sdmx.structurespecificdata+xml;version=2.1")) {
				// get StructureSpecificData SDMX-ML from the Datasource
				InputStream compactIs = dsdData.getInputStream();
				InputStreamReader ir = new InputStreamReader(compactIs);
				BufferedReader br = new BufferedReader(ir);
				String line = "";
				System.out.println("response:");
				while ((line = br.readLine()) != null) {
					System.out.println(line);
				}
			} else if (dsdData.getContentType().equals("application/vnd.sdmx.error+xml;version=2.1")) {
				// get the SDMX-ML Error message from the Datasource 
				InputStream errorIs = dsdData.getInputStream();
				// parse and handle error...
			}

			
			DataSource genericXmlVersion21 = data.getAsApplicationVndSdmxGenericdataXmlVersion21("2008", null, "2010-08-14T00:03:12+00:03", null, null, null, null);

			
			
			// get Codelist
			CodelistAgencyIDResourceIDVersion clist = new CodelistAgencyIDResourceIDVersion("ESTAT", "CL_FREQ", "1.0");
			DataSource clXmlVersion21 = clist.getAsApplicationVndSdmxStructureXmlVersion21("allstubs", null);
			
	
			
			/*
			 * Besides the getApplication*() method for getting raw the response wrapped in a Dataset,
			 * it's possible to call the specific getter so as to deserialise the response to beans created by the wadl2java to hold information in XML.
			 * e.g. StrutureType for the Structure messages.
			 * 
			 * In this case the error messages are deserialised also. Therefore an ErrorException is thrown and the information
			 * of the error message can extracted as shown below.
			 */
			try {
				StructureType structure = clist.getAsStructureType("allstubs", null);
			} catch (ErrorException e) {
				ErrorType error = e.getFaultInfo();
				List<CodedStatusMessageType> list = error.getErrorMessage();
				CodedStatusMessageType statusTextType = list.get(0);
				// error code
				statusTextType.getCode();
				// error texts in different lang
				List<TextType> texts = statusTextType.getText();
				for (TextType text : texts) {
					text.getLang();
					text.getValue();
				}
			}

			// "" is equivalent to "latest". It's should be used null in the URL template parameters. This holds only for parameters.
			CodelistAgencyIDResourceIDVersion clist2 = new CodelistAgencyIDResourceIDVersion("ESTAT", "CL_FREQ", "");
			DataSource xlXmlVersion21_2 = clist2.getAsApplicationVndSdmxStructureXmlVersion21("allstubs", null);


			// get all agencies available in from the WS
			AgencyschemeAgencyIDResourceIDVersion  agSch = new AgencyschemeAgencyIDResourceIDVersion("all", "AGENCIES", "1.0");
			DataSource agDatasource = agSch.getAsApplicationVndSdmxStructureXmlVersion21();
			

			// get CompactSchema
			SchemaContextAgencyIDResourceIDVersion schema = new SchemaContextAgencyIDResourceIDVersion("datastructure", "ESTAT", "STS", "2.1");
			DataSource compactSchema = schema.getAsApplicationVndSdmxSchemaXmlVersion21("STS_INDICATOR", null);



		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
