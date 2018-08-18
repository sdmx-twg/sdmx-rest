## Other Queries

### Data availability

The `availableconstraint` resource defines what data are available for a Dataflow, based on a data query. It returns a `ContentConstraint`, i.e. structural metadata, and is therefore similar to the other structural metadata queries but the query itself is more akin to a data query. 

#### Parameters

##### Parameters used for identifying a resource

The following parameters are used for identifying resources:

Parameter | Type | Description
--- | --- | ---
flowRef | A string identifying the dataflow. The syntax is agency id, artefact id, version, separated by a ",". For example: AGENCY_ID,FLOW_ID,VERSION. In case the string only contains one out of these 3 elements, it is considered to be the flow id, i.e. all,FLOW_ID,latest. In case the string only contains two out of these 3 elements, they are considered to be the agency id and the flow id, i.e. AGENCY_ID,FLOW_ID,latest. | The dataflow of the data for which the data availability information is to be returned. Its a common use case in SDMX-based web services that the flow id is sufficient to uniquely identify a dataflow. Should this not be the case, the agency id and the dataflow version, can be used, in conjunction with the flow id, in order to uniquely identify a dataflow.
key | A string compliant with the KeyType defined in the SDMX WADL. | The key of the artefact to be returned. Wildcarding is supported by omitting the dimension code for the dimension to be wildcarded. For example, if the following series key identifies the bilateral exchange rates for the daily US dollar exchange rate against the euro, D.USD.EUR.SP00.A, then the following series key can be used to retrieve the data for all currencies against the euro: D..EUR.SP00.A.The OR operator is supported using the + character. For example, the following series key can be used to retrieve the exchange rates against the euro for both the US dollar and the Japanese Yen: D.USD+JPY.EUR.SP00.A.
providerRef | A string identifying the provider. The syntax is agency id, provider id, separated by a ",". For example: AGENCY_ID,PROVIDER_ID. In case the string only contains one out of these 2 elements, it is considered to be the provider id, i.e. all,PROVIDER_ID. | The provider of the data for which the data availability information should be retrieved on.  This can be used in cases where multiple Data Providers provide data for a single Dataflow, and information is required about the data available from only one of the Data Providers for the Dataflow identified. If not supplied, the returned message will contain information about the data provided by all providers. It is a common use case in SDMX-based web services that the provider id is sufficient to uniquely identify a data provider. Should this not be the case, the agency can be used, in conjunction with the provider id, in order to uniquely identify a data provider.
componentId | A string compliant with the SDMX common: IDType | The id of the Dimension for which to obtain availability information about.  In the case where this information is not provided, data availability will be provided for all Dimensions.

The parameters mentioned above are specified using the following syntax:

    protocol://ws-entry-point/resource/flowRef/key/providerRef/componentId
    
Furthermore, some keywords may be used:

Keywords | Scope | Description
--- | --- | ---
all | key | Returns data availability information pertaining to all the data belonging to the specified dataflow and provided by the specified provider.
all | providerRef | Returns data availability information matching the supplied key and belonging to the specified dataflow that has been provided by any data provider. As `all` is a reserved keyword in the SDMX RESTful API, it is recommended not to use it as an identifier for providers.
all | componentId | Returns data availability information matching the supplied key and belonging to the specified dataflow that has been provided by the specified data providers for all Dimensions. As `all` is a reserved keyword in the SDMX RESTful API, it is recommended not to use it as an identifier for providers.  As `all` is a reserved keyword in the SDMX RESTful API, it is recommended not to use it as an identifier for Dimension Ids.

The following rules apply:

- If no key is specified, it is equivalent to using the keyword `all`.
- If no providerRef is specified, it equivalent to using the keyword `all`.
- If no componentId is specified, it equivalent to using the keyword `all`.

##### Parameters used to further filter the desired results

The following query string parameters are used to further describe (or filter) the desired results, once the resource has been identified.

Parameter | Type | Description | Default
--- | --- | --- | ---
startPeriod | common:StandardTimePeriodType, as defined in the SDMXCommon.xsd schema. Can be expressed using *dateTime* (all data that falls between the calendar dates will be matched), *Gregorian Period* (all data that falls between the calendar dates will be matched) or *Reporting Period* (all data reported as periods that fall between the specified periods will be returned. When comparing reporting weeks and days to higher order periods (e.g. quarters) one must account for the actual time frames covered by the periods to determine whether the data should be included. Data reported as Gregorian periods or distinct ranges will be returned if it falls between the specified reporting periods, based on a reporting year start day of January 1). In case the `:` or `+` characters are used, the parameter must be [percent-encoded](http://en.wikipedia.org/wiki/URL_encoding#Percent-encoding_reserved_characters) by the client. Note that this value is assumed to be inclusive to the range of data being sought. For additional information about the formats, see section 4.2.14 of Section 06 (SDMX Technical Notes). | The start period for which results should be supplied (inclusive). | 
endPeriod | Same as above | The end period for which results should be supplied (inclusive). | 
updatedAfter | xs:dateTime | The last time the query was performed by the client. If this attribute is used, the returned message should only include the dimension values for the data that have changed since that point in time (updates and revisions). This should include the dimension values for data that have been added since the last time the query was performed (INSERT), data that have been revised since the last time the query was performed (UPDATE) and data that have been deleted since the last time the query was performed (DELETE). If no offset is specified, default to local time of the web service. | 
references  |  String | This attribute instructs the web service to return (or not) the artefacts referenced by the ContentConstraint to be returned  possible values are: "codelist", "datastructure", "conceptscheme", "dataflow", "dataproviderscheme", "none", "all" | **none**.  The keyword "all" is used to indicate the inclusion of dataflow, datastructure, conceptschemes, dataproviderschemes and codelists. Note, in the case ItemSchemes are returned (i.e. Codelists, ConceptSchemes and DataProviderSchemes), only the items used by the ContentConstraint will be included (i.e. concepts used by the constrained dimensions; codes for which data are available; data providers that have provided data available according to the CubeRegion). Additionally for Codelists parent codes will be included in the response if the child codes are in the returned codelist, irrespective of whether they are referenced by the ContentConstraint. If this results in a partial list, the isPartial attribute will be set to true.
mode  | String  | This attribute instructs the web service to return a ContentConstraint which defines a Cube Region containing values which will be returned by executing the query (mode="exact") vs a Cube Region showing what values remain valid selections that could be added to the data query (mode="available"). A valid selection is one which results in one or more series existing for the selected value, based on the current data query selection state defined by the current path parameters. | **exact**

The table below defines the meaning of parameters combinations:

Combination | Meaning
--- | ---
startPeriod with no endPeriod | Until the most recent
endPeriod and no startPeriod | From the beginning
startPeriod and endPeriod | Within the supplied time range
updatedAfter + startPeriod/endPeriod | Available data within the supplied time range, that have changed since the supplied timestamp

### Examples
* To retrieve the distinct values for each Dimension for the entire ECB_EXR1_WEB dataflow. The metadata used to decode the code ids and concept ids into human readable labels is also requested.   

        http://ws-entry-point/availableconstraint/ECB_EXR1_WEB/
		
In this example, the assumption is made that the dataflow id (ECB_EXR1_WEB) is sufficient to uniquely identify the dataflow,

* For the data query query M..EUR.SP00.A supplied by the ECB for the ECB_EXR1_WEB dataflow, the client requests information about which dimension values remain valid for inclusion into the query:

        http://ws-entry-point/availableconstraint/ECB,ECB_EXR1_WEB,1.0/M..EUR.SP00.A/ECB?mode=available
In this example, the full reference to the dataflow is supplied (ECB as maintenance agency, ECB_EXR1_WEB as dataflow id and latest for the version).

* For the data query M..EUR.SP00.A supplied by the ECB for the ECB_EXR1_WEB dataflow, the client requests information about which dimension values will be returned as a result of executing the query:

        http://ws-entry-point/availableconstraint/ECB_EXR1_WEB/M..EUR.SP00.A/ECB?mode=exact

* As `exact` is the default value for mode, the query above can also be written as follows:

        http://ws-entry-point/availableconstraint/ECB_EXR1_WEB/M..EUR.SP00.A/ECB
