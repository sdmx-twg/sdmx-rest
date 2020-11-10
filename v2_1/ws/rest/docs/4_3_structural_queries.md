## Structural Metadata Queries

### Resources

The following resource is defined:

- structure

### Parameters

#### Parameters used for identifying a resource

The following parameters are used for identifying resources:

Parameter | Type | Description
--- | --- | ---
artefactType | One of the following types: datastructure, metadatastructure, categoryscheme, conceptscheme, codelist, hierarchicalcodelist, organisationscheme, agencyscheme, dataproviderscheme, dataconsumerscheme, organisationunitscheme, dataflow, metadataflow, reportingtaxonomy, provisionagreement, structureset, process, categorisation, contentconstraint, actualconstraint, allowedconstraint, attachmentconstraint | The type of structural metadata to be returned.
agencyID | A string compliant with the SDMX *common:NCNameIDType* | The agency maintaining the artefact to be returned. It is possible to set more than one agency, using `+` as separator (e.g. BIS+ECB).
resourceID | A string compliant with the SDMX *common:IDType* | The id of the artefact to be returned. It is possible to set more than one id, using `+` as separator (e.g. CL_FREQ+CL_CONF_STATUS).
version | A string compliant with the SDMX *common:VersionType* | The version of the artefact to be returned. It is possible to set more than one version, using `+` as separator (e.g. 1.0+2.1).

The parameters mentioned above are specified using the following syntax:

    protocol://ws-entry-point/structure/artefactType/agencyID/resourceID/version

Furthermore, some keywords may be used:

Keywords | Scope | Description
--- | --- | ---
all | artefactType | Returns any type of structural metadata
all | agencyID | Returns artefacts maintained by any maintenance agency
all | resourceID | Returns all resources of the type defined by the resource parameter
all | version | Returns all versions of the resource
latest | version | Returns the latest version in production of the resource

As `all` is a reserved keyword in the SDMX RESTful API, it is recommended not to use it as an identifier for agencies, resources or a specific version.

The following rules apply:

- If no version is specified, the version currently used in production should be returned. It is therefore equivalent to using the keyword `latest`.
-  If no agencyID is specified, the matching artefacts maintained by any maintenance agency should be returned. It is therefore equivalent to using the keyword `all`. This would potentially return more than one artefact, if different agencies give the same identifier to a resource (for example, http://ws-entry-point/codelist/all/CL_FREQ, could return more than one codelist if more than one agency is maintaining a codelist with id "CL_FREQ").
- If no resourceID is specified, all matching artefacts (according to the other criteria used) should be returned. It's is therefore equivalent to using the keyword `all`.
- If no artefactType is specified, all matching artefacts of any type (according to the other criteria used) should be returned. It's is therefore equivalent to using the keyword `all`.
- If no parameters are specified, the **latest** version of **all** resources, maintained by **any** maintenance agency should be returned.


#### Additional parameter used for identifying a resource, for item scheme types

SDMX uses the *item scheme* pattern to model SDMX collections of items. These are:

- categoryscheme
- conceptscheme
- codelist
- organisationscheme
- agencyscheme
- dataproviderscheme
- dataconsumerscheme
- organisationunitscheme
- reportingtaxonomy
- transformationscheme
- rulesetscheme
- userdefinedoperatorscheme
- customtypescheme
- namepersonalisationscheme
- vtlmappingscheme

Although it is not following the *item scheme* pattern, *hierarchicalcodelist* is also a collection, i.e. a collection of hierarchies.

For these collections (those following the *item scheme* pattern or the *hierarchicalcodelist*), it is possible to use a 5th parameter for identifying a resource. The rules for the 4 other parameters, as defined in the section above, remain valid.

Parameter | Type | Description
--- | --- | ---
itemID | A string compliant with the SDMX *common:NestedNCNameIDType* for conceptscheme and agencyscheme, SDMX *common:IDType* for hierarchicalcodelist or with the SDMX *common:NestedIDType* in all other cases | The id of the item to be returned. It is possible to set more than one id, using `+` as separator (e.g. A+Q+M).


This 5th parameter is used as follows:

    protocol://ws-entry-point/structure/artefactType/agencyID/resourceID/version/itemID

Furthermore, a keyword may be used:

Keyword | Scope | Description
--- | --- | ---
all | itemID | Returns all items belonging to the item scheme

The following rules apply:

- If no itemID is specified, all the items belonging to the item scheme should be returned. It is therefore equivalent to using the keyword `all`.
- If `itemID` is set and is a *top-level* id (e.g.: Code A (Annual) in the Frequency Codelist), and such an item exists in the matching item scheme, the item scheme returned should contain only the matching item and its `isPartial` parameter should be set to `true`.
- If `itemID` is set and is a *nested* id (e.g.: Category A.1.1, belonging to Category A.1, belonging to Category A in a Category Scheme), and such an item exists in the matching item scheme, the item scheme returned should contain the matching item and its ancestors, and its `isPartial` parameter should be set to `true`.

#### Parameters used to further describe the desired results

The following parameters are used to further describe the desired results, once the resource has been identified. As mentioned in 3.2, these parameters appear in the query string part of the URL.

Parameter | Type | Description | Default
--- | --- | --- | ---
detail | *String* | This attribute specifies the desired amount of information to be returned. For example, it is possible to instruct the web service to return only basic information about the maintainable artefact (i.e.: id, agency id, version and name). Most notably, items of item schemes will not be returned (for example, it will not return the codes in a code list query). Possible values are: (1) `full`: all available information for all returned artefacts should be returned. Returned extended codelists are to be resolved, i.e. include all inherited codes, and must not include the ExtendedBy property. (2) `allstubs`: all returned artefacts should be returned as stubs, i.e. 
only containing identification information and the artefacts' name. (3) `referencestubs`: same as full with the exception that referenced artefacts should be returned only as stubs, i.e. only containing identification information and the artefacts' name. (4) `allcompletestubs`: all returned artefacts should be returned as complete stubs, i.e. only containing identification information, the artefacts' name, description, annotations and isFinal information. (5) `referencecompletestubs`: same as full with the exception that referenced artefacts should be returned as complete stubs, i.e. only containing identification information, the artefacts' name, description, annotations and isFinal information. (6) `referencepartial`: same as full with the exception that referenced item schemes should only include items used by the artefact to be returned. For example, a concept scheme would only contain the concepts used in a DSD, and its isPartial flag would be set to true. Likewise, if a dataflow has been constrained, then the codelists referenced by the DSD referenced by the dataflow should only contain the codes allowed by the content constraint. (7) `raw`: same as full with the exception that the returned extended codelists are not resolved and must include the ExtendedBy property, and if referenced codelists or descendants are to be returned then they include also all inherited codelists. (8) `partialraw`: same as raw with the exception that referenced item schemes, also including returned inherited codelists, should only include items used by the artefact to be returned, similar to the value 'referencepartial' | **full**
references | *String* | This attribute instructs the web service to return (or not) the artefacts referenced by the artefact to be returned (for example, the code lists and concepts used by the data structure definition matching the query), as well as the artefacts that use the matching artefact (for example, the dataflows that use the data structure definition matching the query). Possible values are: `none` (no references will be returned), `parents` (the artefacts that use the artefact matching the query), `parentsandsiblings` (the artefacts that use the artefact matching the query, as well as the artefacts referenced by these artefacts), `children` (artefacts referenced by the artefact to be returned), `descendants` (references of references, up to any level, will also be returned), `all` (the combination of parentsandsiblings and descendants). In addition, a `concrete type of resource` may also be used (for example, references=codelist). | **none**

#### Applicability and meaning of references attribute

The table below lists the 1st level artefacts (one level up, one level down) that will be returned if the references parameter is set to `all`. Artefacts referenced by the matching artefact are displayed in regular style, while the artefacts that reference the matching artefact are displayed in *Italic*.

Maintainable artefact | Artefacts returned
--- | ---
AgencyScheme | All
Categorisation | All
CategoryScheme | *Categorisations*, *Process*, *StructureSet*, AgencyScheme
Codelist | *Categorisation*, *Process*, *HierarchicalCodelist*, *ConceptScheme*, *DataStructureDefinition*, *MetadataStructureDefinition*, *StructureSet*, AgencyScheme, *VtlMappingScheme*
ConceptScheme | *Categorisation*, *Process*, Codelist, *DataStructureDefinition*, *MetadataStructureDefinition*, *StructureSet*, AgencyScheme, *VtlMappingScheme*
Constraint | *Categorisation*, *Process*, DataProviderScheme, DataStructureDefinition, Dataflow, MetadataStructureDefinition, Metadataflow, ProvisionAgreement, AgencyScheme
DataConsumerScheme | *Categorisation*, *Process*, *MetadataStructureDefinition*, *StructureSet*, AgencyScheme
Dataflow | *Categorisation*, *Process*, *Constraint*, DataStructureDefinition, *ProvisionAgreement*, *ReportingTaxonomy*, *StructureSet*, AgencyScheme, *VtlMappingScheme*
DataProviderScheme | *Categorisation*, *Process*, *Constraint*, *ProvisionAgreement*, *MetadataStructureDefinition*, *StructureSet*, AgencyScheme
DataStructureDefinition | *Categorisation*, *Process*, Codelist, ConceptScheme, *Constraint*, *Dataflow*, *StructureSet*, AgencyScheme
HierarchicalCodelist | *Categorisation*, *Process*, Codelist, *StructureSet*, AgencyScheme
Metadataflow | *Categorisation*, *Process*, *Constraint*, MetadataStructureDefinition, *ProvisionAgreement*, *ReportingTaxonomy*, *StructureSet*, AgencyScheme
MetadataStructureDefinition | *Categorisation*, *Process*, ConceptScheme, Codelist, DataProviderScheme, DataConsumerScheme, AgencyScheme, OrganisationUnitScheme, *Constraint*, *Metadataflow*, *StructureSet*, AgencyScheme
OrganisationUnitScheme | *Categorisation*, *Process*, *Constraint*, *MetadataStructureDefinition*, *StructureSet*, AgencyScheme
Process | All
ProvisionAgreement | *Categorisation*, *Process*, DataProviderScheme, Dataflow, Metadataflow, *Constraint*, AgencyScheme
ReportingTaxonomy | *Categorisation*, *Process*, Dataflow, Metadataflow, *StructureSet*, AgencyScheme
StructureSet | *Categorisation*, *Process*, DataStructureDefinition, MetadataStructureDefinition, CategoryScheme, DataProviderScheme, DataConsumerScheme, AgencyScheme, OrganisationUnitScheme, ConceptScheme, Codelist, ReportingTaxonomy, HierarchicalCodelist, Dataflow, Metadataflow, AgencyScheme
CustomTypeScheme | AgencyScheme, _Categorisation_, _TranformationScheme_, AgencyScheme
NamePersonalisationScheme | AgencyScheme, _Categorisation_, _TranformationScheme_, AgencyScheme
RulesetScheme | AgencyScheme, _Categorisation_, _TranformationScheme_, VtlMappingScheme, AgencyScheme, AgencyScheme
TranformationScheme | AgencyScheme, _Categorisation_, CustomTypeScheme, NamePersonalisationScheme, RulesetScheme, UserDefinedOperatorScheme, VtlMappingScheme, AgencyScheme
UserDefinedOperatorScheme | AgencyScheme, _Categorisation_, _TranformationScheme_, VtlMappingScheme, AgencyScheme
VtlMappingScheme | AgencyScheme, _Categorisation_, Codelist, ConceptScheme, Dataflow, _RulesetScheme_, _TranformationScheme_, _UserDefinedOperatorScheme_, AgencyScheme

### Examples

* To retrieve version 1.0 of the DSD with id ECB_EXR1 maintained by the ECB, as well as the code lists and the concepts used in the DSD:

        http://ws-entry-point/structure/datastructure/ECB/ECB_EXR1/1.0?references=children&detail=referencepartial

* To retrieve the latest version in production of the DSD with id ECB_EXR1 maintained by the ECB, without the code lists and concepts of the DSD:

        http://ws-entry-point/structure/datastructure/ECB/ECB_EXR1

* To retrieve all DSDs maintained by the ECB, as well as the dataflows using these DSDs:

        http://ws-entry-point/structure/datastructure/ECB?references=dataflow

* To retrieve the latest version in production of all code lists maintained by all maintenance agencies, but without the codes:

        http://ws-entry-point/structure/codelist?detail=allstubs

* To retrieve, as stubs, the latest version in production of all maintainable artefacts maintained by the ECB:

        http://ws-entry-point/structure/all/ECB?detail=allstubs
        
* To retrieve the category PRICES of the DOMAINS category scheme maintained by the ECB, as well as the categorisations referencing that category:

        http://ws-entry-point/structure/categoryscheme/ECB/DOMAINS/latest/PRICES?references=categorisation

* To retrieve the latest version of the CL_FREQ codelists maintained by the BIS or the ECB:
        
        http://ws-entry-point/structure/codelist/BIS+ECB/CL_FREQ
