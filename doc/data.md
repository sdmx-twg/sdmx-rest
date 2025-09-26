# Data queries

## Overview

Data queries allow **retrieving statistical data**. Entire datasets, individual observations, or anything in between, can be retrieved using filters on dimensions (including time), attributes and/or measures. All data matching a query can be retrieved or only the data that has changed since the last time the same query was performed. Using the _includeHistory_ parameter, it is also possible to retrieve previous versions of the data. Last but not least, the data retrieved can be packaged in different ways (as time series, cross-sections or as a table), in a variety of formats (JSON, XML, CSV, etc.).

## Syntax

    protocol://ws-entry-point/data/{context}/{agencyID}/{resourceID}/{version}/{key}?
    {c}&{updatedAfter}&{firstNObservations}&{lastNObservations}&{dimensionAtObservation}
    &{attributes}&{measures}&{includeHistory}&{offset}&{limit}&{sort}&{asOf}&{reportingYearStartDay}

Parameter | Type | Description | Default | Multiple values?
--- | --- | --- | --- | ---
context | One of the following: `datastructure`, `dataflow`, `provisionagreement` | Data can be reported against a data structure, a dataflow or a provision agreement. This parameter allows selecting the desired context for data retrieval. If possible, services must **respect the requested context type** when returning data. For example, if the client sets the context to `provisionagreement`, the response should contain one dataset per matching provision agreement, i.e. the service should not group the matching data into one dataset, even if all provision agreements relate to the same dataflow (or the same data structure). | * | No
agencyID | A string compliant with the SDMX _common:NCNameIDType_ | The agency maintaining the artefact for which data have been reported. | * | Yes
resourceID | A string compliant with the SDMX _common:IDType_ | The id of the artefact for which data have been reported. | * | Yes
version | A string compliant with the [SDMX _semantic versioning_ rules](querying_versions.md) | The version of the artefact for which data have been reported. | * | Yes
key | A string compliant with the _KeyType_ defined in the SDMX Open API specification. | The combination of dimension values identifying the slice of the cube for which data should be returned. Wildcarding is supported via the `*` operator. For example, if the following key identifies the bilateral exchange rates for the daily US dollar exchange rate against the euro, D.USD.EUR.SP00.A, then the following key can be used to retrieve the data for all currencies against the euro: D.`*`.EUR.SP00.A. Any dimension value omitted at the end of the Key is assumed as equivalent to a wildcard, e.g. D.USD is equivalent to D.USD.`*`.`*`.`*`  | * | Yes
c | Map | Filter data by component value. For example, if a structure defines a frequency dimension (FREQ) and the code A (Annual) is an allowed value for that dimension, the following can be used to retrieve annual data: `c[FREQ]=A`. The same applies to attributes (e.g. `c[CONF_STATUS]=F`) and measures. Multiple values are supported, using a comma (`,`) as separator: `c[FREQ]=A,M`. The comma effectively acts as an `OR` statement (i.e. FREQ is A OR FREQ is M). The plus (`+`) can be used whenever an `AND` statement is required, such as for example, for attributes with multiple values or for time ranges. For example, to indicate that ATTR1 must either be A or (B AND M), use the following: `c[ATTR1]=A,B+M`. Operators may be used too (see table with operators below). This parameter can be used in addition, or instead of, the `key` path parameter. This parameter may be used multiple times (e.g. `c[FREQ]=A,M&c[CONF_STATUS]=F`), but only once per Component. | | Yes
updatedAfter | xs:dateTime | The last time the query was performed by the client in the database. If this parameter is used, the returned message should only include the latest version of what has changed in the database since that point in time (updates and revisions). This should include observations that have been added since the last time the query was performed (INSERT), observations that have been revised since the last time the query was performed (UPDATE) and observations that have been deleted since the last time the query was performed (DELETE). If no offset is specified, default to local time of the web service. If the information about when the data has been updated is not available at the observation level, the web service should return either the series that have changed (if the information is attached at the series level) or the dataflows that have changed (if the information is attached at the dataflow level). | | No
firstNObservations | Positive integer | The maximum number of observations to be returned for each of the matching series, starting from the first observation | | No
lastNObservations | Positive integer | The maximum number of observations to be returned for each of the matching series, counting back from the most recent observation ||No
dimensionAtObservation | A string compliant with the SDMX common:NCNameIDType | The ID of the dimension to be attached at the observation level. This parameter allows the client to indicate how the data should be packaged by the service. The options are `TIME_PERIOD` (a _timeseries_ view of the data), the `ID of any other dimension` used in that dataflow (a _cross-sectional_ view of the data) or the keyword `AllDimensions` (a _flat_ / _table_ view of the data where the observations are not grouped, neither in time series, nor in sections). In case this parameter is not set, the service is expected to default to `TIME_PERIOD`, if the data structure definition has one, or else, to default to `AllDimensions`.|Depends on DSD|No
attributes | String | This parameter specifies the attributes to be returned. Possible options are: `dsd` (all the attributes defined in the data structure definition), `msd` (all the reference metadata attributes), `dataset` (all the attributes attached to the dataset-level), `series` (all the attributes attached to the series- and group-level), `obs` (all the attributes attached to the observation-level), `all` (all attributes), `none` (no attributes), `{attribute_id}`: The ID of one or more attributes the caller is interested in. |`dsd`| Yes
measures | String | This parameter specifies the measures to be returned. Possible options are: `all` (all measures), `none` (no measure), `{measure_id}`: The ID of one or more measures the caller is interested in. |`all`| Yes
includeHistory | Boolean | This parameter allows retrieving previous versions of the data, as they were disseminated in the past (_history_ or _timeline_ functionality). When the value is set to `true`, the returned data message should contain one or two datasets per data dissemination, depending on whether a dissemination also deleted observations from the data source. The `validFromDate` and/or `validToDate` attributes of the dataset should be used to indicate the periods of validity for the data contained in the data set. See below for an example on how to handle the `includeHistory` parameter. | `false` | No
offset | Positive integer | The number of observations (or series keys) to skip before beginning to return observations (or series keys). | 0 | No
limit | Positive integer | The maximum number of observations (or series keys) to be returned. If no limit is set, all matching observations (or series keys) must be returned. | | No
sort | String | This parameter specifies the order in which the returned data should be sorted. It contains either one or more component (dimension, attribute or measure) IDs, by which the data should be sorted, separated by `+` (to indicate an AND), the `*` operator, which represents all dimensions as positioned in the DSD, or the keyword `series_key`, which represents, when `dimensionAtObservation` is not equal to `AllDimensions`, all dimensions not presented at the observational level and as positioned in the DSD. The sorting must respect the sequence, in which the components are listed. In addition, each component, or the set of components (through the operator or keyword) can be sorted in ascending or descending order by appending `:asc` or `:desc`, with `:asc` being the default. For any component not included in the sort parameter, the related order is non-deterministic. Except for time periods, which have a natural chronological order, the sorting within a component is based on the code IDs or the non-coded component values. | | No
asOf | xs:dateTime | Retrieve the data as they were at the specified point in time (aka time travel). In case both `updatedAfter` and `asOf` are set, the service is expected to return a client error if `updatedAfter` is more recent than `asOf`. | | No
reportingYearStartDay | String | This parameter allows providing an explicit value for the reporting year start day. This is useful when the data is not related to a Gregorian calendar year, such as, for example, when the data is related to, say, a fiscal year. For example, if the query requests data greater than or equal to 2010-Q3 (`c[TIME_PERIOD]=ge:2010-Q3`), and sets `reportingYearStartDay` to `--07-01`, then any data where the start period occurs on or after `2010-01-01T00:00:00` should be returned. | | No

The following rules apply:

- Multiple values for a parameter must be separated using a comma (`,`).
- Two additional operators are supported for the _version_ parameter: the `+`, to indicate the latest stable version of an artefact, and `~`, to indicate the latest version of an artefact regardless of its status (draft vs. stable).
- Default values do not need to be supplied if they are the last element in the path.
- Operators can be used to refine the applicability of the `c` query parameter:  

Operator | Meaning | Note
-- | -- | --
eq | Equals | Default if no operator is specified and there is only one value (e.g. `c[FREQ]=M` is equivalent to `c[FREQ]=eq:M`)
ne | Not equal to |
lt | Less than |
le | Less than or equal to |
gt | Greater than |
ge | Greater than or equal to |
co | Contains |
nc | Does not contain |
sw | Starts with |
ew | Ends with |

Operators appear as prefix to the component value(s) and are separated from it by a `:` (e.g. `c[TIME_PERIOD]=ge:2020-01+le:2020-12`).

The table below offers a few examples and how they should be interpreted.

| Example | Meaning |
| --- | --- |
|c[X]=A |X = A|
|c[X]=A,B |X = A OR X = B|
|c[X]=ge:A |X >= A|
|c[X]=ge:A+le:B |X >= A AND X <= B|
|c[X]=A,B+C |X = A OR (X = B AND X = C)|
|c[X]=ge:A+le:B,ge:C+le:D |(X >= A AND X <= B) OR (X >= C AND X <= D)|
|c[X]=ne:A,B |X <> A OR X = B|
|c[X]=ne:A+B |X <> A AND X = B|
|c[X]=ne:A,ne:B |X <> A OR X <> B|
|c[X]=ne:A+ne:B |X <> A AND X <> B|

> [!TIP]
> Some servers treat square brackets (`[` and `]`) as invalid characters in URLs. If the issue occurs, please encode them using `%5B` and `%5D` respectively.

## Response types

The following media types can be used with _data_ queries:

- **application/vnd.sdmx.data+json;version=2.1.0**
- application/vnd.sdmx.data+xml;version=3.1.0
- application/vnd.sdmx.data+csv;version=2.1.0;labels=[id|name|both];timeFormat=[original|normalized];keys=[none|obs|series|both]
- application/vnd.sdmx.data+json;version=2.0.0
- application/vnd.sdmx.data+xml;version=3.0.0
- application/vnd.sdmx.data+csv;version=2.0.0;labels=[id|name|both];timeFormat=[original|normalized];keys=[none|obs|series|both]

The default format is highlighted in **bold**. For media types of previous SDMX versions, please consult the documentation of the SDMX version you are interested in.

SDMX-CSV offers the possibility to set the value for three parameters via the media-type. These parameters are `label`, `timeFormat`, and `keys`. All the parameters are optional. The default values for these parameters are `id`, `original`, and `none` respectively. For additional information about these parameters, please refer to the [SDMX-CSV specification](https://github.com/sdmx-twg/sdmx-csv).

## Use cases behind the various time-related queries

### Efficient data exchanges

When executing a data query such as, for example `https://ws-endpoint/data/dataflow/ECB/EXR`, you will get the current version of the data matching that query. However, you may want to only retrieve the most recent changes since the last time you executed the same query (so called _deltas_). This is the purpose of the `updatedAfter` query parameters. This option supports very efficient data exchanges, as only the most recent changes will be transmitted between the service and the client.

### Data caching

Sometimes, you may want your data store to act as a data cache, i.e. you want to cache the result of a query, while ensuring that the cache remains up-to-date.

In this scenario, you want to retrieve all the data, but only if something has changed since the last time you executed the same query. To support this, you can leverage HTTP features, such as the `If-Modified-Since` or `If-None-Match` HTTP headers. In this case, you will get a `200` status code (`OK`) and all the data matching the query if something has changed in the data, or a `304` status code (`Not Modified`) if nothing was modified. Whenever you get a 304, you can serve the data from your cache, as it is still valid. In case you get a 200, you need to replace the content of your cache with the updated content.

### Data replication

Sometimes, you may want your data store to act as a replica of another data store. In this case, you don't only want the most recent changes (like when using `updatedAfter`), you want to ensure that you get every change ever made to the data. This is the purpose of the `includeHistory` parameter. With `includeHistory`, if the data matching the query has changed 5 times, you will get 5 datasets in the response, with their `validFrom` and `validTo` properties indicating when these data were valid.

`includeHistory` and `updatedAfter` can be combined in the same query to retrieve all versions of the data matching the query, but only those that changed after a certain point in time.

### Time travel

Using the `asOf` parameter, you can retrieve the data as they were at a certain point in time, for example, as they were when a certain report or press release was published.

This can be combined with `updatedAfter`, to retrieve the data as they were at the `asOf` point in time, but only those that were updated after the `updatedAfter` point in time. The `updatedAfter` point in time must be before the `asOf` point in time.


### Vintages

In statistics (and especially in economics and finance), a **vintage** refers to the version of a dataset that was available at a specific point in time. Because statistical data are often revised as new information comes in or methods change (e.g., GDP estimates, inflation, employment figures), multiple vintages of the same statistic may exist.

For example, the first vintage of quarterly GDP might be released shortly after the quarter ends, based on partial data. Later vintages (second, third, final) are published as more complete or revised information becomes available. In practice, this can mean that the same observation changes over time as more information becomes known. Researchers may need to access these historical versions to compare vintages, study data revisions, or simulate how policymakers would have made decisions in real time (since they only had access to earlier vintages).

The SDMX REST API provides two mechanisms to work with vintages:

- The `includeHistory` parameter allows a researcher to retrieve an observation (e.g., GDP for a country) together with its historical changes over time.  
- The `asOf` parameter allows a researcher to retrieve the dataset *as it was* at a given point in time, enabling the reconstruction of historical datasets as they were originally published.  

By default, the REST API interprets the timestamps used by `includeHistory` and `asOf` as the **transactional time** — the moment when the data was loaded into the database. However, some systems may implement techniques to *backload* datasets with custom timestamps to reflect earlier publication dates.

## Examples of queries

- Retrieve the data matching the supplied path parameters:

        https://ws-entry-point/data/dataflow/ECB/EXR/1.0.0/M.USD.EUR.SP00.A
        
- Retrieve the data matching the supplied path parameters, and, for the attributes, only retrieve OBS_STATUS and UNIT_MEASURE :

        https://ws-entry-point/data/dataflow/ECB/EXR/1.0.0/M.USD.EUR.SP00.A?attributes=OBS_STATUS,UNIT_MEASURE

- Retrieve the data matching the supplied path parameters, including multiple versions and a wildcard for the second dimension:

        https://ws-entry-point/data/datastructure/ECB/ECB_EXR1/1.0.0,2.0.0/M.*.EUR.SP00.A

- Retrieve the data matching the supplied path parameters (including multiple keys and the latest stable version):

        https://ws-entry-point/data/dataflow/ECB/EXR/+/M.USD.EUR.SP00.A,A.CHF.EUR.SP00.A?updatedAfter=2009-05-15T14%3A15%3A00%2B01%3A00

- Retrieve the _public_ data matching the supplied path parameters (all data reported for the latest version of the ECB EXR dataflow), and falling between the supplied the start and end periods:

        https://ws-entry-point/data/dataflow/ECB/EXR/?c[TIME_PERIOD]=ge:2009-05-01+le:2009-05-31&c[CONF_STATUS]=F

- Retrieve the list of indicators about Switzerland (CH) available in the source:

        https://ws-entry-point/data/?c[REF_AREA]=CH&attributes=none&measures=none

- Retrieve the list of observations matching the supplied path parameters, that are above a certain threshold:

        https://ws-entry-point/data/dataflow/ECB/EXR/1.0.0/M.USD.EUR.SP00.A?c[OBS_VALUE]=ge:10.0&dimensionAtObservation=AllDimensions

- Retrieve the list of indicators containing euro in their title:

        https://ws-entry-point/data/?c[TITLE]=co:euro&attributes=none&measures=none

- Retrieve deltas using `updatedAfter`:

        https://ws-entry-point/data/dataflow/ECB/EXR?updatedAfter=2009-05-15T14%3A15%3A00%2B01%3A00

  By supplying a percent-encoded timestamp to the `updatedAfter` parameter, it is possible to **only retrieve the latest version of changed values** in the database since a certain point in time (so-called _updates and revisions_ or _deltas_).

  The response to such a query could include one or more dataset(s) representing:

  - The observations that have been **added** since the last time the query was performed.
  - The observations that have been **revised** since the last time the query was performed.
  - The observations that have been **deleted** since the last time the query was performed.
  
  Developers who update their local databases should make use of the `updatedAfter` parameter as it is likely to significantly **improve performance**. Instead of systematically downloading data that may not have changed, you would only receive the _consolidated_ changes to be made in your database since the last time your client performed the same query.

  An alternative, less efficient than the solution described above, but more efficient than downloading everything all the time, would be to use HTTP Conditional GET requests (i.e. the `If-Modified-Since` or `If-None-Match` HTTP Request headers). Using this mechanism, everything will be returned but only if something has changed since the previous query.

  A sample response message is provided below. In the response the `action` attribute of the `Dataset` element is of importance whereas the `validFromDate` is just there for information purposes.

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <message:GenericData
    xmlns:message="http://www.sdmx.org/resources/sdmxml/schemas/v2_1/message"
    xmlns:common="http://www.sdmx.org/resources/sdmxml/schemas/v2_1/common"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:generic="http://www.sdmx.org/resources/sdmxml/schemas/v2_1/data/generic"
    xsi:schemaLocation="http://www.sdmx.org/resources/sdmxml/schemas/v2_1/message https://xml.sdmx.org/2.1/SDMXMessage.xsd http://www.sdmx.org/resources/sdmxml/schemas/v2_1/common https://xml.sdmx.org/2.1/SDMXCommon.xsd http://www.sdmx.org/resources/sdmxml/schemas/v2_1/data/generic https://xml.sdmx.org/2.1/SDMXDataGeneric.xsd">
    <message:Header>
        <message:ID>388d1c9a-d187-4f6a-8792-e117cf34047f</message:ID>
        <message:Test>false</message:Test>
        <message:Prepared>2016-12-20T16:19:56.398+01:00</message:Prepared>
        <message:Sender id="ECB"/>
        <message:Structure structureID="ECB_RTD1" dimensionAtObservation="TIME_PERIOD">
            <common:Structure>
                <URN>urn:sdmx:org.sdmx.infomodel.datastructure.DataStructure=ECB:ECB_RTD1(1.0)</URN>
            </common:Structure>
        </message:Structure>
    </message:Header>
    <message:DataSet action="Replace" validFromDate="2016-12-20T16:19:56.398+01:00" structureRef="ECB_RTD1"> 
    [...]</message:DataSet>
    <message:DataSet action="Delete" validToDate="2016-12-20T16:19:56.440+01:00" structureRef="ECB_RTD1"> 
    [...]</message:DataSet>
  </message:GenericData>
  ```

- Retrieve a limited amount of observations using `firstNObservations` and `lastNObservations`:

        https://ws-entry-point/data/dataflow/ECB/EXR/1.0.0/M.USD.EUR.SP00.A?lastNObservations=2

  Using the `firstNObservations` and/or `lastNObservations` parameters, it is possible to specify the **maximum number of observations** to be returned for each of the matching series, starting from the first observation (`firstNObservations`) or counting back from the most recent observation (`lastNObservations`). This can be useful for building an overview page, for example, where, for each indicator, you only display 2 values (the current one and the previous one).

- Retrieve how a time series evolved over time using the `includeHistory` parameter:

        https://ws-entry-point/data/dataflow/ECB/EXR/1.0.0/M.USD.EUR.SP00.A?includeHistory=true

  Using the `includeHistory` parameter, you can instruct the web service to return **previous versions of the matching data**. This is useful to see how the data have evolved over time, i.e. when new data have been released or when data have been revised or deleted. Possible options are:

  - `false`: Only the version currently in production will be returned. This is the default.
  - `true`: The version currently in production, as well as all previous versions, will be returned.

  Such a query would give you the _history_ between that point in time and today.

  A sample response message is provided below. In the response the `action` & the `validFromDate` attributes of the `Dataset` element are both equally important. While the `action` attribute indicates what needs to be done, the `validFromDate` attribute allows defining the validity periods of the reported data.
  
  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <message:GenericData
    xmlns:message="http://www.sdmx.org/resources/sdmxml/schemas/v2_1/message"
    xmlns:common="http://www.sdmx.org/resources/sdmxml/schemas/v2_1/common"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:generic="http://www.sdmx.org/resources/sdmxml/schemas/v2_1/data/generic"
    xsi:schemaLocation="http://www.sdmx.org/resources/sdmxml/schemas/v2_1/message https://xml.sdmx.org/2.1/SDMXMessage.xsd http://www.sdmx.org/resources/sdmxml/schemas/v2_1/common https://xml.sdmx.org/2.1/SDMXCommon.xsd http://www.sdmx.org/resources/sdmxml/schemas/v2_1/data/generic https://xml.sdmx.org/2.1/SDMXDataGeneric.xsd">
    <message:Header>
        <message:ID>a2c99026-00c8-4f9a-a3d4-1891f89bd2b0</message:ID>
        <message:Test>false</message:Test>
        <message:Prepared>2016-12-20T17:16:51.578+01:00</message:Prepared>
        <message:Sender id="ECB"/>
        <message:Structure structureID="ECB_RTD1" dimensionAtObservation="TIME_PERIOD">
            <common:Structure>
                <URN>urn:sdmx:org.sdmx.infomodel.datastructure.DataStructure=ECB:ECB_RTD1(1.0)</URN>
            </common:Structure>
        </message:Structure>
    </message:Header>
    <message:DataSet action="Replace" validFromDate="2014-12-03T15:30:00.000+01:00" structureRef="ECB_RTD1"> 
    [...]</message:DataSet>
    <message:DataSet action="Replace" validFromDate="2015-06-02T15:30:00.000+02:00" structureRef="ECB_RTD1"> 
    [...]</message:DataSet>
    <message:DataSet action="Replace" validFromDate="2015-10-21T15:30:00.000+02:00" structureRef="ECB_RTD1"> 
    [...]</message:DataSet>
    <message:DataSet action="Replace" validFromDate="2016-06-01T15:30:00.000+02:00" structureRef="ECB_RTD1"> 
    [...]</message:DataSet>
    <message:DataSet action="Delete" validToDate="2014-11-05T15:30:00.000+01:00" structureRef="ECB_RTD1"> 
    [...]</message:DataSet>
  </message:GenericData>
  ```
  
- Retrieve the first 100 observations, sorted by ascending series key and descending time period:

        https://ws-entry-point/data/dataflow/ECB/EXR?offset=0&limit=100&sort=series_key:asc+TIME_PERIOD:desc

  The above is equivalent to:

        https://ws-entry-point/data/dataflow/ECB/EXR?limit=100&sort=series_key+TIME_PERIOD:desc

- Retrieve the third batch of 1000 observations (skipping the first 2000 observations):

        https://ws-entry-point/data/dataflow/ECB/EXR?offset=2000&limit=1000
  
- Retrieve the data sorted by descending time period:

        https://ws-entry-point/data/dataflow/ECB/EXR/1.0.0/M.USD.EUR.SP00.A?sort=TIME_PERIOD:desc

- Retrieve the data sorted by ascending CURRENCY and time period:

        https://ws-entry-point/data/dataflow/ECB/EXR?sort=CURRENCY+TIME_PERIOD

- Retrieve the data sorted ascendingly for all dimensions as positioned in the DSD:

        https://ws-entry-point/data/dataflow/ECB/EXR?sort=*:asc

  The above is equivalent to:

        https://ws-entry-point/data/dataflow/ECB/EXR?sort=*

- Retrieve attributes, but no data, using the `attributes` and `measures` parameters:

        https://ws-entry-point/data/dataflow/ECB/EXR/1.0.0/M.USD.*.*.*?attributes=all&measures=none

  Services must return **all attributes** relevant for the matching data. For example, let's assume the following DSD (different from previous examples):

  ```
  Dimensions:
    FREQ: Frequency
    CUR1: Currency 1
    CUR2: Currency 2
    TIME_PERIOD: Time period
  Measures:
    OBS_VALUE: Observation Value
  Dataflow attributes:
    UNIT_MULT: Unit multiplier
  Group attributes:
    DECIMALS: Number of decimals
    UNIT_MEAS: Unit of measure
  Time series attributes:
    COLL: Collection
  Observation attributes:
    OBS_COM: Observation comment
    OBS_STATUS: Observation status
  ```

  And let's assume the following dataset:
  
  ```
  FREQ,CUR1,CUR2,TIME_PERIOD,OBS_VALUE,UNIT_MULT,DECIMALS,UNIT_MEAS,COLL,OBS_COM,OBS_STATUS
  D,CHF,EUR,2021-10-05,1.0752,0,4,CHF,E,,A
  M,CHF,EUR,2021-09,1.0857,0,4,CHF,A,,A
  M,USD,EUR,2021-09,1.032,0,4,USD,A,,A
  ```

  When querying for `D.CHF.*`, the first data row would be matched. All attributes applying to this row must be returned, regardless of their attachment level, i.e. the response must include the dataflow-level (`UNIT_MULT`), the group-level (`DECIMALS`, `UNIT_MEAS`), the series-level (`COLL`), and the observation-level (`OBS_STATUS`) attributes. `OBS_COM` can be ignored, as no value is available for this optional attribute.
