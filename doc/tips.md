# Useful tips

## Security considerations

This section describes useful security measures for SDMX REST services.

### Authentication

Authentication refers to the process of uniquely identifying an entity. In the context of a web service, we need to distinguish between service authentication and client authentication.

Clients of web services have a high interest in ensuring that they are connected to the service they intend to consume and not to a rogue service masquerading as a trusted entity. To support this, use SSL/TLS and offer clients the possibility to consume the web service over HTTPS. This allows the client to validate that the certificate matches the domain name of the service, is issued by a trusted authority, and has not expired.

When restrictions apply to the data and metadata published, it is important for the service provider to be able to uniquely identify the client. To support this requirement, use HTTP basic authentication over SSL/TLS. If stronger authentication is required, use SSL client certificates instead.

### Confidentiality

Confidentiality refers to the process of guaranteeing that resources cannot be accessed by unauthorised users. This requirement is a key requirement for the SDMX web services when restrictions apply to the data and metadata published, as both clients and services have a high interest in ensuring their data is not illegally accessed.

For these web services, use SSL/TLS to support confidentiality during the transfer between the service and the client. SSL/TLS supports this requirement using a combination of symmetric and asymmetric encryption.

### Integrity

Integrity refers to the process of guaranteeing that resources cannot be accidentally or maliciously modified. Support this requirement using SSL/TLS. SSL/TLS supports this requirement by calculating a message digest.

## Tips for data consumers

### Connect to the service via https

So, you want to keep a local database up to date with data from your favourite data providers? Or maybe, you just want to build a few visualisations with data coming from various web services? Great, that's what SDMX web services are all about! But are you sure it's a good idea to do so without first checking the identity of the data providers? If not, then make sure you connect to the web service over https.

### Know your mime type

If you don't specify the format you want (using the `HTTP Accept header`), the behaviour is unpredictable as the service decides the format to be returned and this decision may change over time. The same applies if you specify a generic mime type, such as `application/xml`. That change could break your client. So, don't rely on the default, and be sure to specify the desired format using the HTTP Accept header with a fully qualified mime-type (e.g.: `application/vnd.sdmx.genericdata+xml;version=2.1`).

### Get only what you need

Let's say you want to get some exchange rates from a web service. And let's say you check regularly to see whether the rates have been updated. Do you want to get everything all the time, included all the data that have not changed, or do you want something more efficient? If it's the latter, make sure to use the `updatedAfter` parameter. In the same vein, if you just want the data and don't need the associated metadata, say so using the `detail` parameter... For additional information, see the section about the [data queries](data.md).

### Opt for fat-free messages

Plain text formats (XML, JSON, CSV, etc.) compress very well. Compressed data are typically several times smaller than uncompressed ones. In our tests, the compressed files were between 3 and ... 47 times smaller than the uncompressed ones, depending on the type of query and the selected formats. So, in order to optimise network transfer times, request **compressed representations** of the data, using the appropriate HTTP `Accept-Encoding header`. See the section about [HTTP Content Negotiation](conneg.md) for additional information.

### Check the response status code

The response status code is used to report potential issues. So, check it. It’s not always 200 (or 42). See the section about the [status codes](status.md) for additional information.

## Tips for data providers

### Offer access over HTTPS

Clients of web services have a high interest in ensuring that they are connected to the service they intend to consume and not to a rogue service masquerading as a trusted entity. To support this, please offer the possibility to **consume your service over HTTPS**, and make sure your **SSL/TLS certificate** is/remains valid (i.e.: the domain/subdomains should match, the certificate should not be expired, etc.).

### Check the mime-type and support various formats

By default, SDMX 2.1 REST services are expected to return the most recent version of the SDMX-ML Generic Data format they support, while SDMX 3.0 REST services are expected to return the most recent version of SDMX-JSON. However, clients can use the HTTP Accept header to specify the format they want, so check it and make sure you don't return XML if they asked for JSON ;-). Also, if you don't offer the requested format, simply return an HTTP 406 status code. This being said, if you offer them a **choice between various formats**, you will most likely improve the usability and accessibility of your service, as different clients have different needs. See the section about [HTTP Content Negotiation](conneg.md) for a list of SDMX formats.

### Support data compression

Plain text formats (XML, JSON, CSV, etc.) compress very well. Compressed data are typically several times smaller than uncompressed ones. In our tests, the compressed files were between 3 and ... 47 times smaller than the uncompressed ones, depending on the type of query and the selected formats. So, in order to optimise network transfer times, offer the possibility to **get compressed representations** of the data, using the appropriate HTTP `Accept-Encoding header`. See the section about [HTTP Content Negotiation](conneg.md) for additional information.

### Support Cross-origin queries

Clients written in Python, Java, .Net, etc. typically won't have any problems getting the data. But what about the HTML5/JavaScript clients running within a browser? Because of the [Same-origin policy](https://en.wikipedia.org/wiki/Same-origin_policy), these won't be able to access your web service without additional work, unless you enable the sharing of resources across domains. One way of doing that, is to configure your service to **[support CORS headers](https://en.wikipedia.org/wiki/Cross-origin_resource_sharing)**. Just make sure the CORS headers are also added in case of errors, else the JavaScript clients will be left clueless about why an error happened.

### Support caching headers

The SDMX API allows a client to retrieve all data matching a query. In addition, using the `updatedAfter` parameter, it also allows a client to only retrieve what has changed since the last time the query was issued. This is a very powerful feature but it requires smart clients, able to handle partial updates. For the others, there is a 3rd option: retrieve **all data matching the query, but only if some of the matching data have changed**. To do so, simply add support for the caching mechanism offered by HTTP. In that scenario, a client sends an `If-Modified-Since` header (or an `If-None-Match`), along with a timestamp (or an ETag), and the web service returns the data, if something has changed, or an `HTTP 304 Not modified` status code otherwise.

If you add support for caching headers, don't forget to indicate, via the `Vary` header, which HTTP headers should be taken into account by the cache to build the cache key. Typical examples would be the HTTP `Accept` and `Accept-Encoding` header, as you don't want the cache to return SDMX-ML when the client has requested SDMX-JSON...

### Version your API

The SDMX-REST API follows semantic versioning. This means that the type and impact of changes is expressed using an API number made of 3 digits (e.g. v1.2.3).

- The first digit (1 in the above example) indicates that changes (either new features or bug fixes) are not backward compatible.
- The second digit (2 in the above example) indicates that features have been added in a backward compatible manner.
- The third digit (3 in the above example) indicates that bugs have been fixed in a backward compatible manner.

For clients of an SDMX-REST API, this means that only an increment of the first digit might be problematic, while any change to the second or third digit should be transparent to them (i.e. clients might not be able to leverage new features but at least they should not break).

It is considered best practice to version an API, to avoid breaking clients in case of backward incompatible changes. As only changes to the first digit represent changes that are not backward compatible, it is recommended to **version the API based on the value of the first digit only**, e.g. `https://ws-entry-point/v1/`.

### Be liberal in what you accept

It may happen that a client sends a request containing both valid and invalid information. For example, it could be that a data query contains codes that exist in the codelist (which is valid), as well as codes that don't exist in the codelist (which is invalid). In such cases, it is recommended to be lenient and return the data matching the query, instead of throwing an error because of the invalid codes.

The same applies when all codes supplied are invalid. Instead of throwing an HTTP client error code, it is recommended to return a 204 (No content), to emphasize that an absence of results is an empty but still healthy state.
