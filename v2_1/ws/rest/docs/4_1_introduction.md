# SDMX RESTful API

## A Brief Introduction to REST

This SDMX API is based on the REST principles, as described below:

- In REST, specific information is known as “**Resource**”. In SDMX, specific
resources would be, for example, code lists, concept schemes, data structure
definitions, dataflows, etc. Each resource is addressable via a **global
identifier** (i.e.: a URI).

-  Manipulating resources is done using **methods defined in the HTTP protocol**
(e.g.: GET, POST, PUT, DELETE). This API focuses on data retrieval, and,
therefore, only the usage of HTTP GET is covered in this document.

-  A resource can be represented in various formats (such as the different
flavours and versions of the SDMX-ML standard). Selection of the appropriate
**representation** is done using HTTP Content Negotiation and the HTTP Accept
request header.
