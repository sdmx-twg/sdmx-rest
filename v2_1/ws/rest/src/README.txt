WADL for the SDMX RESTful API specification.
------------------------------------------

File listing
- SDMXRestTypes.xsd: XSD for defining types of paramteres used in WADL. It is imported by the WADL file.
- sdmx-rest.wadl: The WADL file of the REST specifications
- RestTestClient.java sample client java code for the wadl2java tool.
- wadl.xsd: The XSD of the WADL file. It can be used for validating WADL files.
- xml.xsd: The XSD for the http://www.w3.org/XML/1998/namespace namespace. It is included so the schemas can be validated offline.

Builiding a client from WADL
Using the wadl2java it generates the appropriate client stubs in Java.
Sample command:
wadl2java -o src -p org.estat.rest -a sdmx-rest.wadl
To call these client stubs see the sample code in RestTestClient.java.

Resources
http://www.w3.org/Submission/wadl/
http://java.net/projects/wadl/
http://java.net/downloads/wadl/latest/
