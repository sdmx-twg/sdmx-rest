## Data and Metadata Queries

### Resources

The following resources should be supported:

- data
- metadata

### Parameters

#### Parameters used for identifying a resource

The following parameters are used for identifying resources in data queries:

Parameter | Type | Description
--- | --- | ---
flowRef | A string identifying the dataflow. The syntax is agency id, artefact id, version, separated by a ",". For example: AGENCY_ID,FLOW_ID,VERSION. In case the string only contains one out of these 3 elements, it is considered to be the flow id, i.e. all,FLOW_ID,latest. In case the string only contains two out of these 3 elements, they are considered to be the agency id and the flow id, i.e. AGENCY_ID,FLOW_ID,latest. | The data (or metadata) flow of the data (or metadata) to be returned. Its a common use case in SDMX-based web services that the flow id is sufficient to uniquely identify a dataflow. Should this not be the case, the agency id and the dataflow version, can be used, in conjunction with the flow id, in order to uniquely identify a dataflow.
key | A string compliant with the KeyType defined in the SDMX WADL. | The key of the artefact to be returned. Wildcarding is supported by omitting the dimension code for the dimension to be wildcarded. For example, if the following series key identifies the bilateral exchange rates for the daily US dollar exchange rate against the euro, D.USD.EUR.SP00.A, then the following series key can be used to retrieve the data for all currencies against the euro: D..EUR.SP00.A.The OR operator is supported using the + character. For example, the following series key can be used to retrieve the exchange rates against the euro for both the US dollar and the Japanese Yen: D.USD+JPY.EUR.SP00.A.
providerRef | A string identifying the provider. The syntax is agency id, provider id, separated by a ",". For example: AGENCY_ID,PROVIDER_ID. In case the string only contains one out of these 2 elements, it is considered to be the provider id, i.e. all,PROVIDER_ID. | The provider of the data (or metadata) to be retrieved. If not supplied, the returned message will contain data (or metadata) provided by any provider. Its a common use case in SDMX-based web services that the provider id is sufficient to uniquely identify a data provider. Should this not be the case, the agency can be used, in conjunction with the provider id, in order to uniquely identify a data provider. The OR operator is supported using the + character. For example, the following value can be used to indicate that the data should be provided by the Swiss National Bank (CH2) or Central Bank of Norway (NO2): CH2+NO2.

The parameters mentioned above are specified using the following syntax:

    protocol://ws-entry-point/resource/flowRef/key/providerRef
    
Furthermore, some keywords may be used:

Keywords | Scope | Description
--- | --- | ---
all | key | Returns all data belonging to the specified dataflow and provided by the specified provider.
all | providerRef | Returns all data matching the supplied key and belonging to the specified dataflow that has been provided by any data provider. As `all` is a reserved keyword in the SDMX RESTful API, it is recommended not to use it as an identifier for providers.

The following rules apply:

- If no key is specified, all data (or metadata) belonging to the dataflow (or metadataflow) identified by the flowRef should be supplied. It is therefore equivalent to using the keyword `all`.
- If no providerRef is specified, the matching data (or metadata) provided by any data provider should be returned. It is therefore equivalent to using the keyword `all`.

#### Parameters used to further filter the desired results

The following parameters are used to further describe (or filter) the desired results, once the resource has been identified. As mentioned in 3.2, these parameters go in the query string part of the URL.

Parameter | Type | Description
--- | --- | ---
startPeriod | common:StandardTimePeriodType, as defined in the SDMXCommon.xsd schema. Can be expressed using *dateTime* (all data that falls between the calendar dates will be matched), *Gregorian Period* (all data that falls between the calendar dates will be matched) or *Reporting Period* (all data reported as periods that fall between the specified periods will be returned. When comparing reporting weeks and days to higher order periods (e.g. quarters) one must account for the actual time frames covered by the periods to determine whether the data should be included. Data reported as Gregorian periods or distinct ranges will be returned if it falls between the specified reporting periods, based on a reporting year start day of January 1). In case the `:` or `+` characters are used, the parameter must be [percent-encoded](http://en.wikipedia.org/wiki/URL_encoding#Percent-encoding_reserved_characters) by the client. Note that this value is assumed to be inclusive to the range of data being sought. For additional information about the formats, see section 4.2.14 of Section 06 (SDMX Technical Notes). | The start period for which results should be supplied (inclusive).
endPeriod | Same as above | The end period for which results should be supplied (inclusive).
updatedAfter | xs:dateTime | The last time the query was performed by the client in the database. If this attribute is used, the returned message should only include the latest version of what has changed in the database since that point in time (updates and revisions). This should include observations that have been added since the last time the query was performed (INSERT), observations that have been revised since the last time the query was performed (UPDATE) and observations that have been deleted since the last time the query was performed (DELETE). If no offset is specified, default to local time of the web service. If the information about when the data has been updated is not available at the observation level, the web service should return either the series that have changed (if the information is attached at the series level) or the dataflows that have changed (if the information is attached at the dataflow level).
firstNObservations | Positive integer | Integer specifying the maximum number of observations to be returned for each of the matching series, starting from the first observation
lastNObservations | Positive integer | Integer specifying the maximum number of observations to be returned for each of the matching series, counting back from the most recent observation
dimensionAtObservation<sup> | A string compliant with the SDMX common:NCNameIDType | The ID of the dimension to be attached at the observation level. This parameter allows the client to indicate how the data should be packaged by the service. The options are `TIME_PERIOD` (a *timeseries* view of the data), the `ID of any other dimension` used in that dataflow (a *cross-sectional* view of the data) or the keyword `AllDimensions` (a *flat* view of the data where the observations are not grouped, neither in time series, nor in sections). In case this parameter is not set, the service is expected to: Default to TimeDimension, if the data structure definition has one; If not, default to MeasureDimension, if the data structure definition has one; If none of the above is true, default to AllDimensions.
detail | String | This attribute specifies the desired amount of information to be returned. For example, it is possible to instruct the web service to return data only (i.e. no attributes). Possible options are: `full` (all data and documentation, including annotations - This is the default), `dataonly` (attributes  and therefore groups will be excluded from the returned message), `serieskeysonly` (returns only the series elements and the dimensions that make up the series keys. This is useful for performance reasons, to return the series that match a certain query, without returning the actual data) and `nodata` (returns the groups and series, including attributes and annotations, without observations).
includeHistory | Boolean | This attribute allows retrieving previous versions of the data, as they were disseminated in the past (*history* or *timeline* functionality). When the value is set to `true`, the returned SDMX-ML data message should contain one or two datasets per data dissemination, depending on whether a dissemination also deleted observations from the data source. The `validFromDate` and/or `validToDate` attributes of the dataset should be used to indicate the periods of validity for the data contained in the data set. See below for an example on how to handle the `includeHistory` parameter. Default to `false`.

The table below defines the meaning of parameters combinations:

Combination | Meaning
--- | ---
startPeriod with no endPeriod | Until the most recent
endPeriod and no startPeriod | From the beginning
startPeriod and endPeriod | Within the supplied time range
lastNObservations + startPeriod/endPeriod | The specified number of observations, starting from the end, within the supplied time range
firstNObservations + startPeriod/endPeriod + updatedAfter | The specified number of observations, starting from the beginning, that have changed since the supplied timestamp, within the supplied time range
updatedAfter + startPeriod/endPeriod | The observations, within the supplied time range, that have changed since the supplied timestamp.

### Examples

* To retrieve the data for the series M.USD.EUR.SP00.A supplied by the ECB for the ECB_EXR1_WEB dataflow:

        http://ws-entry-point/data/ECB_EXR1_WEB/M.USD.EUR.SP00.A/ECB

    In this example, the assumption is made that the dataflow id (ECB_EXR1_WEB) is sufficient to uniquely identify the dataflow, and the data provider id (ECB) is sufficient to uniquely identify the data provider.

* To retrieve the data, provided by the ECB for the ECB_EXR1_WEB dataflow, for the supplied series keys, using wildcarding for the second dimension:

        http://ws-entry-point/data/ECB,ECB_EXR1_WEB,latest/M..EUR.SP00.A/ECB

    In this example, the full reference to the dataflow is supplied (ECB as maintenance agency, ECB_EXR1_WEB as dataflow id and latest for the version)

* To retrieve the updates and revisions for the data matching the supplied series keys, using the OR operator for the second dimension, and using percent encoding for the updatedAfter:

        http://ws-entry-point/Data/ECB_EXR1_WEB/M.USD+GBP+JPY.EUR.SP00.A?
        updatedAfter=2009-05-15T14%3A15%3A00%2B01%3A00

* To retrieve the data matching the supplied series key and restricting the start and end dates:

        http://ws-entry-point/data/ECB_EXR1_WEB/D.USD.EUR.SP00.A?
        startPeriod=2009-05-01&endPeriod=2009-05-31

### Example: How to handle the `includeHistory` parameter

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
