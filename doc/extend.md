# Extending the SDMX REST API

One powerful aspect of REST APIs is that they can be easily extended to accommodate more resources or parameters. Extending a RESTful API does not render it incompatible to its predecessor, as long as you respect the existing resources and parameters and implement them according to the specification.

The purpose of this page is to show how the SDMX REST API can be extended, for example by adding the possibility to select the representation using query parameters.

## Extending query parameters

The easiest extension one can perform on a REST API is adding custom query parameters. Using path parameters is possible as well but more complex as the order of path parameters is significant. Remember though that the parameters must not clash with the parameters of the original API specification, i.e. the SDMX REST API in our case. Of course, apart from adding new query parameters, their values may also be extended instead.

As a general rule, any extension to the SDMX REST API has a lower order of precedence compared to the standard functionality. For the specific example of the representation as query parameters, described in this section, the HTTP `Accept` header is higher in the order of precedence. This means that for any vendor specific mime-type in the HTTP `Accept` header, the representation query parameters should be ignored.

Apart from the behaviour and order of precedence in regard to the standard REST API specifications, the default behaviour of the extension should be documented as well.

## Use case: Ease the selection of the desired representation

### REST APIs and web browsers

REST APIs have become popular, not only for their simplicity and power, but also for being intuitive and easy to use through a web browser. Nevertheless, there is one important aspect of the SDMX REST API that is not available to end users via browsers, like the format and version of the returned resource.

Currently, the SDMX REST API uses the [HTTP content negotiation](content_negotiation.md) mechanism to allow clients to select the resource representation to be returned.

The problem is that end users, on their browser, cannot set the HTTP `Accept` header, unless they use a dedicated extension (aka plugin) such as Postman; hence, they are always presented with the default representation that the specific web service implementation has. For this use case, some providers of SDMX REST web services have extended their API to make it easier for end users to select the required representation.

### Query parameters for representation

As an example of extending the SDMX REST API, a proposal for new query parameters is presented here. Since the normative way of the SDMX REST specifications for requesting representation is the HTTP content negotiation mechanism and, in principle, the API should not specify the same functionality in alternative ways, this proposal should be treated as an example of how to extend the API and not as a normative part of the standard.

The *mime-types* for the `Accept` header, as specified by the SDMX REST API, may be found [here](content_negotiation.md). They follow the following pattern:
`application/vnd.sdmx.<representation>+<format>;version=<version>`, where:

- `<representation>` is any of the available SDMX message types, e.g. structurespecific, compact, data, schema, etc.
- `<format>` is the technical format, e.g. xml, json, and
- `<version>` is the version of the representation.

In the example presented here, the `<format>` will be omitted, since two parameters can sufficiently describe the required representation. Hence, two new query parameters may be specified to allow for a similar functionality, i.e. `formatVersion` and `format`. The latter is proposed as a more generic term compared to representation, in order to include the mime-type `<format>` part.

#### The `format` query parameter

The `format`, as explained above, represents a combination of the representation and technical format of the messages. Hence, it includes the following possible values:

- `genericdata` for the SDMX-ML Generic Dataset
- `structurespecificdata` for the SDMX-ML Structure Specific Dataset
- `generictimeseriesdata` for the SDMX-ML Generic Time Series Dataset
- `structurespecifictimeseriesdata` for the SDMX-ML Structure Specific Time Series Dataset
- `genericmetadata` for the SDMX-ML Generic Reference Metadataset
- `structurespecificmetadata` for the SDMX-ML Structure Specific Reference Metadataset
- `structure` for all SDMX-ML Structural Metadata
- `schema` for all SDMX XML Generated Schema
- `jsondata` for the SDMX-JSON Dataset
- `csvdata` for the SDMX-CSV Dataset

#### The `formatVersion` query parameter

The `formatVersion` represents the version of the message format, for any format specified by the `format` query parameter.

The versions per the available formats, may be found in the section [HTTP content negotiation](content_negotiation.md).

### Default behaviour

The default behaviour of this extension is as follows:

- Omitting both attributes: falls back to the standard's default behaviour.
- Omitting `formatVersion`: the Web Service may define the default version; following the SDMX general behaviour for versions, the latest available should be used.
- Omitting `format`: the attribute `formatVersion` should be also ignored, if specified.

### Examples

To retrieve an SDMX-ML 2.1 Structure Specific dataset, using the representation query parameters:

    https://ws-entry-point/data/dataflow/ECB/EXR/+/M.USD.EUR.SP00.A?format=structurespecificdata&formatVersion=2.1

To retrieve an SDMX-ML 2.1 Codelist, using the representation query parameters:

    https://ws-entry-point/structure/codelist/ECB/CL_FREQ?format=structure&formatVersion=2.1

To retrieve a json dataset, using the representation query parameters:

    https://ws-entry-point/data/dataflow/ECB/EXR/+/M.USD.EUR.SP00.A?format=jsondata&formatVersion=1.0.0

## Other parameters used for Content-Negotiation

While format selection is probably the most obvious example of content-negotiation (via the HTTP `Accept` header), there are other use cases, such as language selection or output encoding. As for format selection, the normative way for SDMX REST services to support such use cases is via the HTTP content negotiation mechanism. However, if there is a need to set this via a web browser without any plugin such as Postman, the following parameters should be considered:

| Parameter | Function | Allowed values | Default behaviour |
| --- | --- | --- | --- |
| compression | Select the desired compression algorithm for the response | `gzip`, `compress`, `deflate`, `br` | If compression is not set, then the response must be returned without compression. If the selected compression algorithm is not supported by the service, it must return a `406` status code. 
| lang | Select the preferred natural language for the response | An ISO 639 2-letter language code, optionally followed by an ISO 3166 2-letter country code, indicating the country variant (e.g. fr-CH, en, en-MT, es, es-AR). The keyword `all` can be used to indicate that all available languages should be returned. | If the preferred language is not available, the service must return a `406` status code. 
