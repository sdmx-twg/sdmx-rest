# Data registration queries

## By Registration ID

### Overview

These queries enable clients to find a single Registration by its unique ID.  

### Syntax 
        protocol://ws-entry-point/registration/id/{registrationId}

### Example

Retrieve all Registrations for the Registration with unique ID 'abcd-efgh-ijkl'

        https://ws-entry-point/registration/provider/IMF/abcd-efgh-ijkl


## By Data Provider

### Overview

These queries enable clients to find all Registrations for the Data Provider.  The Data Provider is resolved through 
the Provision Agreement that the Registration references.  

Each path parameter can contain a wildcard filter '*' to mean 'all'.  It is therefore possible to use this query to retrieve all Registrations.


### Syntax 
        protocol://ws-entry-point/registration/provider/{providerAgencyID}/{providerID}

Parameter | Type | Description | Default | Multiple values
--- | --- | --- | --- | ---
providerAgencyID | A string compliant with the SDMX *common:NCNameIDType* | The agency which maintains the Data Provider | * | Yes
providerID | A string compliant with the SDMX *common:IDType* | The id of the Data Provider to find Registrations for . | * | Yes
updatedBefore | xs:dateTime | Optional parameter, if this parameter is used, the returned message should only include Registrations updated before this time (exclusive) | | No
updatedAfter | xs:dateTime | Optional parameter, if this parameter is used, the returned message should only include Registrations updated after this time (exclusive)  | | No

### Examples of queries

Retrieve all Registrations for the UK1 Data Provider maintained in the IMF Data Provider Scheme

        https://ws-entry-point/registration/provider/IMF/UK1
        
Retrieve all Registrations for the UK1 and UK2 Data Provider maintained in the IMF Data Provider Scheme

        https://ws-entry-point/registration/provider/IMF/UK1,UK2

Retrieve all Registrations for any Data Provider maintained in the IMF Data Provider Scheme

        https://ws-entry-point/registration/provider/IMF/*
        
## By Provision Agreement, Dataflow, or Data Structure Definition

### Overview

These queries enable clients to find all Registrations related to one or more Provision Agreements, Dataflows or Data Structure Definitions.  The 
context controls which level the filter is applied to (datastructure, dataflow, provisionagreement) with the subsequent path parameters defining more
precisely which context structures to filter for. For example if the context is dataflow, with agencyID of ECB the response will contain all Registrations
related to ECB Dataflows.

It should be noted that the Registration only references a Provision Agreement, the Provision Agreement
is used to determin the corresponding Dataflow and Data Structure Definition.  

Each path parameter can contain a wildcard filter '*' to mean 'all'.  It is therefore possible to use this query to retrieve all Registrations.

### Syntax 
        protocol://ws-entry-point/registration/{context}/{agencyID}/{resourceID}/{version}

Parameter | Type | Description | Default | Multiple values
--- | --- | --- | --- | ---
context | One of the following: `datastructure`, `dataflow`, `provisionagreement` | Registrations relate to provision agreement, which in turn can be used to identify the related dataflow and data structure definition. This parameter allows selecting the desired context for registration retrieval. | * | Yes
agencyID | A string compliant with the SDMX *common:NCNameIDType* | The agency maintaining the context artefact. | * | Yes
resourceID | A string compliant with the SDMX *common:IDType* | The id of the context artefact. | * | Yes
version | A string compliant with the [SDMX *semantic versioning* rules](querying_versions.md) | The version of the context artefact. | * | Yes
updatedBefore | xs:dateTime | Optional parameter, if this parameter is used, the returned message should only include Registrations updated before this time (exclusive) | | No
updatedAfter | xs:dateTime | Optional parameter, if this parameter is used, the returned message should only include Registrations updated after this time (exclusive)  | | No

### Examples of queries

Retrieve all Registrations for the Balance Sheet Item (BSI) Dataflow maintained by the ECB

        https://ws-entry-point/registration/dataflow/ECB/BSI/1.0.0
        
Retrieve all Registrations for the BSI and EXT Dataflows maintained by the ECB

        https://ws-entry-point/registration/dataflow/ECB/BSI,EXR/1.0.0
 
Retrieve all Registrations for the ECOFIN DataStructure maintained by the IMF
        
        https://ws-entry-point/registration/datastructure/IMF/ECOFIN/1.0
        
Retrieve all Registrations updated after a specific point in time (percent encoded)
        
        https://ws-entry-point/registration/datastructure/*/*/*?updatedAfter=2009-05-15T14%3A15%3A00%2B01%3A00
        