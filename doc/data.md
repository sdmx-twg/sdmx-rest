# Data queries

## Overview

Data queries allow **retrieving statistical data**. Entire datasets, individual observations, or anything in between, can be retrieved using filters on dimensions (including time), attributes and/or measures. All data matching a query can be retrieved or only the data that has changed since the last time the same query was performed. Using the _includeHistory_ parameter, it is also possible to retrieve previous versions of the data. Last but not least, the data retrieved can be packaged in different ways (as time series, cross-sections or as a table), in a variety of formats (JSON, XML, CSV, etc.).

## Syntax

    protocol://ws-entry-point/data/{context}/{agencyID}/{resourceID}/{version}/{key}?
    {c}&{updatedAfter}&{firstNObservations}&{lastNObservations}&{dimensionAtObservation}&{attributes}&{measures}&{includeHistory}

Parameter | Type | Description | Default | Multiple values?
--- | --- | --- | --- | ---
context | One of the following: `datastructure`, `dataflow`, `provisionagreement` | Data can be reported against a data structure, a dataflow or a provision agreement. This parameter allows selecting the desired context for data retrieval. | * | Yes
agencyID | A string compliant with the SDMX *common:NCNameIDType* | The agency maintaining the artefact for which data have been reported. | * | Yes
resourceID | A string compliant with the SDMX *common:IDType* | The id of the artefact for which data have been reported. | * | Yes
version | A string compliant with the allowed SDMX versioning schemes | The version of the artefact for which data have been reported. | * | Yes
key | A string compliant with the *KeyType* defined in the SDMX Open API specification. | The combination of dimension values identifying the slice of the cube for which data should be returned. Wildcarding is supported via the `*` operator. For example, if the following key identifies the bilateral exchange rates for the daily US dollar exchange rate against the euro, D.USD.EUR.SP00.A, then the following key can be used to retrieve the data for all currencies against the euro: D.`*`.EUR.SP00.A. Any dimension value omitted at the end of the Key is assumed as equivalent to a wildcard, e.g. D.USD is equivalent to D.USD.`*`.`*`.`*`  | * | Yes
c | Map | Filter data by component value. For example, if a structure defines a frequency dimension (FREQ) and the code A (Annual) is an allowed value for that dimension, the following can be used to retrieve annual data: `c[FREQ]=A`. The same applies to attributes (e.g. `c[CONF_STATUS]=F`) and measures. Multiple values are supported, using a comma (`,`) as separator: `c[FREQ]=A,M`. The comma effectively acts as an `OR` statement (i.e. FREQ is A OR FREQ is M). The plus (`+`) can be used whenever an `AND` statement is required, such as for example, for attributes with multiple values or for time ranges. For example, to indicate that ATTR1 must either be A or (B AND M), use the following: `c[ATTR1]=A,B+M`. Operators may be used too (see table with operators below). This parameter can be used in addition, or instead of, the `key` path parameter. This parameter may be used multiple times (e.g. `c[FREQ]=A,M&c[CONF_STATUS]=F`), but only once per Component. | | Yes
updatedAfter | xs:dateTime | The last time the query was performed by the client in the database. If this parameter is used, the returned message should only include the latest version of what has changed in the database since that point in time (updates and revisions). This should include observations that have been added since the last time the query was performed (INSERT), observations that have been revised since the last time the query was performed (UPDATE) and observations that have been deleted since the last time the query was performed (DELETE). If no offset is specified, default to local time of the web service. If the information about when the data has been updated is not available at the observation level, the web service should return either the series that have changed (if the information is attached at the series level) or the dataflows that have changed (if the information is attached at the dataflow level). | | No
firstNObservations | Positive integer | The maximum number of observations to be returned for each of the matching series, starting from the first observation | | No
lastNObservations | Positive integer | The maximum number of observations to be returned for each of the matching series, counting back from the most recent observation ||No
dimensionAtObservation | A string compliant with the SDMX common:NCNameIDType | The ID of the dimension to be attached at the observation level. This parameter allows the client to indicate how the data should be packaged by the service. The options are `TIME_PERIOD` (a *timeseries* view of the data), the `ID of any other dimension` used in that dataflow (a *cross-sectional* view of the data) or the keyword `AllDimensions` (a *flat* / *table* view of the data where the observations are not grouped, neither in time series, nor in sections). In case this parameter is not set, the service is expected to default to TimeDimension, if the data structure definition has one, or else, to default to AllDimensions.|Depends on DSD|No
attributes | String | This parameter specifies the attributes to be returned. Possible options are: `dsd` (all the attributes defined in the data structure definition), `msd` (all the reference metadata attributes), `dataset` (all the attributes attached to the dataset-level), `series` (all the attributes attached to the series-level), `obs` (all the attributes attached to the observation-level), `all` (all attributes), `none` (no attributes), `{attribute_id}`: The ID of one or more attributes the caller is interested in. |`dsd`| Yes
measures | String | This parameter specifies the measures to be returned. Possible options are: `all` (all measures), `none` (no measure), `{measure_id}`: The ID of one or more measures the caller is interested in. |`all`| Yes
includeHistory | Boolean | This parameter allows retrieving previous versions of the data, as they were disseminated in the past (*history* or *timeline* functionality). When the value is set to `true`, the returned data message should contain one or two datasets per data dissemination, depending on whether a dissemination also deleted observations from the data source. The `validFromDate` and/or `validToDate` attributes of the dataset should be used to indicate the periods of validity for the data contained in the data set. See below for an example on how to handle the `includeHistory` parameter. | `false` | No

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

## Response types

The following media types can be used with _data_ queries:

- **application/vnd.sdmx.data+json;version=2.0.0**
- application/vnd.sdmx.data+xml;version=3.0.0
- application/vnd.sdmx.data+csv;version=2.0.0;labels=[id|name|both];timeFormat=[original|normalized];keys=[none|obs|series|both]
- application/vnd.sdmx.genericdata+xml;version=2.1
- application/vnd.sdmx.structurespecificdata+xml;version=2.1
- application/vnd.sdmx.generictimeseriesdata+xml;version=2.1
- application/vnd.sdmx.structurespecifictimeseriesdata+xml;version=2.1
- application/vnd.sdmx.data+json;version=1.0.0
- application/vnd.sdmx.data+csv;version=1.0.0;labels=[id|both];timeFormat=[original|normalized]

The default format is highlighted in **bold**.

SDMX-CSV offers the possibility to set the value for two parameters via the media-type. These parameters are `label` and `timeFormat`; both are optional. The default values for these parameters are marked with * in the above media-type (i.e. `id` and `original` respectively). For additional information about these parameters, please refer to the [SDMX-CSV specification](https://sdmx.org/?sdmx_news=sdmx-csv-format-specifications-just-released).

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

- Retrieving deltas using `updatedAfter`:

        https://ws-entry-point/data/dataflow/ECB/EXR?updatedAfter=2009-05-15T14%3A15%3A00%2B01%3A00

  By supplying a percent-encoded timestamp to the `updatedAfter` parameter, it is possible to **only retrieve the latest version of changed values** in the database since a certain point in time (so-called *updates and revisions* or *deltas*).

  The response to such a query could include one or more dataset(s) representing:

  - The observations that have been **added** since the last time the query was performed.
  - The observations that have been **revised** since the last time the query was performed.
  - The observations that have been **deleted** since the last time the query was performed.
  
  Developers who update their local databases should make use of the `updatedAfter` parameter as it is likely to significantly **improve performance**. Instead of systematically downloading data that may not have changed, you would only receive the *consolidated* changes to be made in your database since the last time your client performed the same query.

  An alternative, less efficient than the solution described above, but more efficient than downloading everything all the time, would be to use HTTP Conditional GET requests (i.e. the `If-Modified-Since` or `If-None-Match` HTTP Request headers). Using this mechanism, everything will be returned but only if something has changed since the previous query.

  A sample response message is provided below. In the response the `action` attribute of the `Dataset` element is of importance whereas the `validFromDate` is just there for information purposes.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<message:GenericData xmlns:message="http://www.sdmx.org/resources/sdmxml/schemas/v2_1/message" xmlns:common="http://www.sdmx.org/resources/sdmxml/schemas/v2_1/common" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:generic="http://www.sdmx.org/resources/sdmxml/schemas/v2_1/data/generic" xsi:schemaLocation="http://www.sdmx.org/resources/sdmxml/schemas/v2_1/message http://sdw-wsrest.ecb.europa.eu:80/vocabulary/sdmx/2_1/SDMXMessage.xsd http://www.sdmx.org/resources/sdmxml/schemas/v2_1/common http://sdw-wsrest.ecb.europa.eu:80/vocabulary/sdmx/2_1/SDMXCommon.xsd http://www.sdmx.org/resources/sdmxml/schemas/v2_1/data/generic http://sdw-wsrest.ecb.europa.eu:80/vocabulary/sdmx/2_1/SDMXDataGeneric.xsd">
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
    <message:DataSet action="Replace" validFromDate="2016-12-20T16:19:56.398+01:00" structureRef="ECB_RTD1">[...]</message:DataSet>
    <message:DataSet action="Delete" validToDate="2016-12-20T16:19:56.440+01:00" structureRef="ECB_RTD1">[...]</message:DataSet>
</message:GenericData>
```

- Retrieving a limited amount of observations using `firstNObservations` and `lastNObservations`:

        https://ws-entry-point/data/dataflow/ECB/EXR/1.0.0/M.USD.EUR.SP00.A?lastNObservations=2

  Using the `firstNObservations` and/or `lastNObservations` parameters, it is possible to specify the **maximum number of observations** to be returned for each of the matching series, starting from the first observation (`firstNObservations`) or counting back from the most recent observation (`lastNObservations`). This can be useful for building an overview page, for example, where, for each indicator, you only display 2 values (the current one and the previous one).

- Retrieving how a time series evolved over time using the `includeHistory` parameter:

        https://ws-entry-point/data/dataflow/ECB/EXR/1.0.0/M.USD.EUR.SP00.A?includeHistory=true

  Using the `includeHistory` parameter, you can instruct the web service to return **previous versions of the matching data**. This is useful to see how the data have evolved over time, i.e. when new data have been released or when data have been revised or deleted. Possible options are:

  - `false`: Only the version currently in production will be returned. This is the default.
  - `true`: The version currently in production, as well as all previous versions, will be returned.

  Such a query would give you the *history* between that point in time and today.

  A sample response message is provided below. In the response the `action` & the `validFromDate` attributes of the `Dataset` element are both equally important. While the `action` attribute indicates what needs to be done, the `validFromDate` attribute allows defining the validity periods of the reported data.
  
```xml
<?xml version="1.0" encoding="UTF-8"?>
<message:GenericData xmlns:message="http://www.sdmx.org/resources/sdmxml/schemas/v2_1/message" xmlns:common="http://www.sdmx.org/resources/sdmxml/schemas/v2_1/common" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:generic="http://www.sdmx.org/resources/sdmxml/schemas/v2_1/data/generic" xsi:schemaLocation="http://www.sdmx.org/resources/sdmxml/schemas/v2_1/message http://sdw-wsrest.ecb.europa.eu:80/vocabulary/sdmx/2_1/SDMXMessage.xsd http://www.sdmx.org/resources/sdmxml/schemas/v2_1/common http://sdw-wsrest.ecb.europa.eu:80/vocabulary/sdmx/2_1/SDMXCommon.xsd http://www.sdmx.org/resources/sdmxml/schemas/v2_1/data/generic http://sdw-wsrest.ecb.europa.eu:80/vocabulary/sdmx/2_1/SDMXDataGeneric.xsd">
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
    <message:DataSet action="Replace" validFromDate="2014-12-03T15:30:00.000+01:00" structureRef="ECB_RTD1">[...]</message:DataSet>
    <message:DataSet action="Replace" validFromDate="2015-06-02T15:30:00.000+02:00" structureRef="ECB_RTD1">[...]</message:DataSet>
    <message:DataSet action="Replace" validFromDate="2015-10-21T15:30:00.000+02:00" structureRef="ECB_RTD1">[...]</message:DataSet>
    <message:DataSet action="Replace" validFromDate="2016-06-01T15:30:00.000+02:00" structureRef="ECB_RTD1">[...]</message:DataSet>
    <message:DataSet action="Delete" validToDate="2014-11-05T15:30:00.000+01:00" structureRef="ECB_RTD1">[...]</message:DataSet>
</message:GenericData>
```
