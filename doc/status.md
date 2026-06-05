# Error handling and status information

RESTful web services rely on HTTP status codes to indicate the outcome of requests, including errors. While an SDMX RESTful web service can return any [HTTP-defined status code](https://en.wikipedia.org/wiki/List_of_HTTP_status_codes), the most commonly used codes are described below.

- **Codes below 400**: Indicate successful or normal operations (non-problematic).
- **Codes between 400 and 499**: Indicate client errors.
- **Codes 500 and above**: Indicate server errors.

HTTP status code | Description
---|---
200 | A successful HTTP request. For GET requests, it means the request was successfully processed, and the response body contains the requested data.
204 | The query result is empty, but this lack of results is acceptable.
304 | No changes since the version specified by the request headers (If-Modified-Since or If-None-Match). The response does not include data, as previously downloaded data is still valid.
400 | The query does not comply with the SDMX-REST API.
401 | Authentication is required but has failed or has not been provided.
403 | Authentication was successful, but the user is not authorized to access the requested resource.
404 | The requested resource is not available.
406 | The client requested a resource representation that the web service does not offer.
413 | The requested response is too large for the server to process. If the service supports downloading large queries asynchronously, it may provide the future location of the file in the error message.
422 | The request is syntactically correct but fails semantic validation or violates agreed business rules.
500 | The server cannot provide a meaningful response due to an unexpected error.
501 | The web service has not implemented a specific method defined in the API. Returning this code ensures interoperability and simplifies the development of generic SDMX web service clients.
503 | The service is temporarily unavailable, such as during maintenance.
