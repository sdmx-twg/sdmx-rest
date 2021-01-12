

## Metadata Queries

### Resources

The following resources are defined:

Resource | Purpose
---| ---
metadata/metadataset | retrieve metadata by metadataset identification
metadata/metadataflow | retrieve metadata related to metadataflow    
metadata/structure | retrieve metadata linked to structure(s)


## metadata/metadataset
### Purpose
This resource enables clients to find metadatasets by the identification of the metadataset, enabling clients to checkout specific reports.

### Parameters

#### Parameters used for identifying a resource

The following parameters are used for identifying resources:


Parameter | Type | Description
--- | --- | ---
providerID | A string compliant with the SDMX _common:NestedNCNameIDType_ for DataProvider | The id of the data provider who maintains the Metadata Set.  It is possible to set more than one id, using `,` as separator (e.g. UK1,UK2)
metadatasetID | A string compliant with the SDMX _common:NestedNCNameIDType_ for MetadataSet | The Id of the Metadata Set to be returned.  It is possible to set more than one id, using `,` as separator (e.g. MS1,MS2) 
version | A string compliant with the SDMX *semantic versioning* rules | The version of the artefact to be returned. It is possible to set more than one version, using `,` as separator (e.g. 1.0.0,2.1.7).


The parameters mentioned above are specified using the following syntax:

    protocol://ws-entry-point/metadata/metadataset/providerID/metadatasetID/version


The following rules apply:

- All path parameters support multiple values, using comma (`,`) as separator.
- All path parameters offer the option to retrieve all existing values, using the `*` operator.
- Two additional operators are supported for the version parameter: the `+`, to indicate the latest stable version of an artefact, and `~`, to indicate the latest version of an artefact regardless of its status (draft vs. stable). 
- For all path parameters, the default value is `*`.
- Default values do not need to be supplied if they are the last element in the path.


#### Parameters used to further describe the desired results

The following parameters are used to further describe the desired results, once the resource has been identified. These parameters appear in the query string part of the URL.

Parameter | Type | Description | Default
--- | --- | --- | ---
detail | *String* | This attribute specifies the desired amount of information to be returned. For example, it is possible to instruct the web service to return only basic information about the metadataset  (i.e.: id, dataprovider id, version and name). Most notably, metadata attributes of a metadataset  will not be returned Possible values are: (1) `full`: all available information for all returned metadatasets should be returned. (2) `allstubs`: all returned metadatasets should be returned as stubs, i.e. only containing identification information and the metadataset' name. | **full**

### Examples

* To retrieve version 1.0.0 of the Metadataset with id QUALITY_REPORT maintained by the ECB

        http://ws-entry-point/metadata/metadataset/ECB/QUALITY_REPORT/1.0.0

* To retrieve all the Metadatasets maintained by the ECB, without the reported metadata attributes

        http://ws-entry-point/metadata/metadataset/ECB/?detail=allstubs

## metadata/metadataflow
### Purpose
This resource enables clients to find metadatasets by the collection (metadataflow), optionally filtered by the metadata provider

### Parameters

#### Parameters used for identifying a resource

The following parameters are used for identifying resources:

Parameter | Type | Description
--- | --- | ---
metadataflowID | A string compliant with the SDMX _common:NestedNCNameIDType_ for Metadataflow | The id of the metdataflow the Metadata Set is reported against.  It is possible to set more than one id, using `,` as separator (e.g. MDF1,MDF2)
flowAgencyID | A string compliant with the SDMX *common:NCNameIDType* | The agency maintaining the metadataflow for which metadata have been reported.
flowID | A string compliant with the SDMX *common:IDType* | The id of the metadataflow for which metadata have been reported.
flowVersion |  A string compliant with the *VersionType* defined in the SDMX Open API specification | The version of the metadataflow for which data have been reported.
providerRef | A string identifying the metadata provider. The syntax is agency id, provider id, separated by a ",". For example: AGENCY_ID,PROVIDER_ID. In case the string only contains one out of these 2 elements, it is considered to be the provider id, i.e. all,PROVIDER_ID. | The provider of the data (or metadata) to be retrieved. If not supplied, the returned message will contain data (or metadata) provided by any provider. Its a common use case in SDMX-based web services that the provider id is sufficient to uniquely identify a data provider. Should this not be the case, the agency can be used, in conjunction with the provider id, in order to uniquely identify a data provider. The OR operator is supported using the + character. For example, the following value can be used to indicate that the metadataset should be provided by the Swiss National Bank (CH2) or Central Bank of Norway (NO2): CH2+NO2.

The parameters mentioned above are specified using the following syntax:

    protocol://ws-entry-point/metadata/metadataflow/flowAgencyID/flowID/flowVersion/providerRef

The following rules apply:

- All path parameters support multiple values, using comma (`,`) as separator.
- All path parameters offer the option to retrieve all existing values, using the `*` operator.
- Two additional operators are supported for the version parameter: the `+`, to indicate the latest stable version of an artefact, and `~`, to indicate the latest version of an artefact regardless of its status (draft vs. stable). 
- For all path parameters, the default value is `*`.
- Default values do not need to be supplied if they are the last element in the path.


#### Parameters used to further describe the desired results

The following parameters are used to further describe the desired results, once the resource has been identified. These parameters appear in the query string part of the URL.

Parameter | Type | Description | Default
--- | --- | --- | ---
detail | *String* | This attribute specifies the desired amount of information to be returned. For example, it is possible to instruct the web service to return only basic information about the metadataset  (i.e.: id, dataprovider id, version and name). Most notably, metadata attributes of a metadataset  will not be returned Possible values are: (1) `full`: all available information for all returned metadatasets should be returned. (2) `allstubs`: all returned metadatasets should be returned as stubs, i.e. only containing identification information and the metadataset' name. | **full**


### Examples

* To retrieve all Metadatasets reported by the central bank of France (FR2) against the ECB METHODOLOGY metadataflow (any version)

        http://ws-entry-point/metadata/metadataflow/ECB/METHODOLOGY/*/FR2

* To retrieve Metadatasets from all metadata providers, for the latest stable version of the METHODOLOGY metadataflow, without the reported metadata attributes

        http://ws-entry-point/metadata/metadataflow/*/METHODOLOGY/+/?detail=allstubs

## metadata/structure 
### Purpose
This resource enables clients to request all metadata sets which are reported against one or more structures, as such the syntax for defining which structures to find metadata for follows the same syntax as the [structure](https://github.com/sdmx-twg/sdmx-rest/blob/develop/v2_1/ws/rest/docs/4_3_structural_queries.md) resource.   

### Syntax
The metadata/structure resource is followed by the same path, query parameters, and rules as the structure query, documented [here](https://github.com/sdmx-twg/sdmx-rest/blob/develop/v2_1/ws/rest/docs/4_3_structural_queries.md).

    protocol://ws-entry-point/metadata/structure/artefactType/agencyID/resourceID/version

#### Parameters used to further describe the desired results

The structure resource detail parameter is redefined for the metadata/structure resource as follows

Parameter | Type | Description | Default
--- | --- | --- | ---
detail | *String* | This attribute specifies the desired amount of information to be returned. For example, it is possible to instruct the web service to return only basic information about the metadataset  (i.e.: id, dataprovider id, version and name). Most notably, metadata attributes of a metadataset  will not be returned Possible values are: (1) `full`: all available information for all returned metadatasets should be returned. (2) `allstubs`: all returned metadatasets should be returned as stubs, i.e. only containing identification information and the metadataset' name. | **full**

### Examples

To retrieve metadatasets against version 1.0.0 of the DSD with id ECB_EXR1 maintained by the ECB, as well as the code lists and the concepts used in the DSD:
    
    http://ws-entry-point/metadata/structure/datastructure/ECB/ECB_EXR1/1.0.0references=children&detail=referencepartial
    
To retrieve metadatasets reported against the latest version of the DSD with id ECB_EXR1 maintained by the ECB, without the code lists and concepts of the DSD:
    
    http://ws-entry-point/metadata/structure/datastructure/ECB/ECB_EXR1
        
To retrieve metadatasets against any DSD maintained by the ECB (latest version), as well as the dataflows using these DSDs:
    
    http://ws-entry-point/metadata/structure/datastructure/ECB?references=dataflow
    
To retrieve metadatasets (as stubs) against any Dataflow 
    
    http://ws-entry-point/metadata/structure/dataflow/?detail=allStubs

