## Selection of the Appropriate Representation

Selection of the appropriate formats for the response message is made using the mechanisms defined for HTTP Content Negotiation<sup>[21](#fn-21)</sup>. Using the HTTP Content Negotiation mechanism, the client specifies the desired format and version of the resource using the Accept HTTP header<sup>[22](#fn-22)</sup>.

Along with official mime types (e.g.: text/html, application/xml, etc), the standard also defines a syntax allowing a service to define its own types. The SDMX Restful API makes use of this functionality and the syntax is as follows:

    application/vnd.sdmx.[format]+xml;version=[version]

where [format] should be replaced with the desired format (i.e. : genericdata, structurespecificdata, structure, etc) and [version] should be replaced with one of the versions of the SDMX standard, starting with SDMX 2.1 (e.g.: 2.1, future SDMX versions, etc)<sup>[23](#fn-23)</sup>.

A few examples are listed below

- SDMX-ML Generic Data Format, version 2.1: application/vnd.sdmx.genericdata+xml;version=2.1
- SDMX-ML Structure Specific Data Format, version 2.1: application/vnd.sdmx.structurespecificdata+xml;version=2.1
- SDMX-ML Structure Format, version 2.1: application/vnd.sdmx.structure+xml;version=2.1

In case the client does not specify the desired format and version of the response message, or only specifies the generic application/xml format, the SDMX RESTful web service should return:

- The most recent version, that the service support, of the SDMX-ML Structure format for structural metadata queries;
- The most recent version, that the service support, of the SDMX-ML Generic Data format for data queries;
- The most recent version, that the service support, of the SDMX-ML Generic Metadata format for metadata queries.

The list below indicates the valid formats for SDMX RESTful web services, compliant with version 2.1 of the SDMX standard:

    application/vnd.sdmx.genericdata+xml;version=2.1
    application/vnd.sdmx.structurespecificdata+xml;version=2.1
    application/vnd.sdmx.generictimeseriesdata+xml;version=2.1
    application/vnd.sdmx.structurespecifictimeseriesdata+xml;version=2.1
    application/vnd.sdmx.genericmetadata+xml;version=2.1
    application/vnd.sdmx.structurespecificmetadata+xml;version=2.1
    application/vnd.sdmx.structure+xml;version=2.1
    application/vnd.sdmx.schema+xml;version=2.1

<a name="fn-21"></a>[21] For additional information, please refer to http://www.w3.org/Protocols/rfc2616/rfc2616-sec12.html

<a name="fn-22"></a>[22] For additional information, please refer to http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html

<a name="fn-23"></a>[23] For the time being, only version 2.1 is supported as version number.
