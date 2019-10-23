## HTTP Content-Negotiation

[HTTP Content Negotiation](http://www.w3.org/Protocols/rfc2616/rfc2616-sec12.html) is a mechanism offered by HTTP that allows clients to indicate their preferred representation, language, encoding, etc. for a resource.

### Selection of the Appropriate Representation

Using the HTTP Content Negotiation mechanism, the client specifies the desired format and version of the resource using the [Accept header](http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html).

Along with official mime types (e.g.: text/html, application/xml, etc), the HTTP standard also defines a syntax, which the SDMX RESTful API leverages, allowing a service to define its own types:

    application/vnd.sdmx.[format]+xml;version=[version]

where [format] should be replaced with the desired format (i.e. : genericdata, structurespecificdata, structure, etc) and [version] should be replaced with one of the versions of the SDMX standard, starting with SDMX 2.1.

A few examples are listed below

- SDMX-ML Generic Data Format, version 2.1: application/vnd.sdmx.genericdata+xml;version=2.1
- SDMX-ML Structure Specific Data Format, version 2.1: application/vnd.sdmx.structurespecificdata+xml;version=2.1
- SDMX-ML Structure Format, version 2.1: application/vnd.sdmx.structure+xml;version=2.1

In case the client does not specify the desired format and version of the response message, or only specifies the generic application/xml format, the SDMX RESTful web service should return:

- The most recent version, that the service supports, of the SDMX-ML Structure format for structural metadata queries;
- The most recent version, that the service supports, of the SDMX-ML Generic Data format for data queries;
- The most recent version, that the service supports, of the SDMX-ML Generic Metadata format for metadata queries.

The list below indicates the valid formats for SDMX RESTful web services, compliant with version 2.1 of the SDMX standard:

    application/vnd.sdmx.genericdata+xml;version=2.1
    application/vnd.sdmx.structurespecificdata+xml;version=2.1
    application/vnd.sdmx.generictimeseriesdata+xml;version=2.1
    application/vnd.sdmx.structurespecifictimeseriesdata+xml;version=2.1
    application/vnd.sdmx.data+json;version=1.0.0
    application/vnd.sdmx.data+csv;version=1.0.0
    application/vnd.sdmx.genericmetadata+xml;version=2.1
    application/vnd.sdmx.structurespecificmetadata+xml;version=2.1
    application/vnd.sdmx.structure+xml;version=2.1
    application/vnd.sdmx.structure+json;version=1.0.0
    application/vnd.sdmx.schema+xml;version=2.1
    
### Selection of the Appropriate language

The [Accept-Language header](http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html) is used to indicate the language preferences of the client. Multiple values, along with their respective weights, are possible. For example:

```
Accept-Language: ru, en-gb;q=0.8, en;q=0.7
```

### Enabling data compression

Compression could be enabled using the appropriate [Accept-Encoding header](http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html).

### Helping web caches and Content Delivery Networks (CDN)

The [Vary header](http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html) is used to indicate the list of headers that are relevant for a particular service.

For example, services that offer data in multiple formats will rely on the HTTP Accept header, but this header will likely be irrelevant for services that support only one format. Using the `Vary` header to indicate which headers are effectively used by the server helps web caches and Content Delivery Networks to build appropriate cache keys.
