# Error handling and status information

RESTful web services use HTTP status codes to indicate errors. In principle, any [code defined by HTTP](https://en.wikipedia.org/wiki/List_of_HTTP_status_codes) can therefore be returned by an SDMX RESTful web service but a few of the most common ones are described below.

Codes below 400 are considered normal (i.e. non-problematic). Codes between 400 and 499 are considered client errors. Codes greater or equal to 500 indicate a server error or problem.

HTTP status code | Description
---|---
200 | This is the standard response for successful HTTP requests. With `GET` requests, it is used to indicate that the request was successfully processed and that the data are available in the body of the response.
204 | If the result from the query is empty, the web service should return this code. `204` is preferred to `404` in this case, as a lack of results matching a query is an “empty but still healthy state”.
304 | This is used to indicate that there are no changes since the version specified by the request headers `If-Modified-Since` or `If-None-Match`. The response does not include matching data, as the data downloaded previously is still valid.
400 | This error code is used when the query doesn’t comply with the SDMX REST API.
401 | For use when authentication is needed but has failed or has not yet been provided.
403 | For use when authentication has been provided correctly, but user is not authorized to access the query results.
406 | If a client asks for a resource representation that the web service does not offer, a 406 HTTP status code should be returned.
413 | The request would result in a response that is larger than the server is willing or able to process. In case the service offers the possibility to users to download the results of large queries at a later stage (for instance, using asynchronous web services), the web service may choose to indicate the (future) location of the file, as part of the error message.
414 | This status code (URI Too Long) indicates that the server is refusing to service the request because the request-target is longer than the server is willing to interpret. See [the wiki page for information on the URL length](https://github.com/sdmx-twg/sdmx-rest/wiki/URL-length-in-REST).
422 | A web service should return this error when a request is syntactically correct but fails a semantic validation or violates agreed business rules.
500 | The web service should return this error code when none of the other error codes better describes the reason for the failure of the service to provide a meaningful response.
501 | All SDMX web services should implement all the standard interfaces, even if their only function is to return this error message. This eases interoperability between SDMX compliant web services and it also eases the development of generic SDMX web services clients. If the web service has not yet implemented one of the methods defined in the API, then it should return this error.
503 | If a web service is temporarily unavailable because of maintenance or for some other similar reasons, then it should return this error code.
