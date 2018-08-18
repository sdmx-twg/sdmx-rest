## Error handling and status information

RESTful web services should indicate errors using the proper HTTP status code. In addition, whenever appropriate, the error should also be returned using the error message offered starting with version 2.1 of SDMX-ML.

### Error categories

The numbering of error messages divides the three types of messages up, and provides for web services to implement custom messages as well:

- 000–499: Client errors
- 500-999: Server errors
- 1000 and up: Custom errors

### Client errors

#### No results found - 100

If the result from the query is empty the web service should return this message. This is a way to inform the client that the result is empty.

#### Unauthorized - 110

For use when authentication is needed but has failed or has not yet been provided.

#### Syntax error - 140

This error code is used when the query string doesn’t comply with the SDMX RESTful interface.

#### Semantic error - 150

A web service should return this error when a request is syntactically correct but fails a semantic validation or violates agreed business rules.

### Server errors

#### Internal Server Error – 500

The web service should return this error code when none of the other error codes better describes the reason for the failure of the service to provide a meaningful response.

#### Not implemented – 501

All SDMX web services should implement all the standard interfaces, even if their only function is to return this error message. This eases interoperability between SDMX compliant web services and it also eases the development of generic SDMX web services clients. If the web service has not yet implemented one of the methods defined in the API, then it should return this error.

#### Service unavailable - 503

If a web service is temporarily unavailable because of maintenance or for some other similar reasons, then it should return this error code.

#### Response size exceeds service limit - 510

The request results in a response that is larger than the server is willing or able to process. In case the service offers the possibility to users to download the results of large queries at a later stage (for instance, using asynchronous web services), the web service may choose to indicate the (future) location of the file, as part of the error message. 

### Custom errors - 1000+

Web services can use codes 1000 and above for the transmission of service specific error messages. However, it should be understood that different services may use the same numbers for different errors, so the documentation provided by the specific service should be consulted when implementing this class of errors.

### SDMX to HTTP Error Mapping

The following table maps the SDMX error codes with the HTTP status code.

SDMX error | HTTP status code
---|---
100 No results found | 404 Not found
110 Unauthorized | 401 Unauthorized
130 Response too large due to client request | 413 Request entity too large
140 Syntax error | 400 Bad syntax
150 Semantic error | 403 Forbidden
500 Internal Server error | 500 Internal server error
501 Not implemented | 501 Not implemented
503 Service unavailable | 503 Service unavailable
510 Response size exceeds service limit | 413 Request entity too large
1000+ | 500 Internal server error

### Other useful HTTP status codes

Apart from the error codes mentioned above, there are a few additional [HTTP status codes](https://en.wikipedia.org/wiki/List_of_HTTP_status_codes) that are typically encountered when consuming a SDMX RESTful web service. These are:

- `200`: This is the standard response for successful HTTP requests. With `GET` requests, it is used to indicate that the request was successfully processed and that the data are available in the body of the response.
- `304`: This is used to indicate that there are no changes since the version specified by the request headers `If-Modified-Since` or `If-None-Match`. The response does not include matching data, as the client can still use the previously-downloaded copy.
- `414`: This status code (URI Too Long) indicates that the server is refusing to service the request because the request-target is longer than the server is willing to interpret. See [the wiki page for information on the URL length](https://github.com/sdmx-twg/sdmx-rest/wiki/URL-length-in-REST).
