# Structure queries

## Overview

Structure queries allo **retrieving structural metadata**.

Below is a list of popular types of structural metadata, that you will typically find in SDMX web services.

- `Concept schemes`: In order to make sense of some statistical data, we need to know the *concepts* associated with them. For example, on its own, the figure 1.2953 is pretty meaningless, but if we know that this is an exchange rate for the US dollar against the euro on 23 November 2006, it starts making more sense. The various concepts are typically grouped into collections known as *concept schemes*.
- `Codelists`: Some of the concepts can be *free text* (such as a comment about a particular observation value) but others take their values from a *controlled vocabulary list* (such as, for example, a list of countries). These are known as *codelists* in SDMX.
- `Data structure definitions`: All the concepts that describe a particular domain (such as exchange rates or inflation) are grouped into a *data structure definition (DSD)*. In a DSD, concepts are divided into *dimensions* and *attributes*. Dimensions, when combined, allow to uniquely *identify* statistical data. Attributes on the other hand do not help identifying statistical data, but add useful information (like the unit of measure or the number of decimals). In order to perform granular data queries, one must know the concepts that are used as dimensions, as well as their allowed values (as defined in the codelists).
- `Dataflows`: Dataflows represent the data that cover a particular domain (such as, for example, balance of payments). A *dataflow* provides a reference to the data structure definition that applies for a particular domain, thereby indicating how the data for that domain will look like.
- `Organisation schemes`: Organisations that play a role in a particular statistical context are defined in *organisation schemes*. The type of organisation scheme will depend on the role played by a particular organisation (*data consumer*, *data provider*, *maintenance agency*). Organisations that define the metadata used in a particular context are known as maintenance agencies and are grouped together in an agency scheme.

Structure queries in SDMX allow you to retrive structural metadata at various levels of granularity, from all structural metadata available in the source to a single code from a particular version of a particular codelist maintained by a particular agency.

## Syntax

        protocol://ws-entry-point/structure/{artefactType}/{agencyID}/{resourceID}/{version}?{detail}&{references}

For item schemes, an additional path parameter (itemID) is permissible.

        protocol://ws-entry-point/structure/{artefactType}/{agencyID}/{resourceID}/{version}/{itemID}?{detail}&{references}

Parameter | Type | Description | Default | Multiple values?
--- | --- | --- | --- | ---
artefactType | One of the following types: datastructure, metadatastructure, categoryscheme, conceptscheme, codelist, hierarchy, hierarchyassociation, valuelist, organisationscheme, agencyscheme, dataproviderscheme, dataconsumerscheme, organisationunitscheme, dataflow, metadataflow, reportingtaxonomy, provisionagreement, structuremap, representationmap, conceptschememap,categoryschememap, organisationschememap, process, categorisation, dataconstraint, metadataconstraint, transformationscheme, rulesetscheme, userdefinedoperatorscheme, customtypescheme, namepersonalisationscheme, vtlmappingscheme | The type of structural metadata to be returned. | * | No
agencyID | A string compliant with the SDMX *common:NCNameIDType* | The agency maintaining the artefact to be returned. It is possible to set more than one agency, using `,` as separator (e.g. BIS,ECB). | * | Yes
resourceID | A string compliant with the SDMX *common:IDType* | The id of the artefact to be returned. It is possible to set more than one id, using `,` as separator (e.g. CL_FREQ,CL_CONF_STATUS). | * | Yes
version | A string compliant with the SDMX *semantic versioning* rules | The version of the artefact to be returned. It is possible to set more than one version, using `,` as separator (e.g. 1.0.0,2.1.7). | ~ | Yes
itemID | A string compliant with the SDMX *common:NestedNCNameIDType* for conceptscheme and agencyscheme, any type for valuelist or with the SDMX *common:NestedIDType* in all other cases | The id of the item to be returned. It is possible to set more than one id, using `,` as separator (e.g. A,Q,M). **This parameter is valid for item schemes only** | * | Yes
detail | *String* | This attribute specifies the desired amount of information to be returned. For example, it is possible to instruct the web service to return only basic information about the maintainable artefact (i.e.: id, agency id, version and name). Most notably, items of item schemes will not be returned (for example, it will not return the codes in a code list query). Possible values are: (1) `full`: all available information for all returned artefacts should be returned. Returned extended codelists are to be resolved, i.e. include all inherited codes, and must not include the ExtendedBy property. As the inherited codelists must be resolved, they should not be returned a second time as separated codelists. (2) `allstubs`: all returned artefacts should be returned as stubs, i.e. only containing identification information and the artefacts' name. (3) `referencestubs`: same as full with the exception that referenced artefacts should be returned only as stubs, i.e. only containing identification information and the artefacts' name. (4) `allcompletestubs`: all returned artefacts should be returned as complete stubs, i.e. only containing identification information, the artefacts' name, description, annotations and isFinal information. (5) `referencecompletestubs`: same as full with the exception that referenced artefacts should be returned as complete stubs, i.e. only containing identification information, the artefacts' name, description, annotations and isFinal information. (6) `referencepartial`: same as full with the exception that referenced item schemes should only include items used by the artefact to be returned. For example, a concept scheme would only contain the concepts used in a DSD, and its isPartial flag would be set to true. Likewise, if a dataflow has been constrained, then the codelists referenced by the DSD referenced by the dataflow should only contain the codes allowed by the content constraint. (7) `raw`: same as full with the exception that the returned extended codelists are not resolved and must include the ExtendedBy property, and if referenced codelists or descendants are to be returned then they include also all inherited codelists. (8) `partialraw`: same as raw with the exception that referenced item schemes, also including returned inherited codelists, should only include items used by the artefact to be returned, similar to the value 'referencepartial' | **full** | No
references | *String* | This attribute instructs the web service to return (or not) the artefacts referenced by the artefact to be returned (for example, the code lists and concepts used by the data structure definition matching the query), as well as the artefacts that use the matching artefact (for example, the dataflows that use the data structure definition matching the query). Possible values are: `none` (no references will be returned), `parents` (the artefacts that use the artefact matching the query), `parentsandsiblings` (the artefacts that use the artefact matching the query, as well as the artefacts referenced by these artefacts), `ancestors` (the artefacts that use the artefact matching the query, up to any level), `children` (artefacts referenced by the artefact to be returned), `descendants` (references of references, up to any level, will also be returned), `all` (the combination of parentsandsiblings and descendants). In addition, a `concrete type of resource` may also be used (for example, references=codelist). | **none** | Yes

### Notes about using `itemID`

As mentioned, `itemID` can be used for item scheme queries only! These are:

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

Although it is not following the *item scheme* pattern, the *valuelist* is also a collection, i.e. a collection of values.

If `itemID` is set and is a *top-level* id (e.g.: Code A (Annual) in the Frequency Codelist), and such an item exists in the matching item scheme, the item scheme returned should contain only the matching item and its `isPartial` parameter should be set to `true`.

If `itemID` is set and is a *nested* id (e.g.: Category A.1.1, belonging to Category A.1, belonging to Category A in a Category Scheme), and such an item exists in the matching item scheme, the item scheme returned should contain the matching item and its ancestors, and its `isPartial` parameter should be set to `true`.

The `detail` parameter allows specifying the amount of details to be retrieved. When `itemID` is used, the full details of the items requested are the following:

- Identification
- Names
- Descriptions
- Annotations
- Other details except Items

Since details are not exactly the same for all Item Schemes, the following table provides these details per Artefact type (`@` is used for XML attributes and `/` for XML elements).

| Item | flat/nested | Details in Item Query |
| ---- | ----------- | --------------------- |
| Code | flat | @id, @urn, @uri, /Annotations, /Name, /Description, /Parent |
| Concept | flat | @id, @urn, @uri, /Annotations, /Name, /Description, /Parent, /CoreRepresentation, /ISOConceptReference |
| Agency | flat | @id, @urn, @uri, /Annotations, /Name, /Description, /Contact |
| DataProvider | flat | @id, @urn, @uri, /Annotations, /Name, /Description, /Contact |
| DataConsumer | flat | @id, @urn, @uri, /Annotations, /Name, /Description, /Contact |
| OrganisationUnit | flat | @id, @urn, @uri, /Annotations, /Name, /Description, /Parent, /Contact |
| Category | nested | @id, @urn, @uri, /Annotations, /Name, /Description |
| ReportingCategory | nested | @id, @urn, @uri, /Annotations, /Name, /Description, /StructuralMetadata, /ProvisioningMetadata |

While most Item Schemes are flat, hence the above table is easy to interpret, for Category Schemes and Reporting Taxonomies (which have a nested structure) the above table indicates that any child Item(s) of the Item(s) requested must not be included in an Item Query request.

Further to the above, the reference resolution mechanism will be applied to all items returned. For example, querying for a category which has two ancestors in the hierarchy of the category scheme, will result into returning three categories (the requested one and its ancestors), as well as all references of those three categories.

### Applicability and meaning of references (including referencepartial)

The table below lists the 1st level artefacts (one level up, one level down) that will be returned if the references parameter is set to `all`. Artefacts referenced by the matching artefact are displayed in regular style, while the artefacts that reference the matching artefact are displayed in *Italic*.

Maintainable artefact | Artefacts returned
--- | ---
AgencyScheme | All
Categorisation | All
CategoryScheme | *Categorisations*, *Process*, *CategorySchemeMap*, AgencyScheme
CategorySchemeMap | *Categorisation*, *Process*, CategoryScheme, ReportingTaxonomy, AgencyScheme
Codelist | *Categorisation*, *Process*, *Hierarchy*, *ConceptScheme*, *DataStructureDefinition*, *MetadataStructureDefinition*, *RepresentationMap*, AgencyScheme, *VtlMappingScheme*
ConceptScheme | *Categorisation*, *Process*, Codelist, *DataStructureDefinition*, *MetadataStructureDefinition*, *ConceptSchemeMap*, AgencyScheme, *VtlMappingScheme*
ConceptSchemeMap | *Categorisation*, *Process*, ConceptScheme, AgencyScheme
Constraint | *Categorisation*, *Process*, DataProviderScheme, DataStructureDefinition, Dataflow, MetadataStructureDefinition, Metadataflow, ProvisionAgreement, AgencyScheme
DataConsumerScheme | *Categorisation*, *Process*, *MetadataStructureDefinition*, *OrganisationSchemeMap*, AgencyScheme
Dataflow | *Categorisation*, *Process*, *Constraint*, DataStructureDefinition, *ProvisionAgreement*, *ReportingTaxonomy*, *StructureMap*, AgencyScheme, *VtlMappingScheme*
DataProviderScheme | *Categorisation*, *Process*, *Constraint*, *ProvisionAgreement*, *MetadataStructureDefinition*, *OrganisationSchemeMap*, AgencyScheme
DataStructureDefinition | *Categorisation*, *Process*, Codelist, ConceptScheme, *Constraint*, *Dataflow*, *StructureMap*, Valuelist, AgencyScheme
Hierarchy | *HierarchyAssociation*, *Categorisation*, *Process*, Codelist, AgencyScheme
HierarchyAssociation | All
Metadataflow | *Categorisation*, *Process*, *Constraint*, MetadataStructureDefinition, *ProvisionAgreement*, *ReportingTaxonomy*, AgencyScheme
MetadataStructureDefinition | *Categorisation*, *Process*, ConceptScheme, Codelist, Valuelist, DataProviderScheme, DataConsumerScheme, AgencyScheme, OrganisationUnitScheme, *Constraint*, *Metadataflow*, AgencyScheme
OrganisationSchemeMap | *Categorisation*, *Process*, DataProviderScheme, DataConsumerScheme, OrganisationUnitScheme, AgencyScheme
OrganisationUnitScheme | *Categorisation*, *Process*, *Constraint*, *MetadataStructureDefinition*, *OrganisationSchemeMap*, AgencyScheme
Process | All
ProvisionAgreement | *Categorisation*, *Process*, DataProviderScheme, Dataflow, Metadataflow, *Constraint*, AgencyScheme
ReportingTaxonomy | *Categorisation*, *CategorySchemeMap*, *Process*, Dataflow, Metadataflow, AgencyScheme
RepresentationMap | *Categorisation*, *Process*, Codelist, Valuelist, AgencyScheme
StructureMap | *Categorisation*, *Process*, DataStructureDefinition, Dataflow, AgencyScheme
CustomTypeScheme | AgencyScheme, _Categorisation_, _TranformationScheme_, AgencyScheme
NamePersonalisationScheme | AgencyScheme, _Categorisation_, _TranformationScheme_, AgencyScheme
RulesetScheme | AgencyScheme, _Categorisation_, _TranformationScheme_, VtlMappingScheme, AgencyScheme, AgencyScheme
TranformationScheme | AgencyScheme, _Categorisation_, CustomTypeScheme, NamePersonalisationScheme, RulesetScheme, UserDefinedOperatorScheme, VtlMappingScheme, AgencyScheme
UserDefinedOperatorScheme | AgencyScheme, _Categorisation_, _TranformationScheme_, VtlMappingScheme, AgencyScheme
Valuelist | *Categorisation*, *Process*, *ConceptScheme*, *DataStructureDefinition*, *MetadataStructureDefinition*, *RepresentationMap*, *VtlMappingScheme*, AgencyScheme
VtlMappingScheme | _Categorisation_, Codelist, ConceptScheme, Dataflow, _RulesetScheme_, _TranformationScheme_, _UserDefinedOperatorScheme_, Valuelist, AgencyScheme

Also, when returning only partial references (via `referencepartial`), the filtering applies to any dependency, regardless of its level. The table below describes the impact of using `referencepartial` on the referenced item schemes.

| Maintainable artefact	| Meaning of detail=referencepartial |
| --------------------- | ---------------------------------- |
| AgencyScheme | Only the agencies maintaining the returned structures should be included in the returned agency scheme(s) |
| CategoryScheme | Only the categories referenced by the returned categorisations or structure sets should be included in the returned category scheme(s) |
| Codelist | Only the codes referenced by the returned categorisations, constraints, hierarchies or structure sets should be included in the returned codelist(s) |
| ConceptScheme | Only the concepts referenced by the returned categorisations, data structure definitions, metadata structure definitions or structure sets should be included in the returned concept scheme(s) |
| DataConsumerScheme | Only the data consumers referenced by the returned categorisations, metadata structure definitions or structure sets should be included in the returned data consumer scheme(s) |
| DataProviderScheme | Only the data providers referenced by the returned categorisations, constraints, provision agreements, metadata structure definitions or structure sets should be included in the returned data provider scheme(s) |
| HierarchicalCodelist | Only the hierarchies referenced by the returned categorisations or structure sets should be included in the returned hierarchical codelist(s) |
| OrganisationUnitScheme | Only the organisation units referenced by the returned categorisations, constraints, metadata structure definitions or structure sets in the returned organisation unit scheme(s) |
| ReportingTaxonomy | Only the reporting categories referenced by the returned categorisations or structure sets should be included in the reporting taxonomy |

For example, a dataflow only references a data structure definition and, in addition, it may be referenced by one or more constraints. In case `references` is set to `all` and `detail` is set to `referencepartial`, the following should apply to the returned item schemes:

- The concept scheme(s) should only include the concepts referenced by the data structure referenced by target dataflow;
- The codelist(s) should only include the codes referenced by the constraint(s) referencing the target dataflow;
- The data provider schemes(s) should only include the data provider(s) that are linked to the target dataflow via a provision agreement
- The agency scheme(s) should only include the agencies maintaining the structural metadata to be returned (dataflow, constraints, data structures, etc.)

## Response types

The following media types can be used with _structure_ queries:

- **application/vnd.sdmx.structure+json;version=2.0.0**
- application/vnd.sdmx.structure+json;version=1.0.0
- application/vnd.sdmx.structure+xml;version=3.0.0
- application/vnd.sdmx.structure+xml;version=2.1

The default format is highlighted in **bold**.

## Examples of queries

- To retrieve version 1.0.0 of the DSD with id ECB_EXR1 maintained by the ECB, as well as the code lists and the concepts used in the DSD:

        https://ws-entry-point/structure/datastructure/ECB/ECB_EXR1/1.0.0?references=children&detail=referencepartial

- To retrieve the latest stable version of the DSD with id ECB_EXR1 maintained by the ECB, without the code lists and concepts of the DSD:

        https://ws-entry-point/structure/datastructure/ECB/ECB_EXR1/+

- To retrieve the latest version of all DSDs maintained by the ECB, as well as the dataflows using these DSDs:

        https://ws-entry-point/structure/datastructure/ECB?references=dataflow

- To retrieve the latest version of all code lists maintained by all maintenance agencies, but without the codes:

        https://ws-entry-point/structure/codelist?detail=allstubs

- To retrieve, as stubs, the latest version of all maintainable artefacts maintained by the ECB:

        https://ws-entry-point/structure/all/ECB?detail=allstubs

- To retrieve the category PRICES of the DOMAINS category scheme maintained by the ECB, as well as the categorisations referencing that category:

        https://ws-entry-point/structure/categoryscheme/ECB/DOMAINS/latest/PRICES?references=categorisation

- To retrieve the latest version of the CL_FREQ codelists maintained by the BIS or the ECB:

        https://ws-entry-point/structure/codelist/BIS,ECB/CL_FREQ