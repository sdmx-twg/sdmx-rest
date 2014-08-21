## Scope of the API

The RESTful API focuses on simplicity. The aim is not to replicate the full
semantic richness of the SDMX-ML Query message but to make it simple to perform
a limited set of standard queries. Also, in contrast to other parts of the SDMX
specification, the RESTful API focuses solely on data retrieval (via HTTP GET).
More specifically, the API allows:

-  To retrieve structural metadata, using a combination of id, agencyID and
version number.

-  To retrieve statistical data or reference metadata using keys (with options
for wildcarding and support for the OR operator), data or metadata flows and
data or metadata providers.

-  To further refine queries for statistical data or reference metadata using
time information (start period and end period).

- To retrieve updates and revisions only.

- To return the results of a query in various formats. The desired format and
version of the returned message will be specified using HTTP Content Negotiation
(and the HTTP Accept request header).

- For structural metadata, it is possible to instruct the web service to
resolve references (for instance, when querying for data structure definitions,
it is possible to also retrieve the concepts and code lists used in the returned
data structure definitions), as well as artefacts that use the matching artefact
(for example, to retrieve the dataflows that use a matching data structure
definition).

- For structural metadata, it is possible to retrieve a minimal version of the
artefact, for the sake of efficiency (for example, to retrieve all code lists –
names, ids, etc – without the codes).

- A distinction should be established between the elements that allow
identifying the resource to be retrieved and the elements that give additional
information about, or allow to further filter, the desired results. Elements
belonging to the 1st category are specified in the path part of the URL while
elements belonging to the 2nd category are specified in the query string part of
the URL.
