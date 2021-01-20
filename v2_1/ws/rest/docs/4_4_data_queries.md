## Data queries

### Overview

Data queries allow **retrieving statistical data**. Entire datasets can be retrieved or individual observations, or anything in between, using filters on dimensions, including time, attributes and/or measures. All data matching a query can be retrieved or only the data that has changed since the last time the same query was performed. Using the _withHistory_ parameter, it is also possible to retrieve previous versions of the data. Last but not least, the data retrieved can be packaged in different ways (as time series, cross-sections or as a table), in a variety of formats (JSON, XML, CSV, etc.).

### Syntax

    protocol://ws-entry-point/data/{context}/{agencyID}/{resourceID}/{version}/{key}?
    {c}&{updatedAfter}&{firstNObservations}&{lastNObservations}&{dimensionAtObservation}&{detail}&{includeHistory}

Parameter | Type | Description | Default | Multiple values?
--- | --- | --- | --- | ---
context | One of the following: `datastructure`, `dataflow`, `provisionagreement` | Data can be reported against a data structure, a dataflow or a provision agreement. This parameter allows selecting the desired context for data retrieval. | * | Yes
agencyID | A string compliant with the SDMX *common:NCNameIDType* | The agency maintaining the artefact for which data have been reported. | * | Yes
resourceID | A string compliant with the SDMX *common:IDType* | The id of the artefact for which data have been reported. | * | Yes
version | A string compliant with the SDMX semantic versioning rules | The version of the artefact for which data have been reported. | * | Yes
key | A string compliant with the *KeyType* defined in the SDMX Open API specification. | The combination of dimension values identifying the slice of the cube for which data should be returned. Wildcarding is supported via the `*` operator. For example, if the following key identifies the bilateral exchange rates for the daily US dollar exchange rate against the euro, D.USD.EUR.SP00.A, then the following key can be used to retrieve the data for all currencies against the euro: D.`*`.EUR.SP00.A. | * | Yes
c | Map | Filter data by component value. For example, if a structure defines a frequency dimension (FREQ) and the code A (Annual) is an allowed value for that dimension, the following can be used to retrieve annual data: `c[FREQ]=A`. The same applies to attributes (e.g. `c[CONF_STATUS]=F`) and measures. Multiple values are supported, using a comma (`,`) as separator: `c[FREQ]=A,M`. In case of attributes that support multiple values, the plus (`+`) can be used to list all values that an attribute must have. For example, to indicate that ATTR1 must either be A or (B AND M), use the following: `c[ATTR1]=A,B+M`. Operators may be used too (see table with operators below). This parameter can be used in addition, or instead of, the `key` path parameter. This parameter may be used multiple times (e.g. `c[FREQ]=A,M&c[CONF_STATUS]=F`). | | Yes
updatedAfter | xs:dateTime | The last time the query was performed by the client in the database. If this attribute is used, the returned message should only include the latest version of what has changed in the database since that point in time (updates and revisions). This should include observations that have been added since the last time the query was performed (INSERT), observations that have been revised since the last time the query was performed (UPDATE) and observations that have been deleted since the last time the query was performed (DELETE). If no offset is specified, default to local time of the web service. If the information about when the data has been updated is not available at the observation level, the web service should return either the series that have changed (if the information is attached at the series level) or the dataflows that have changed (if the information is attached at the dataflow level). | | No
firstNObservations | Positive integer | The maximum number of observations to be returned for each of the matching series, starting from the first observation | | No
lastNObservations | Positive integer | The maximum number of observations to be returned for each of the matching series, counting back from the most recent observation ||Yes
dimensionAtObservation | A string compliant with the SDMX common:NCNameIDType | The ID of the dimension to be attached at the observation level. This parameter allows the client to indicate how the data should be packaged by the service. The options are `TIME_PERIOD` (a *timeseries* view of the data), the `ID of any other dimension` used in that dataflow (a *cross-sectional* view of the data) or the keyword `AllDimensions` (a *flat* / *table* view of the data where the observations are not grouped, neither in time series, nor in sections). In case this parameter is not set, the service is expected to default to TimeDimension, if the data structure definition has one, or else, to default to AllDimensions.|Depends on DSD|No 
detail | String | This attribute specifies the desired amount of information to be returned. For example, it is possible to instruct the web service to return data only (i.e. no attributes). Possible options are: `full` (all data and documentation, including annotations - This is the default), `dataonly` (attributes  and therefore groups will be excluded from the returned message), `serieskeysonly` (returns only the series elements and the dimensions that make up the series keys. This is useful for performance reasons, to return the series that match a certain query, without returning the actual data) and `nodata` (returns the groups and series, including attributes and annotations, without observations). |`full`| No
includeHistory | Boolean | This attribute allows retrieving previous versions of the data, as they were disseminated in the past (*history* or *timeline* functionality). When the value is set to `true`, the returned SDMX-ML data message should contain one or two datasets per data dissemination, depending on whether a dissemination also deleted observations from the data source. The `validFromDate` and/or `validToDate` attributes of the dataset should be used to indicate the periods of validity for the data contained in the data set. See below for an example on how to handle the `includeHistory` parameter. | `false` | No 

The following rules apply:

- Multiple values for a parameter must be separated using a comma (`,`).
- Two additional operators are supported for the _version_ parameter: the `+`, to indicate the latest stable version of an artefact, and `~`, to indicate the latest version of an artefact regardless of its status (draft vs. stable). 
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

Operators appear immediately after the `=` and are separated from the component value(s) by a `:` (e.g. `c[TIME_PERIOD]=ge:2020-01`).

### Response types

The following media types can be used with _data_ queries:

- **application/vnd.sdmx.data+json;version=2.0.0**
- application/vnd.sdmx.data+xml;version=3.0.0
- application/vnd.sdmx.data+csv;version=2.0.0;labels=[id|both|name];timeFormat=[original|normalized]
- application/vnd.sdmx.genericdata+xml;version=2.1
- application/vnd.sdmx.structurespecificdata+xml;version=2.1
- application/vnd.sdmx.generictimeseriesdata+xml;version=2.1
- application/vnd.sdmx.structurespecifictimeseriesdata+xml;version=2.1
- application/vnd.sdmx.data+json;version=1.0.0
- application/vnd.sdmx.data+csv;version=1.0.0;labels=[id|both];timeFormat=[original|normalized]

The default format is highlighted in **bold**.

### Examples of queries

* Retrieve the data matching the supplied path parameters:

        http://ws-entry-point/data/dataflow/ECB/EXR/1.0.0/M.USD.EUR.SP00.A

* Retrieve the data matching the supplied path parameters, including multiple versions and a wildcard for the second dimension:

        http://ws-entry-point/data/datastructure/ECB/ECB_EXR1/1.0.0,2.0.0/M.*.EUR.SP00.A

* Retrieve the data matching the supplied path parameters (including multiple keys and the latest stable version), that have been updated after the supplied timestamp:

        http://ws-entry-point/data/dataflow/ECB/EXR/+/M.USD.EUR.SP00.A,A.CHF.EUR.SP00.A?updatedAfter=2009-05-15T14%3A15%3A00%2B01%3A00

* Retrieve the _public_ data matching the supplied path parameters (all data reported for the latest version of the ECB EXR dataflow), and falling between the supplied the start and end periods:

        http://ws-entry-point/data/dataflow/ECB/EXR/?c[TIME_PERIOD]=ge:2009-05-01&c[TIME_PERIOD]=le:2009-05-31&c[CONF_STATUS]=F
        
* Retrieve the list of indicators about Switzerland (CH) available in the source:

        http://ws-entry-point/data/?c[REF_AREA]=CH&detail=serieskeysonly
        
* Retrieve the list of observations matching the supplied path parameters, that are above a certain threshold:

        http://ws-entry-point/data/dataflow/ECB/EXR/1.0.0/M.USD.EUR.SP00.A?c[OBS_VALUE]=ge:10.0&dimensionAtObservation=AllDimensions
        
* Retrieve the list of indicators containing euro in their title:

        http://ws-entry-point/data/?c[TITLE]=co:euro&detail=serieskeysonly


### How to handle the `includeHistory` parameter

For example, for a particular series, there were, so far, 3 disseminations:
* In February 2012, there was the initial dissemination, with 2 periods: 2011-12 and 2012-01.
* In March, the decision was taken to delete all observations before 2012 (so, 2011-12). In addition, a new observation has been published for 2012-02.
* In April, the value for February has been revised, and the value for March has been published.

If the value of the includeHistory is set to true, the web service should return 4 datasets:
* The first dataset contains the data disseminated in February, so 2 observations (2011-12 and 2012-01). The dataset action flag is `Replace`.
* The second dataset contains the new data disseminated in March. It will contain one observation (2012-02). The dataset action flag is also `Replace`.
* The third dataset contains the deleted data, removed with the March dissemination. It will contain one observation (2011-12). The dataset action flag is `Delete`.
* The fourth dataset contains the data disseminated in April. It will contain the revised observation (2012-02) and the new one (2012-03). The dataset action flag is `Replace`.

The `validFrom` and `validTo` flags should be used as follows:
* For datasets whose action flag is `Replace`, the `validFromDate` is used to indicate from which point in time the values are considered valid.
* For datasets whose action flag is `Delete`, the `validToDate` is used to indicate until which point in time the values were considered valid.
