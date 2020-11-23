## Schema queries


### Resources

The following resource is defined:

- schema

This resource allows a client to ask a service to return a schema, i.e. a document which defines data validity within a certain context. The service must take into account the constraints that apply within that context (DSD or MSD, dataflow or metadataflow, or provision agreement).

This is typically used for validation purposes but it may also be used for communication purposes, i.e. as a way to inform providers about the data they are expected to report.

### Parameters

#### Parameters used for identifying a resource

The following parameters are used for identifying resources:

Parameter | Type | Description
--- | --- | ---
context | One of the following: `datastructure`, `metadatastructure`, `dataflow`, `metadataflow`, `provisionagreement` | The value of this parameter determines the constraints that need to be taken into account, when generating the schema. If datastructure or metadatastructure is used, constraints attached to the DSD or MSD must be applied when generating the schema. If dataflow or metadataflow is used, constraints attached to the dataflow or metadataflow and to the DSD or MSD used in the dataflow or metadataflow must be applied when generating the schema. If provisionagreement is used, constraints attached to the provision agreement, as well as to the dataflow or metadafalow used in the agreement and the DSD or MSD used in the dataflow or metadataflow must be applied when generating the schema.
agencyID | A string compliant with the *SDMX common:NCNameIDType* | The agency maintaining the artefact used to generate the schema to be returned.
resourceID | A string compliant with the SDMX *common:IDType* | The id of the artefact used to generate the schema to be returned.
version | A string compliant with the SDMX *semantic versioning* rules| The version of the artefact used to generate the schema to be returned.

The parameters mentioned above are specified using the following syntax:

    protocol:// ws-entry-point/schema/context/agencyID/resourceID/version
    
Furthermore, a reserved operator may be used:

Keyword | Scope | Description 
--- | --- | ---
`+` | version | Returns the latest stable version of a resource

The following rules apply:

  - If no version attribute is specified, the latest stable version will be returned. It is therefore equivalent to using the `+` operator.

#### Parameters used to further describe the desired results

The following parameters are used to further describe the desired results, once the resource has been identified:

Parameter | Type | Description
--- | --- | ---
dimensionAtObservation | A string compliant with the SDMX *common:NCNameIDType* | The ID of the dimension to be attached at the observation level.
explicitMeasure | *Boolean* | For cross-sectional data validation, indicates whether observations are strongly typed (defaults to `false`).

### Using an SDMX format for the response

By default, a *schema* query will return an XML schema (i.e. an `xsd` file). However, it is also possible to get the response in one of the SDMX Structure formats. In that case, the response should only include the following types of artefact: data structures, codelists, concept schemes and agency schemes. The various item schemes must only contain the following:

- For codelists, the codes that are allowed after applying the constraints up to the specified *context*;
- For concept schemes, the concepts that are used by the data structure;
- For agency schemes, the agencies maintaining artefacts that are part of the response.

### Examples

* To retrieve the schema for data supplied within the context of version 1.0 of the provision agreement EXR_WEB maintained by the ECB:

        http://ws-entry-point/schema/provisionagreement/ECB/EXR_WEB/1.0.0/

    In this case, the schema returned by the service must take into account the constraints attached to the provision agreement, the dataflow used in the provisionagreement and the data structure definition used in the dataflow.
