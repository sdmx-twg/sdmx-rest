## Schema queries

### Overview

Schema queries allow retrieving **data validity within a certain context**. The service must take into account the constraints that apply within that context (e.g. dataflow).

This is typically used for **validation and/or communication purposes**, for example as a way to inform providers about the data they are expected to report.

### Syntax

    protocol://ws-entry-point/schema/{context}/{agencyID}/{resourceID}/{version}?{dimensionAtObservation}&{explicitMeasure}

Parameter | Type | Description | Default
--- | --- | --- | ---
**context** | One of `datastructure`, `metadatastructure`, `dataflow`, `metadataflow`, `provisionagreement` | The value of this parameter determines the constraints that need to be taken into account, when generating the schema. Multiple constraints may need to be considered, depending on the selected value. For example, if dataflow is used, constraints attached to the dataflow and to the DSD used for the dataflow must be considered when generating the schema. |
**agencyID** | A string compliant with the *SDMX common:NCNameIDType* | The agency maintaining the artefact that defines the data validity. |
**resourceID** | A string compliant with the SDMX *common:IDType* | The id of the artefact defining the data validity. |
version | A string compliant with the SDMX *semantic versioning* rules| The version of the artefact defining the data validity. | `~`
dimensionAtObservation | A string compliant with the SDMX *common:NCNameIDType* | The ID of the dimension to be attached at the observation level. |
explicitMeasure | *Boolean* | For cross-sectional data validation, indicates whether observations are strongly typed. | `false`

Note: Mandatory parameters are highlighted in **bold**.

### Response types

The following media types can be used with _schema_ queries:

- **application/vnd.sdmx.schema+json;version=3.0.0**
- application/vnd.sdmx.schema+xml;version=3.0.0
- application/vnd.sdmx.structure+xml;version=3.0.0
- application/vnd.sdmx.structure+json;version=2.0.0
- application/vnd.sdmx.schema+xml;version=2.1
- application/vnd.sdmx.structure+xml;version=2.1
- application/vnd.sdmx.structure+json;version=1.0.0

The default format is highlighted in **bold**.

The _schema_ formats are meant to be used for **validation** purposes (i.e. to validate SDMX-ML and SDMX-JSON data files). The _structure_ formats are meant to be used for **communication** purposes. In that case, the response should only include the following types of artefact: (meta)data structures, codelists, concept schemes and agency schemes. The various item schemes must only contain the following:

- For codelists, the codes that are allowed after applying the constraints up to the specified *context*;
- For concept schemes, the concepts that are used by the data structure;
- For agency schemes, the agencies maintaining artefacts that are part of the response.

### Examples

* To retrieve the schema for data supplied within the context of version 1.0.0 of the provision agreement EXR_WEB maintained by the ECB:

        http://ws-entry-point/schema/provisionagreement/ECB/EXR_WEB/1.0.0/

    In this case, the schema returned by the service must take into account the constraints attached to the provision agreement, the dataflow used in the provisionagreement and the data structure definition used in the dataflow.
