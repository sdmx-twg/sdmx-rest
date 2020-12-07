## Scope of the API

The SDMX RESTful API offers a variety of features such as:

- To retrieve structural metadata, using a combination of id, agencyID and version number.
- To retrieve statistical data or reference metadata using keys (with options for wildcarding and support for the OR operator), data or metadata flows and data or metadata providers.
- To further refine queries for statistical data or reference metadata using time information (start period and end period).
- To retrieve what has changed since your last query (i.e. new or updated data).
- To return the results of a query in various formats. The desired format and version of the returned message will be specified using HTTP Content Negotiation (and the HTTP `Accept` request header).
- For structural metadata, it is possible to instruct the web service to resolve references (for instance, when querying for data structure definitions, it is possible to also retrieve the concepts and code lists used in the returned data structure definitions), as well as artefacts that use the matching artefact (for example, to retrieve the dataflows that use a matching data structure definition).
- For structural metadata, it is possible to retrieve a minimal version of the artefact, for the sake of efficiency (for example, to retrieve all code lists – names, ids, etc – without the codes).
- To build dynamic query filters using the _availability_ queries.
- To perform  maintenance operations, such as creating, updating or deleting data and metadata.
