## Selection of the Appropriate Representation

Selection of the appropriate formats for the response message is made using the mechanisms defined for HTTP Content Negotiation[^C46_21]. Using the HTTP Content Negotiation mechanism, the client specifies the desired format and version of the resource using the Accept HTTP header[^C46_22].

Along with official mime types (e.g.: text/html, application/xml, etc), the standard also defines a syntax allowing a service to define its own types. The SDMX Restful API makes use of this functionality and the syntax is as follows:

    application/vnd.sdmx.[format]+xml;version=[version]

where [format] should be replaced with the desired format (i.e. : genericdata, structurespecificdata, structure, etc) and [version] should be replaced with one of the versions of the SDMX standard, starting with SDMX 2.1 (e.g.: 2.1, future SDMX versions, etc)[^C46_23].

A few examples are listed below

- SDMX-ML Generic Data Format, version 2.1: application/vnd.sdmx.genericdata+xml;version=2.1
- SDMX-ML Structure Specific Data Format, version 2.1: application/vnd.sdmx.structurespecificdata+xml;version=2.1
- SDMX-ML Structure Format, version 2.1: application/vnd.sdmx.structure+xml;version=2.1

In case the client does not specify the desired format and version of the response message, or only specifies the generic application/xml format, the SDMX RESTful web service should return:

- most recent version, that the service support, of the SDMX-ML Structure format for structural metadata queries;
- most recent version, that the service support, of the SDMX-ML Generic Data format for data queries;
- most recent version, that the service support, of the SDMX-ML Generic Metadata format for metadata queries.

The list below indicates the valid formats for SDMX RESTful web services, compliant with version 2.1 of the SDMX standard:

    application/vnd.sdmx.genericdata+xml;version=2.1
    application/vnd.sdmx.structurespecificdata+xml;version=2.1
    application/vnd.sdmx.generictimeseriesdata+xml;version=2.1
    application/vnd.sdmx.structurespecifictimeseriesdata+xml;version=2.1
    application/vnd.sdmx.genericmetadata+xml;version=2.1
    application/vnd.sdmx.structurespecificmetadata+xml;version=2.1
    application/vnd.sdmx.structure+xml;version=2.1
    application/vnd.sdmx.schema+xml;version=2.1

[^C46_21]: For additional information, please refer to http://www.w3.org/Protocols/rfc2616/rfc2616-sec12.html
[^C46_22]: For additional information, please refer to http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html
[^C46_23]: For the time being, only version 2.1 is supported as version number.
