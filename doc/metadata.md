# Metadata queries

## By metadataset

### Overview

These queries enable clients to find metadatasets by the identification of the metadataset, enabling clients to checkout specific reports.

### Syntax

    protocol://ws-entry-point/metadata/metadataset/{providerID}/{metadatasetID}/{version}?{detail}

Parameter | Type | Description | Default | Multiple values
--- | --- | --- | --- | ---
providerID | A string compliant with the SDMX _common:NestedNCNameIDType_ for MetadataProvider | The id of the data provider who maintains the metadata set.  It is possible to set more than one id, using `,` as separator (e.g. UK1,UK2) | * | Yes
metadatasetID | A string compliant with the SDMX _common:NestedNCNameIDType_ for MetadataSet | The Id of the metadata set to be returned.  It is possible to set more than one id, using `,` as separator (e.g. MS1,MS2) | * | Yes
version | A string compliant with the [SDMX *semantic versioning* rules](querying_versions.md) | The version of the artefact to be returned. It is possible to set more than one version, using `,` as separator (e.g. 1.0.0,2.1+.7). | ~ | Yes
detail | *String* | This attribute specifies the desired amount of information to be returned. For example, it is possible to instruct the web service to return only basic information about the metadataset  (i.e.: id, dataprovider id, version and name). Most notably, metadata attributes of a metadataset  will not be returned Possible values are: (1) `full`: all available information for all returned metadatasets should be returned. (2) `allstubs`: all returned metadatasets should be returned as stubs, i.e. only containing identification information and the metadataset' name. | 
**full** | No
asOf | xs:dateTime | Retrieve the metadata set as it was at the specified point in time (aka time travel). | | No

Notes:

- Default values do not need to be supplied if they are the last element in the path.

### Examples

- To retrieve version 1.0.0 of the Metadataset with id QUALITY_REPORT maintained by the ECB

        https://ws-entry-point/metadata/metadataset/ECB/QUALITY_REPORT/1.0.0

- To retrieve all the Metadatasets maintained by the ECB, without the reported metadata attributes

        https://ws-entry-point/metadata/metadataset/ECB/?detail=allstubs

## By metadataflow

### Overview

These queries enable clients to find metadatasets by the collection (metadataflow), optionally filtered by the metadata provider.

### Syntax

    protocol://ws-entry-point/metadata/metadataflow/{flowAgencyID}/{flowID}/{flowVersion}/{providerRef}?{detail}

Parameter | Type | Description | Default | Multiple values
--- | --- | --- | --- | ---
flowAgencyID | A string compliant with the SDMX *common:NCNameIDType* | The agency maintaining the metadataflow for which metadata have been reported. | * | Yes
flowID | A string compliant with the SDMX *common:IDType* | The id of the metadataflow for which metadata have been reported. | * | Yes
flowVersion |  A string compliant with the *VersionType* defined in the SDMX Open API specification | The version of the metadataflow for which data have been reported. | ~ | Yes
providerRef | A string identifying the metadata provider. The syntax is agency id, provider id, separated by a `.`, i.e. `AGENCY_ID.PROVIDER_ID`. In case the string only contains one out of these 2 elements, it is considered to be the provider id, i.e. `*.PROVIDER_ID`. | The provider of the data (or metadata) to be retrieved. If not supplied, the returned message will contain data (or metadata) provided by any provider. Its a common use case in SDMX-based web services that the provider id is sufficient to uniquely identify a data provider. Should this not be the case, the agency can be used, in conjunction with the provider id, in order to uniquely identify a data provider. It is possible to set more than one provider, using `,` as separator. For example, the following value can be used to indicate that the metadataset should be provided by the Swiss National Bank (CH2) or Central Bank of Norway (NO2): `CH2,NO2`. | * | Yes
detail | *String* | This attribute specifies the desired amount of information to be returned. For example, it is possible to instruct the web service to return only basic information about the metadataset  (i.e.: id, dataprovider id, version and name). Most notably, metadata attributes of a metadataset  will not be returned Possible values are: (1) `full`: all available information for all returned metadatasets should be returned. (2) `allstubs`: all returned metadatasets should be returned as stubs, i.e. only containing identification information and the metadataset' name. | **full** | No
asOf | xs:dateTime | Retrieve the metadata sets as they were at the specified point in time (aka time travel). | | No

Notes:

- Default values do not need to be supplied if they are the last element in the path.

### Examples

- To retrieve all Metadatasets reported by the central bank of France (FR2) against the ECB METHODOLOGY metadataflow (any version)

        https://ws-entry-point/metadata/metadataflow/ECB/METHODOLOGY/*/FR2

- To retrieve Metadatasets from all metadata providers, for the latest stable version of the METHODOLOGY metadataflow, without the reported metadata attributes

        https://ws-entry-point/metadata/metadataflow/*/METHODOLOGY/+/?detail=allstubs

## By structure

### Overview

These queries enable clients to request all metadata sets which are reported against one or more structures, as such the syntax for defining which structures to find metadata for follows the same syntax as the [structure](structures.md) resource.

### Syntax

    protocol://ws-entry-point/metadata/structure/{artefactType}/{agencyID}/{resourceID}/{version}?{detail}

Parameter | Type | Description | Default | Multiple values?
--- | --- | --- | --- | ---
artefactType | One of the following types: datastructure, metadatastructure, categoryscheme, conceptscheme, codelist, hierarchy, hierarchyassociation, valuelist, organisationscheme, agencyscheme, dataproviderscheme, dataconsumerscheme, organisationunitscheme, dataflow, metadataflow, reportingtaxonomy, provisionagreement, structuremap, representationmap, conceptschememap,categoryschememap, organisationschememap, process, categorisation, dataconstraint, metadataconstraint, transformationscheme, rulesetscheme, userdefinedoperatorscheme, customtypescheme, namepersonalisationscheme, vtlmappingscheme | The type of structural metadata to be returned. | * | No
agencyID | A string compliant with the SDMX *common:NCNameIDType* | The agency maintaining the artefact to be returned. It is possible to set more than one agency, using `,` as separator (e.g. BIS,ECB). | * | Yes
resourceID | A string compliant with the SDMX *common:IDType* | The id of the artefact to be returned. It is possible to set more than one id, using `,` as separator (e.g. CL_FREQ,CL_CONF_STATUS). | * | Yes
version | A string compliant with the SDMX *semantic versioning* rules | The version of the artefact to be returned. It is possible to set more than one version, using `,` as separator (e.g. 1.0.0,2.1.7). | ~ | Yes
detail | *String* | This attribute specifies the desired amount of information to be returned. For example, it is possible to instruct the web service to return only basic information about the metadataset  (i.e.: id, dataprovider id, version and name). Most notably, metadata attributes of a metadataset will not be returned. Possible values are: (1) `full`: all available information for all returned metadata sets should be returned. (2) `allstubs`: all returned metadatasets should be returned as stubs, i.e. only containing identification information and the metadataset' name. | **full** | No
asOf | xs:dateTime | Retrieve the metadata sets as they were at the specified point in time (aka time travel). | | No

### Examples

- To retrieve metadatasets against version 1.0.0 of the DSD with id ECB_EXR1 maintained by the ECB, as well as the code lists and the concepts used in the DSD:

        https://ws-entry-point/metadata/datastructure/ECB/ECB_EXR1/1.0.0?references=children&detail=referencepartial

- To retrieve metadatasets reported against the latest version of the DSD with id ECB_EXR1 maintained by the ECB, without the code lists and concepts of the DSD:

        https://ws-entry-point/metadata/datastructure/ECB/ECB_EXR1

- To retrieve metadatasets  against any DSD maintained by the ECB (latest version), as well as the dataflows using these DSDs:

        https://ws-entry-point/metadata/datastructure/ECB?references=dataflow

- To retrieve metadatasets (as stubs) against any Dataflow:

        https://ws-entry-point/metadata/dataflow/?detail=allStubs

## Response types

The following media types can be used with _reference metadata_ queries:

- **application/vnd.sdmx.metadata+json;version=2.0.0**
- application/vnd.sdmx.metadata+xml;version=3.0.0
- application/vnd.sdmx.metadata+csv;version=2.0.0

The default format is highlighted in **bold**. For media types of previous SDMX versions, please consult the documentation of the SDMX version you are interested in.
