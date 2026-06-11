# Schema queries

## Overview

Schema queries allow retrieving **the definition of data validity for a certain context**. The service must take into account the constraints that apply within that context (e.g. dataflow).

This is typically used for **validation and/or communication purposes**, for example as a way to inform providers about the data they are expected to report.

## Syntax

    protocol://ws-entry-point/schema/{context}/{agencyID}/{resourceID}/{version}?{dimensionAtObservation}&{deletion}&{asOf}

Parameter | Type | Description | Default
--- | --- | --- | ---
**context** | One of `datastructure`, `metadatastructure`, `dataflow`, `metadataflow`, `provisionagreement`, `metadataprovisionagreement` | This parameter determines the constraints that need to be taken into account. Multiple constraints may need to be considered, depending on the selected value. For example, if dataflow is used, constraints attached to the dataflow and the DSD used by the dataflow must be considered when generating the schema. |
**agencyID** | A string compliant with the *SDMX common:NCNameIDType* | The agency maintaining the artefact that defines the data validity. |
**resourceID** | A string compliant with the SDMX *common:IDType* | The id of the artefact defining the data validity. |
version | A string compliant with the [SDMX *semantic versioning* rules](querying_versions.md) | The version of the artefact defining the data validity. | `~`
dimensionAtObservation | A string compliant with the SDMX *common:NCNameIDType* | The ID of the dimension to be attached at the observation level. |
deletion | Boolean | Whether the generated schema will be used to validate deletion messages. | `false`
asOf | xs:dateTime | Retrieve the schema as it was at the specified point in time (aka time travel). | | No

Note: Mandatory parameters are highlighted in **bold**.

## Response types

The following media types can be used with *schema* queries:

- **application/vnd.sdmx.schema+json;version=2.1.0**
- application/vnd.sdmx.schema+xml;version=3.1.0
- application/vnd.sdmx.structure+xml;version=3.1.0
- application/vnd.sdmx.structure+json;version=2.1.0
- application/vnd.sdmx.schema+json;version=2.0.0
- application/vnd.sdmx.schema+xml;version=3.0.0
- application/vnd.sdmx.structure+xml;version=3.0.0
- application/vnd.sdmx.structure+json;version=2.0.0

The default format is highlighted in **bold**. For media types of previous SDMX versions, please consult the documentation of the SDMX version you are interested in.

The *schema* formats are meant to be used for **validation** purposes (i.e. to validate SDMX-ML and SDMX-JSON data files).

The *structure* formats are meant to be used for **communication** purposes. In that case, the response must only include the following types of artefact: (meta)data structures, codelists, concept schemes, agency schemes and, optionally, constraints. The various item schemes must only contain the relevant items: Codelists must only contain the codes that are allowed after applying the constraints up to the specified *context*; Concept schemes must only contain the concepts that are used by the data structure; Agency schemes must only contain agencies maintaining artefacts that are part of the response. Constraints may be added to the response in case multiple components reference the same codelist. For example, let's consider a data structure where two components, reference area and counterparty country, reference the same codelist (containing 10 codes). For reference area, only code A is allowed, while, for counterparty country, only codes B & C are allowed. The codelist returned in the response must only include the 3 allowed codes (A, B and C), as mentioned above. In addition, constraints should also be returned in the response, to indicate which codes are allowed as reference area (A) and which codes are allowed as counterparty country (B and C).

## Examples

- Retrieve the data validity (in SDMX-JSON) for the **latest stable version** of the `EXR_WEB` dataflow maintained by the `ECB`. The constraints attached to the dataflow and the data structure used by the dataflow must be considered. The returned codelists may only contain the codes that are allowed after applying the constraints. The returned concept schemes may only contain the concepts used by the data structure. The returned agency schemes may only contain the agencies maintaining artefacts that are part of the response. Special characters have not been percent-encoded for the sake of simplicity.

```
GET /schema/dataflow/ECB/EXR_WEB/+
Accept: application/vnd.sdmx.structure+json;version=2.0.0
```

- Retrieve an XML schema to validate data submissions for the **latest stable version** of the `EXR_WEB` dataflow maintained by the `ECB`. The data are expected to be packaged as time series (i.e. All dimensions are available at the series-level, except for the `TIME_PERIOD` dimension, which is found at the observation-level). The constraints attached to the dataflow and the data structure used by the dataflow must be considered.

```
GET /schema/dataflow/ECB/EXR_WEB/+/?dimensionAtObservation=TIME_PERIOD
Accept: application/vnd.sdmx.schema+xml;version=3.0.0
```
