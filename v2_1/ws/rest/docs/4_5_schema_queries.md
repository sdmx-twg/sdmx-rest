## Schema queries


### Resources

The following resource is defined:

- schema

This resource allows a client to ask a service to return an XML schema, which defines data (or reference metadata) validity within a certain context. The service must take into account the constraints that apply within that context (DSD or MSD, dataflow or metadataflow, or provision agreement).


### Parameters

#### Parameters used for identifying a resource

The following parameters are used for identifying resources:

Parameter | Type | Description
--- | --- | ---
context | One of the following: `datastructure`, `metadatastructure`, `dataflow`, `metadataflow`, `provisionagreement` | The value of this parameter determines the constraints that need to be taken into account, when generating the schema. If datastructure or metadatastructure is used, constraints attached to the DSD or MSD must be applied when generating the schema. If dataflow or metadataflow is used, constraints attached to the dataflow or metadataflow and to the DSD or MSD used in the dataflow or metadataflow must be applied when generating the schema. If provisionagreement is used, constraints attached to the provision agreement, as well as to the dataflow or metadafalow used in the agreement and the DSD or MSD used in the dataflow or metadataflow must be applied when generating the schema.
agencyID | A string compliant with the *SDMX common:NCNameIDType* | The agency maintaining the artefact used to generate the schema to be returned.
resourceID | A string compliant with the SDMX *common:IDType* | The id of the artefact used to generate the schema to be returned.
version | A string compliant with the SDMX *common:VersionType* | The version of the artefact used to generate the schema to be returned.

The parameters mentioned above are specified using the following syntax:

    protocol:// ws-entry-point/schema/context/agencyID/resourceID/version
    
Furthermore, a keyword may be used:

Keyword | Scope | Description 
--- | --- | ---
latest | version | Returns the latest version in production of the resource

As the query for schema must match one artefact only, the keyword `all` is not supported for agencyId and resourceId.

The following rules apply:

  - If no version attribute is specified, the version currently used in production should be returned. It is therefore equivalent to using the keyword `latest`.

#### Parameters used to further describe the desired results

The following parameters are used to further describe the desired results, once the resource has been identified:

Parameter | Type | Description
--- | --- | ---
dimensionAtObservation | A string compliant with the SDMX *common:NCNameIDType* | The ID of the dimension to be attached at the observation level.
explicitMeasure | *Boolean* | For cross-sectional data validation, indicates whether observations are strongly typed (defaults to `false`).

### Examples

* To retrieve the schema for data supplied within the context of version 1.0 of the provision agreement EXR_WEB maintained by the ECB:

        http://ws-entry-point/schema/provisionagreement/ECB/EXR_WEB/1.0/

    In this case, the schema returned by the service must take into account the constraints attached to the provision agreement, the dataflow used in the provisionagreement and the data structure definition used in the dataflow.
