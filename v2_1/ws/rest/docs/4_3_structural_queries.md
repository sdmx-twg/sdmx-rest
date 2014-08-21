## Structural Metadata Queries

### Resources

The following resources are defined:
- datastructure<sup>[1](#fn-1)</sup>
- metadatastructure<sup>[2](#fn-2)</sup>
- categoryscheme
- conceptscheme
- codelist
- hierarchicalcodelist
- organisationscheme<sup>[3](#fn-3)</sup>
- agencyscheme<sup>[4](#fn-4)</sup>
- dataproviderscheme
- dataconsumerscheme
- organisationunitscheme
- dataflow
- metadataflow
- reportingtaxonomy
- provisionagreement
- structureset
- process
- categorisation
- contentconstraint
- attachmentconstraint
- structure<sup>[5](#fn-5)</sup>


### Parameters

#### Parameters used for identifying a resource

The following parameters are used for identifying resources:

Parameter | Type | Description
--- | --- | ---
agencyID | *string compliant with the SDMX common:NCNameIDType* | The agency maintaining the artefact to be returned.
resourceID | *string compliant with the SDMX common:IDType* | The id of the artefact to be returned. 
version | *string compliant with the SDMX common:VersionType* | The version of the artefact to be returned.

The parameters mentioned above are specified using the following syntax:

    protocol://ws-entry-point/resource/agencyID/resourceID/version
    
Furthermore, some keywords may be used:

Keywords | Scope | Description
--- | --- | ---
all<sup>[6](#fn-6)</sup> | agencyID | Default, returns artefacts maintained by any maintenance agency<sup>[7](#fn-7)</sup>
all | resourceID | Default, returns all resources of the type defined by the resource parameter<sup>[7](#fn-7)</sup>
all | version | Returns all versions of the resource
latest | version | Default, returns the latest version in production of the resource<sup>[7](#fn-7)</sup>

The following rules apply:

- If no version is specified, the version currently used in production should
be returned. It is therefore equivalent to using the keyword "latest".
-  If no agencyID is specified, the matching artefacts maintained by any
maintenance agency should be returned. It is therefore equivalent to using the
keyword "all"<sup>[8](#fn-8)</sup>.
- If no resourceID is specified, all matching artefacts (according to the other
criteria used) should be returned. It's is therefore equivalent to using the
keyword "all".
- If no parameters are specified, the "latest" version of "all" resources of
the type identified by the resource parameter, maintained by any maintenance
agency should be returned.


#### Parameters used to further describe the desired results

The following parameters are used to further describe the desired results, once the resource has been identified. As mentioned in 3.2, these parameters appear in the query string part of the URL.

Parameter | Type | Description | Default
--- | --- | --- | ---
detail | *String* | This attribute specifies the desired amount of information to be returned. For example, it is possible to instruct the web service to return only basic information about the maintainable artefact (i.e.: id, agency id, version and name). Most notably, items of item schemes will not be returned (for example, it will not return the codes in a code list query). Possible values are: *allstubs* (all artefacts should be returned as stubs<sup>[9](#fn-9)</sup>), *referencestubs* (referenced artefacts should be returned as stubs<sup>[10](#fn-10)</sup>) and *full* (all available information for all artefacts should be returned<sup>[11](#fn-11)</sup>). | full
references | *String* | This attribute instructs the web service to return (or not) the artefacts referenced by the artefact to be returned (for example, the code lists and concepts used by the data structure definition matching the query), as well as the artefacts that use the matching artefact (for example, the dataflows that use the data structure definition matching the query). Possible values are: *none* (no references will be returned), *parents* (the artefacts that use the artefact matching the query), *parentsandsiblings* (the artefacts that use the artefact matching the query, as well as the artefacts referenced by these artefacts), *children* (artefacts referenced by the artefact to be returned), *descendants* (references of references, up to any level, will also be returned), *all* (the combination of parentsandsiblings and descendants). In addition, a concrete type of resource, as defined in 3.3.1, may also be used (for example, references=codelist). | none

#### Applicability and meaning of references attribute

The table below lists the artefacts that will be returned if the references
parameter is set to "all".

Maintainable artefact | Artefacts returned
--- | ---
Categorisation | All
CategoryScheme | Categorisations
Codelist | HierarchicalCodelist
ConceptScheme |  Codelists
Constraint | OrganisationSchemes, DataProviderSchemes, DataStructureDefinitions, Dataflows, MetadataStructureDefinitions, Metadataflows, ProvisionAgreements
Dataflow | Constraints, DataStructureDefinitions, ProvisionAgreements, ReportingTaxonomies, StructureSets
DataProviderScheme | Constraint, ProvisionAgreement
HierarchicalCodelist | Codelists
DataStructureDefinition | Codelists, ConceptSchemes, Constraints, Dataflows, StructureSets
Metadataflow | Constraints, MetadataStructureDefinitions, ProvisionAgreements, ReportingTaxonomies, StructureSets
MetadataStructureDefinition | ConceptSchemes, Codelists, DataProviderSchemes, DataConsumerSchemes, AgencySchemes, OrganisationSchemes, Constraints, Metadataflows, StructureSets
OrganisationScheme | None
Process | All
ProvisionAgreement | DataProviderSchemes, Dataflows, Metadataflows
ReportingTaxonomy | Dataflows, Metadataflows
StructureSet | DataStructureDefinitions, MetadataStructureDefinitions, CategorySchemes, DataProviderSchemes, DataConsumerSchemes, AgencySchemes, OrganisationSchemes, ConceptSchemes, Codelists, HierarchicalCodelists

### Examples

1. To retrieve version 1.0 of the DSD with id ECB_EXR1 maintained by the ECB, as well as the code lists and the concepts used in the DSD:

        http://ws-entry-point/datastructure/ECB/ECB_EXR1/1.0?references=children

2. To retrieve the latest version in production of the DSD with id ECB_EXR1 maintained by the ECB, without the code lists and concepts of the DSD:

        http://ws-entry-point/datastructure/ECB/ECB_EXR1

3. To retrieve all DSDs maintained by the ECB, as well as the dataflows using these DSDs:

        http://ws-entry-point/datastructure/ECB?references=dataflow

4. To retrieve the latest version in production of all code lists maintained by all maintenance agencies, but without the codes:

        http://ws-entry-point/codelist?detail=allstubs

5. To retrieve, as stubs, the latest version in production of all maintainable artefacts maintained by the ECB:

        http://ws-entry-point/structure/ECB?detail=allstubs

<a name="fn-1"></a>[1]: This has been shortened from DataStructureDefinition to allow for shorter URLs.

<a name="fn-2"></a>[2]: This has been shortened from MetadataStructureDefinition to allow for shorter URLs.

<a name="fn-3"></a>[3] The organisationscheme resource can be used whenever the role played by the organisation schemes (e.g. maintenance agencies) is not known/relevant.

<a name="fn-4"></a>[4]: For 3 of the subtypes of OrganisationScheme (AgencyScheme, DataProviderScheme and DataConsumerScheme), the id and version parameters have fixed values. See Section 03 of the SDMX information model document for additional information.

<a name="fn-5"></a>[5]: This type can be used to retrieve any type of structural metadata matching the supplied parameters.

<a name="fn-6"></a>[6]: As "all" is a reserved keyword in the SDMX RESTful API, it is recommended not to use it as an identifier for agencies, resources or a specific version.

<a name="fn-7"></a>[7]: Default, if parameter not specified

<a name="fn-8"></a>[8]: This would potentially return more than one artefact, if different agencies give the same identifier to a resource (for example, http://ws-entry-point/codelist/all/CL_FREQ, could return more than one codelist if more than one agency is maintaining a codelist with id "CL_FREQ").

<a name="fn-9"></a>[9]: The equivalent in SDMX-ML query is: Stub at the query level and Stub at the reference level.

<a name="fn-10"></a>[10]: The equivalent in SDMX-ML query is: Full at the query level and Stub at the reference level.

<a name="fn-11"></a>[11]: The equivalent in SDMX-ML query is: Full at the query level and Full at the reference level.

