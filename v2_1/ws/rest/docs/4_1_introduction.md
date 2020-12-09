# SDMX RESTful API

This SDMX API is based on the REST principles

- In REST, specific information is known as **Resource**. In SDMX, specific resources would be maintainable artefacts, for example, code lists, concept schemes, data structure definitions, dataflows, etc. Each resource is addressable via a **global identifier** (i.e.: a URI).
-  Manipulating resources is done using **methods defined in the HTTP protocol** (e.g.: GET, POST, PUT, DELETE).
-  A resource can be represented in various formats (such as the different flavours and versions of the SDMX-ML standard). Selection of the appropriate **representation** is done using HTTP Content Negotiation and the HTTP `Accept` request header.

Furthemore, the API supports several operators, which will be described in more details in the various sections. These are:

| Character | Meaning |
| --- | --- |
|	`*` | All |
| `+` | Latest stable version |
| `~` | Latest version (whether stable, draft or unversioned) |
| `,` | Or | 
| `.` | Subpart separator |
| `+` | Multivalues attribute |

As can be seen, the `+` is used twice, but in different contexts, thereby removing any ambiguity.
