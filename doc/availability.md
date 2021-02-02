# Data availability queries

## Overview

Availability queries allow to see what data are available for a structure (data structure, dataflow or provision agreement), based on a data query. It returns a `Constraint`, i.e. structural metadata, and is therefore similar to the other structural metadata queries but the query itself is more akin to a data query.

## Syntax

        protocol://ws-entry-point/availability/{context}/{agencyID}/{resourceID}/{version}/{key}/{componentId}?{c}&{updatedAfter}&{references}&{mode}

Parameter | Type | Description | Default | Multiple values?
--- | --- | --- | --- | ---
context | One of the following: `datastructure`, `dataflow`, `provisionagreement` | Data can be reported against a data structure, a dataflow or a provision agreement. This parameter allows selecting the desired context for data retrieval. | * | No
agencyID | A string compliant with the SDMX *common:NCNameIDType* | The agency maintaining the artefact for which data have been reported. | * | Yes
resourceID | A string compliant with the SDMX *common:IDType* | The id of the artefact for which data have been reported. | * | Yes
version | A string compliant with the *VersionType* defined in the SDMX Open API specification | The version of the artefact for which data have been reported. | * | Yes
key | A string compliant with the *KeyType* defined in the SDMX Open API specification. | The combination of dimension values identifying the slice of the cube for which data should be returned. Wildcarding is supported via the `*` operator. For example, if the following key identifies the bilateral exchange rates for the daily US dollar exchange rate against the euro, D.USD.EUR.SP00.A, then the following key can be used to retrieve the data for all currencies against the euro: D.\*.EUR.SP00.A | * | Yes
componentId | A string compliant with the SDMX common: IDType | The id of the Dimension for which to obtain availability information about.  In the case where this information is not provided, data availability will be provided for all Dimensions. | * | No
c | Map | Filter data by component value. For example, if a structure defines a frequency dimension (FREQ) and the code A (Annual) is an allowed value for that dimension, the following can be used to retrieve annual data: `c[FREQ]=A`. The same applies to attributes (e.g. `c[CONF_STATUS]=F`) and measures. Multiple values are supported, using a comma (`,`) as separator: `c[FREQ]=A,M`. In case of attributes that support multiple values, the plus (`+`) can be used to list all values that an attribute must have. For example, to indicate that ATTR1 must either be A or (B AND M), use the following: `c[ATTR1]=A,B+M`. Operators may be used too (see table with operators below). This parameter can be used in addition, or instead of, the `key` path parameter. This parameter may be used multiple times (e.g. `c[FREQ]=A,M&c[CONF_STATUS]=F`) | | Yes
updatedAfter | xs:dateTime | The last time the query was performed by the client. If this attribute is used, the returned message should only include the dimension values for the data that have changed since that point in time (updates and revisions). This should include the dimension values for data that have been added since the last time the query was performed (INSERT), data that have been revised since the last time the query was performed (UPDATE) and data that have been deleted since the last time the query was performed (DELETE). If no offset is specified, default to local time of the web service. | | No
references  |  String | This attribute instructs the web service to return (or not) the artefacts referenced by the ContentConstraint to be returned. Possible values are: "codelist", "datastructure", "conceptscheme", "dataflow", "dataproviderscheme", "none", "all". The keyword "all" is used to indicate the inclusion of dataflow, datastructure, conceptschemes, dataproviderschemes and codelists. Note, in the case ItemSchemes are returned (i.e. Codelists, ConceptSchemes and DataProviderSchemes), only the items used by the ContentConstraint will be included (i.e. concepts used by the constrained dimensions; codes for which data are available; data providers that have provided data available according to the CubeRegion). Additionally for Codelists parent codes will be included in the response if the child codes are in the returned codelist, irrespective of whether they are referenced by the ContentConstraint. If this results in a partial list, the isPartial attribute will be set to true. | **none** | Yes
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

As already mentioned, the response from the Data Availability API is an SDMX Content Constraint containing a CubeRegion which defines the distinct Values for each Dimension of the data.  These distinct values contained in the CubeRegion are determined by the server based on the Data Query presented to this API.  The meaning of the distinct values depends on the response mode.

### Response Mode

The API response to the data query depends on the **mode** the API is called with.  `mode` is a query parameter to the API, and if not provided defaults to `exact`. Depending on the mode, the returned values represent either:

- The **distinct values** which will be contained in the resultant dataset, should the data query be run against the Data API (**mode=exact**);
- The **valid future selections** that could be passed to the Data API based on the current data query selections (**mode=available**).

To highlight the difference between the response mode, consider the following dataset.

| Reference Area| Employment Status | Sex  |
| ------------- |:-----------------:| ----:|
| UK            | EMP               | M    |
| FR            | EMP               | M    |
| FR            | UEMP              | F    |

The client calls the availability API with the query **Reference Area=FR** and **Sex=F**.  

With `mode=exact`, the response is:

|Dimension         |Values    |
| -----------------| --------:|
|Reference Area    | FR       |
|Employment Status | UEMP     |
|SEX               | F        |

Mode exact has found only one series matches this query, and returned to the client the distinct values per dimension.  In this case there is only one distinct value per dimension.

With `mode=available`, the response is:

|Dimension         |Values    |
| -----------------| --------:|
|Reference Area    | FR       |
|Employment Status | UEMP     |
|SEX               | **M**, F |

Mode available has determined that the UK is no longer a valid selection.  If the UK were chosen, no data would be returned for that Reference Area.  If the user were to include Employment Status=EMP this would result in no data being returned at all.  The difference in this mode is that the service has determined that SEX=M is a valid selection, if this was to be added to the data query it would result in more series being returned.

If the user was to add to their query **Sex=M**, the query becomes **Reference Area=FR** and **Sex=M + F** and the response would change as follows.

With `mode=exact`:

|Dimension         |Values    |
| -----------------| --------:|
|Reference Area    | FR       |
|Employment Status | EMP, UEMP|
|SEX               | M, F     |

The service has detected two series that match the query criteria, and responded with the distinct code values for each dimension for those matches.

With `mode=available`:

|Dimension         |Values      |
| -----------------| ----------:|
|Reference Area    | **UK**, FR |
|Employment Status | EMP, UEMP  |
|SEX               | M, F       |

Reference Area UK has now become available, this is because if the user were to now select Reference Area=UK they would add additional series to the query response.  In addition both Employment Status values are available.  The Employment Status dimension currently has no query selections on it, this means the dimension has no filters imposed.  The service has determined that the user can impose a filter for either EMP or UEMP and the inclusion of these filters will not result in an empty dataset being returned.

### Temporal coverage

For DSDs that have a time dimension, temporal coverage can be included in the ContentConstraint's `ReferencePeriod` element. The `ReferencePeriod` is used to indicate the earliest and latest observation dates available for the sub-cube of data based on the current data query.  

This information may not be included in the response if the service does not have access to this information.

### Metrics

The `ContentConstraint` may define up to two additional Annotations used to capture the metrics for the **number of series** and the **number of observations** which will be returned if the data query presented to this API were run against the data API.  

The `Metrics` annotations are attached to the `ContentConstraint`, and have `AnnotationType` of `sdmx_metrics` an Id of `series_count` or `obs_count` depending on the metric being reported, and the annotation title is used to report the count, which is a positive integer value.

Metrics are only included if the service has the information available to provide the count. A request for metrics may include only series counts, or no metrics at all depending on the service.

### Availability for a single dimension

If a client application is only interested in the data available for a single dimension of the dataset, for example if the client is building a code picker form for only the 'Reporting Country' dimension, then the client is able to specify that this using the `componentId` path parameter. If this is the case, the CubeRegion will only include information of the distinct values for the specified dimension.

### Referenced structures

The client is able to include referenced structures used by the constraint by using the `references` parameter, for example if a dimension is coded, and references are returned, the dimension's codelist will be included in the response. Importantly the codelist will only include codes which are part of the constraint.  

Included references are:

- `Dataflow` - referenced directly by the constraint
- `Data Structure Definition` - referenced by the dataflow
- `Codelist` - which contain only the codes identified in the constraint, with any parent codes also included even if they are not included in the constraint
- `Concept Scheme` - which contain only the concepts used by the constraint
- `Data Provider Scheme` - which contain only the data providers who have provided data for the dataflow

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

## A typical use case: Executing queries via data query forms

### Step 1: Building the data query form

A data query form enables users of the UI to build a data query against a dataflow by selecting dimensions, and choosing filters for the dimension. For example the user is able to click 'Reporting Country' and then select the codes 'United Kingdom', 'France', 'Germany'. The code labels are presented in the users chosen locale where possible.

The use case can be supported as follows:

1. Query Data Availability API with a query for all data for the dataflow, and include all references:

        http://ws-entry-point/availableconstraint/ECB_EXR1_WEB/?references=all

2. The response includes the Content Constraint and the Data Structure Definition. We can iterate the dimensions to build the Dimension picker. For each dimension, we can get the concept, as this provides the human readable label (ideally in the chosen locale, if available). The Cube Region Constraint provides the available values for the dimension. If the dimension is coded, then the codelist can be used to get the human readable label in the chosen locale. The code will additionally provide any hierarchy information. An HTML checkbox is created for each available dimension value.

### Step 2: Update the data query form based on code selection state

The use case is to disable or enable values for dimension from being selected based on the current query state. The choice to disable a value from selection is based on the answer to the question: If this value is selected will it; a) return no data: b) not add any more data?

Let's consider the following dataset:

| Reference Area| Employment Status | Sex  |
| ------------- |:-----------------:| ----:|
| UK            | EMP               | M    |
| FR            | EMP               | M    |
| FR            | UEMP              | F    |

1. The user selects `Reference Area=UK`.  Employment Status=UEMP and Sex=F are no longer valid selections. Selecting either of these two codes and then running the data query will fail to return any data.
2. The user is however able to add to their query the `Reference Area=FR`.  Re-running the availability query results in `Employment Status=UEMP` and `Sex=F` becoming valid selections again, as the inclusion of either of these values will result in data being returned.
3. If the user now adds the selection `Sex=F`, then the Reference `Area=UK` is no longer a valid selection.

The use case can be supported as follows:

1. When the user adds or removes a data query filter by checking or unchecking a checkbox, call the Data Availability API with current data query state and `mode=available`.

        http://ws-entry-point/availableconstraint/EMPLOYMENT/UK+FR..M?mode=available

2. The response will include only the values which remain valid selections. Use this information to enable or disable the dimension values.

### Bonus: Prevent the client from executing a data query which exceeds server limit

Let’s imagine a situation where the service has imposed a limit on 2000 series per query. If the service receives a data query which results in more series being returned, then the client will receive an error response. The idea is then to prevent a user inadvertently running a query from the web User Interface which exceeds the limit imposed by this service.

The use case can be supported as follows:

1. When the user adds or removes a data query filter by checking or unchecking a checkbox, call the Data Availability API with the current data query state.
2. The response will include the Cube Region constraint. If the Cube Region constraint has an Annotation with type `sdmx_metrics` and an Id of `series_count` then obtain the count via the Annotation's title. Use this count to either enable or disable the 'Run Query' button.
