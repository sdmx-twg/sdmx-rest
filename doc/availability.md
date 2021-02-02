# Data availability queries

## Overview

Availability queries allow to see what data are available for a structure (data structure, dataflow or provision agreement), based on a data query. It returns a `Constraint`, i.e. structural metadata, and is therefore similar to the other structural metadata queries but the query itself is more akin to a data query. 

## Syntax

        protocol://ws-entry-point/availability/{context}/{agencyID}/{resourceID}/{version}/{key}/{componentId}?{c}&{updatedAfter}&{references}&{mode}

Parameter | Type | Description | Default | Multiple values?
--- | --- | --- | --- | ---
context | One of the following: `datastructure`, `dataflow`, `provisionagreement` | Data can be reported against a data structure, a dataflow or a provision agreement. This parameter allows selecting the desired context for data retrieval. | * | No
agencyID | A string compliant with the SDMX *common:NCNameIDType* | The agency maintaining the artefact for which data have been reported. | * | No
resourceID | A string compliant with the SDMX *common:IDType* | The id of the artefact for which data have been reported. | * | No
version | A string compliant with the *VersionType* defined in the SDMX Open API specification | The version of the artefact for which data have been reported. | * | No
key | A string compliant with the *KeyType* defined in the SDMX Open API specification. | The combination of dimension values identifying the slice of the cube for which data should be returned. Wildcarding is supported via the `*` operator. For example, if the following key identifies the bilateral exchange rates for the daily US dollar exchange rate against the euro, D.USD.EUR.SP00.A, then the following key can be used to retrieve the data for all currencies against the euro: D.\*.EUR.SP00.A | * | No
componentId | A string compliant with the SDMX common: IDType | The id of the Dimension for which to obtain availability information about.  In the case where this information is not provided, data availability will be provided for all Dimensions. | * | No
c | Map | Filter data by component value. For example, if a structure defines a frequency dimension (FREQ) and the code A (Annual) is an allowed value for that dimension, the following can be used to retrieve annual data: `c[FREQ]=A`. The same applies to attributes (e.g. `c[CONF_STATUS]=F`) and measures. Multiple values are supported, using a comma (`,`) as separator: `c[FREQ]=A,M`. In case of attributes that support multiple values, the plus (`+`) can be used to list all values that an attribute must have. For example, to indicate that ATTR1 must either be A or (B AND M), use the following: `c[ATTR1]=A,B+M`. Operators may be used too (see table with operators below). This parameter can be used in addition, or instead of, the `key` path parameter. This parameter may be used multiple times (e.g. `c[FREQ]=A,M&c[CONF_STATUS]=F`) | | Yes
updatedAfter | xs:dateTime | The last time the query was performed by the client. If this attribute is used, the returned message should only include the dimension values for the data that have changed since that point in time (updates and revisions). This should include the dimension values for data that have been added since the last time the query was performed (INSERT), data that have been revised since the last time the query was performed (UPDATE) and data that have been deleted since the last time the query was performed (DELETE). If no offset is specified, default to local time of the web service. | | No
references  |  String | This attribute instructs the web service to return (or not) the artefacts referenced by the ContentConstraint to be returned  possible values are: "codelist", "datastructure", "conceptscheme", "dataflow", "dataproviderscheme", "none", "all". The keyword "all" is used to indicate the inclusion of dataflow, datastructure, conceptschemes, dataproviderschemes and codelists. Note, in the case ItemSchemes are returned (i.e. Codelists, ConceptSchemes and DataProviderSchemes), only the items used by the ContentConstraint will be included (i.e. concepts used by the constrained dimensions; codes for which data are available; data providers that have provided data available according to the CubeRegion). Additionally for Codelists parent codes will be included in the response if the child codes are in the returned codelist, irrespective of whether they are referenced by the ContentConstraint. If this results in a partial list, the isPartial attribute will be set to true. | **none** | Yes
mode  | String  | This attribute instructs the web service to return a ContentConstraint which defines a Cube Region containing values which will be returned by executing the query (mode="exact") vs a Cube Region showing what values remain valid selections that could be added to the data query (mode="available"). A valid selection is one which results in one or more series existing for the selected value, based on the current data query selection state defined by the current path parameters. | **exact** | No

Notes:

- Default values do not need to be supplied if they are the last element in the path.
- Operators can be used to refine the applicability of the `c` query parameter:

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

Operators appear as prefix to the component value(s) and are separated from it by a `:` (e.g. `c[TIME_PERIOD]=ge:2020-01,le:2020-12`).

## Response types

The following media types can be used with _availability_ queries:

- **application/vnd.sdmx.structure+json;version=2.0.0**
- application/vnd.sdmx.structure+xml;version=3.0.0
- application/vnd.sdmx.structure+xml;version=2.1
- application/vnd.sdmx.structure+json;version=1.0.0

The default format is highlighted in **bold**.

## Examples

- To retrieve the distinct values for each Dimension for the entire ECB_EXR1_WEB dataflow. The metadata used to decode the code ids and concept ids into human readable labels is also requested.

        https://ws-entry-point/availability/dataflow/ECB/ECB_EXR1_WEB/?references=all

- For the data query M.*.EUR.SP00.A supplied for the ECB_EXR1_WEB dataflow, the client requests information about which dimension values remain valid for inclusion into the query:

        https://ws-entry-point/availability/dataflow/ECB/ECB_EXR1_WEB/1.0.0/M.*.EUR.SP00.A?mode=available

- For the data query M.*.EUR.SP00.A supplied for the ECB_EXR1_WEB dataflow, the client requests information about which dimension values will be returned as a result of executing the query:

        https://ws-entry-point/availability/dataflow/ECB/ECB_EXR1_WEB/*/M.*.EUR.SP00.A?mode=exact 

- As `exact` is the default value for mode, the query above can also be written as follows:

        https://ws-entry-point/availability/dataflow/ECB/ECB_EXR1_WEB/*/M.*.EUR.SP00.A
