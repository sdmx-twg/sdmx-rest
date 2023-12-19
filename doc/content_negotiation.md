# HTTP Content-Negotiation

[HTTP Content Negotiation](http://www.w3.org/Protocols/rfc2616/rfc2616-sec12.html) is a mechanism offered by HTTP that allows clients to indicate their preferred representation, language, encoding, etc. for a resource.

> [!TIP]
> HTTP Content-Negotiation is the standard way to support features such as format or language selection. However, there are use cases where an alternative is required. Please check the [Extending the SDMX REST API](extend.md) section for additional information about the alternative way of supporting these features. 

## Format selection

Using the HTTP Content Negotiation mechanism, the client specifies the desired format for the resource using the [Accept header](http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html).

Along with official media types (e.g.: text/html, application/xml, etc), the HTTP standard also defines a syntax, which the SDMX RESTful API leverages, allowing a service to define its own types:

    application/vnd.sdmx.[type]+[format];version=[version]

A few examples are listed below:

- application/vnd.sdmx.structure+json;version=2.0.0
- application/vnd.sdmx.data+json;version=2.0.0
- application/vnd.sdmx.structure+xml;version=3.0.0

The full list of media types is available in the _Response types_ section of the various query pages:

- [Data queries](data.md#response-types)
- [Structure queries](structures.md#response-types)
- [Reference metadata queries](metadata.md#response-types)
- [Schema queries](schema.md#response-types)
- [Data availability queries](availability.md#response-types)

## Selection of the appropriate version

Usually, clients will indicate the precise version of the format they support (for example, version 2.0.0 of SDMX-JSON). 

However, clients may also use semantic versioning, to indicate that they are capable of handling any version matching the supplied semantic versioning string (e.g. `2.3+.1`).

Examples:

- application/vnd.sdmx.structure+xml;version=3.0.0+
- application/vnd.sdmx.data+json;version=2.3+.1
- application/vnd.sdmx.data+csv;version=2+.0.1

For additional information about semantic versioning in SDMX, please refer to [Section 06 (Technical Notes)](https://sdmx.org/wp-content/uploads/SDMX_3-0-0_SECTION_6_FINAL-1_0.pdf) of the SDMX 3.0 Documentation package.

## Selection of the appropriate language

The [Accept-Language header](http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html) is used to indicate the language preferences of the client. Multiple values, along with their respective weights, are possible. For example:

    Accept-Language: ru, en-gb;q=0.8, en;q=0.7

## Enabling data compression

Compression can be enabled using the appropriate [Accept-Encoding header](http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html).

## Helping web caches and Content Delivery Networks (CDN)

The [Vary header](http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html) is used to indicate the list of headers that are relevant for a particular service.

For example, services that offer data in multiple formats will rely on the HTTP Accept header, but this header will likely be irrelevant for services that support only one format. Using the `Vary` header to indicate which headers are effectively used by the server helps web caches and Content Delivery Networks to build appropriate cache keys.
