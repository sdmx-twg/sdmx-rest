# SDMX REST API: Developers' documentation

## Overview

The SDMX REST API allows implementers to offer programmatic access to statistical data and metadata over HTTP.

It offers a variety of features such as:

- To retrieve structural metadata, using a combination of id, agencyID and version number.
- To retrieve statistical data or reference metadata using keys (with options for wildcarding and support for the OR operator, as well as operators on Component values), data or metadata flows and data or metadata providers.
- To further refine queries for statistical data or reference metadata using time information (start period and end period).
- To retrieve what has changed since your last query (i.e. new or updated data).
- To return the results of a query in various formats.
- For structural metadata, it is possible to instruct the web service to resolve references (for instance, when querying for data structure definitions, it is possible to also retrieve the concepts and code lists used in the returned data structure definitions), as well as artefacts that use the matching artefact (for example, to retrieve the dataflows that use a matching data structure definition).
- For structural metadata, it is possible to retrieve a minimal version of the artefact, for the sake of efficiency (for example, to retrieve all code lists – names, ids, etc – without the codes).
- To build dynamic query filters using the _availability_ queries.
- To perform  maintenance operations, such as creating, updating or deleting data and metadata.

## REST principles

The API is based on REST principles:

- In REST, specific information is known as **Resource**. In SDMX, specific resources would be maintainable artefacts, for example, code lists, concept schemes, data structure definitions, dataflows, etc. Each resource is addressable via a **global identifier** (i.e.: a URI).
- Manipulating resources is done using **methods defined in the HTTP protocol** (e.g.: GET, POST, PUT, DELETE).
- A resource can be represented in various formats (such as the different flavours and versions of the SDMX-ML standard). Selection of the appropriate **representation** is done using HTTP Content Negotiation and the HTTP `Accept` request header.

## Use of operators

Furthemore, the API supports several operators, which will be described in more details in the various sections. These are:

| Character | Meaning |
| --- | --- |
| `*` | All |
| `+` | [Latest stable version](querying_versions.md#query-for-the-latest-stable-semantic-version) |
| `~` | [Latest version (whether stable, draft or unversioned)](querying_versions.md#query-for-the-latest-possibly-unstable-version) |
| `,` | Or |
| `.` | Subpart separator |
| `+` | Multivalues attribute |

As can be seen, the `+` is used twice, but in different contexts, thereby removing any ambiguity.

## Content

The documentation is organized in the following sections:

- [Data queries](data.md)
- [Structure queries](structures.md)
- [Reference metadata queries](metadata.md)
- [Schema queries](schema.md)
- [Availability queries](availability.md)
- [Query syntax for versions](querying_versions.md)
- [Content-Negotiation](conneg.md)
- [Status codes](status.md)
- [Structure maintenance](maintenance.md)
- [Tips for providers and consumers](tips.md)
- [Extending the API](extend.md)
