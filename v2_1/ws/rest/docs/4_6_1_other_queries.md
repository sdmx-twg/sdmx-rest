## Other Queries

### Data availability

The `availability` resource defines what data are available for a structure (data structure, dataflow or provision agreement), based on a data query. It returns a `Constraint`, i.e. structural metadata, and is therefore similar to the other structural metadata queries but the query itself is more akin to a data query. 

#### Parameters

##### Path parameters

The following path parameters are supported:

Parameter | Type | Description
--- | --- | ---
context | One of the following: `datastructure`, `dataflow`, `provisionagreement` | Data can be reported against a data structure, a dataflow or a provision agreement. This parameter allows selecting the desired context for data retrieval.
agencyID | A string compliant with the SDMX *common:NCNameIDType* | The agency maintaining the artefact for which data have been reported. 
resourceID | A string compliant with the SDMX *common:IDType* | The id of the artefact for which data have been reported.
version | A string compliant with the *VersionType* defined in the SDMX Open API specification | The version of the artefact for which data have been reported.
key | A string compliant with the *KeyType* defined in the SDMX Open API specification. | The combination of dimension values identifying the slice of the cube for which data should be returned. Wildcarding is supported via the `*` operator. For example, if the following key identifies the bilateral exchange rates for the daily US dollar exchange rate against the euro, D.USD.EUR.SP00.A, then the following key can be used to retrieve the data for all currencies against the euro: D.*.EUR.SP00.A.
componentId | A string compliant with the SDMX common: IDType | The id of the Dimension for which to obtain availability information about.  In the case where this information is not provided, data availability will be provided for all Dimensions.

The parameters mentioned above are specified using the following syntax:

    protocol://ws-entry-point/resource/context/agencyID/resourceID/version/key/componentId

The following rules apply:

- The `version`, `key` and `componentId` parameters offer the option to retrieve all existing values, using the `*` operator. This is the default value for these parameters, unless specified otherwise.
- Default values do not need to be supplied if they are the last element in the path.
- Two additional operators are supported for the version parameter: the `+`, to indicate the latest stable version of an artefact, and `~`, to indicate the latest version of an artefact regardless of its status (draft vs. stable). In addition, version supports multiple values, using comma (`,`) as separator. 

##### Query parameters

The following query parameters are supported:

Parameter | Type | Description | Default
--- | --- | --- | ---
c | Map | Filter data by component value. For example, if a structure defines a frequency dimension (FREQ) and the code A (Annual) is an allowed value for that dimension, the following can be used to retrieve annual data: `c[FREQ]=A`. The same applies to attributes (e.g. `c[CONF_STATUS]=F`) and measures. Multiple values are supported, using a comma (`,`) as separator: `C[FREQ]=A,M`. In case of attributes that support multiple values, the plus (`+`) can be used to list all values that an attribute must have. For example, to indicate that ATTR1 must either be A or B AND M, use the following: `C[ATTR1]=A,B+M`. Operators may be used too (see table with operators below). This parameter can be used in addition, or instead of, the `key` path parameter. | 
updatedAfter | xs:dateTime | The last time the query was performed by the client in the database. If this attribute is used, the returned message should only include the latest version of what has changed in the database since that point in time (updates and revisions). This should include observations that have been added since the last time the query was performed (INSERT), observations that have been revised since the last time the query was performed (UPDATE) and observations that have been deleted since the last time the query was performed (DELETE). If no offset is specified, default to local time of the web service. If the information about when the data has been updated is not available at the observation level, the web service should return either the series that have changed (if the information is attached at the series level) or the dataflows that have changed (if the information is attached at the dataflow level). | 
references  |  String | This attribute instructs the web service to return (or not) the artefacts referenced by the ContentConstraint to be returned  possible values are: "codelist", "datastructure", "conceptscheme", "dataflow", "dataproviderscheme", "none", "all" | **none**.  The keyword "all" is used to indicate the inclusion of dataflow, datastructure, conceptschemes, dataproviderschemes and codelists. Note, in the case ItemSchemes are returned (i.e. Codelists, ConceptSchemes and DataProviderSchemes), only the items used by the ContentConstraint will be included (i.e. concepts used by the constrained dimensions; codes for which data are available; data providers that have provided data available according to the CubeRegion). Additionally for Codelists parent codes will be included in the response if the child codes are in the returned codelist, irrespective of whether they are referenced by the ContentConstraint. If this results in a partial list, the isPartial attribute will be set to true.
mode  | String  | This attribute instructs the web service to return a ContentConstraint which defines a Cube Region containing values which will be returned by executing the query (mode="exact") vs a Cube Region showing what values remain valid selections that could be added to the data query (mode="available"). A valid selection is one which results in one or more series existing for the selected value, based on the current data query selection state defined by the current path parameters. | **exact**

##### Operators

Operators can be used to refine the applicability of the `c` query parameter.  

Operator | Meaning | Note
-- | -- | --
eq | Equals | Default if no operator is specified and there is only one value (e.g. `c[FREQ]=M`)
ne | Not equal to |
lt | Less than |
le | Less than or equal to |
gt | Greater than |
ge | Greater than or equal to |
co | Contains |
nc | Does not contain |
sw | Starts with |
ew | Ends with |
nd | And |
or | Or | Default if no operator is specified and there are multiple values (e.g. `c[FREQ]=M,A`)

Operators appear immediately after the `=` and are separated from the component value(s) by a `:` (e.g. `c[TIME_PERIOD]=ge:2020-01`).

### Examples

- To retrieve the distinct values for each Dimension for the entire ECB_EXR1_WEB dataflow. The metadata used to decode the code ids and concept ids into human readable labels is also requested.   

        https://ws-entry-point/availability/dataflow/ECB/ECB_EXR1_WEB/?references=all
		
- For the data query M.*.EUR.SP00.A supplied for the ECB_EXR1_WEB dataflow, the client requests information about which dimension values remain valid for inclusion into the query:

        https://ws-entry-point/availability/dataflow/ECB/ECB_EXR1_WEB/1.0.0/M.*.EUR.SP00.A?mode=available

- For the data query M.*.EUR.SP00.A supplied for the ECB_EXR1_WEB dataflow, the client requests information about which dimension values will be returned as a result of executing the query:

        https://ws-entry-point/availability/dataflow/ECB/ECB_EXR1_WEB/*/M.*.EUR.SP00.A?mode=exact 

* As `exact` is the default value for mode, the query above can also be written as follows:

        https://ws-entry-point/availability/dataflow/ECB/ECB_EXR1_WEB/*/M.*.EUR.SP00.A
