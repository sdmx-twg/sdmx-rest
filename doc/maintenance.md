# Maintaining Structural and Reference Metadata via the SDMX REST API

The following sections describe the foreseen **HTTP methods** for the maintenance of SDMX Structural and Reference Metadata via the SDMX REST API.  

Note that maintenance actions for data are defined by the `Action` property of the related Datasets. See here for details:
- [SDMX-ML](https://github.com/sdmx-twg/sdmx-ml/blob/master/documentation/SDMX_3-1_SECTION_3A_PART_IV_DATA.md#41-Data-Actions)
- [SDMX-JSON](https://github.com/sdmx-twg/sdmx-json/blob/master/data-message/docs/1-sdmx-json-field-guide.md#dataset)
- [SDMX-CSV](https://github.com/sdmx-twg/sdmx-csv/blob/master/data-message/docs/sdmx-csv-field-guide.md#column-content-all-rows-after-header)

-------------

**Content:**

- [Maintaining Structural Metadata](#maintaining-structural-metadata)
  - [CREATE](#create)
    - [Artefact (Structure)](#artefact-structure)
      - [Client](#client)
      - [Server](#server)
      - [Response](#response)
  - [UPDATE](#update)
    - [Replace full Artefacts](#replace-full-artefacts)
      - [Client](#client-1)
      - [Server](#server-1)
      - [Response](#response-1)
    - [Partially update Item Schemes](#partially-update-item-schemes)
      - [Client](#client-2)
      - [Server](#server-2)
      - [Response](#response-2)
  - [DELETE](#delete)
      - [Client](#client-3)
      - [Server](#server-3)
      - [Response](#response-3)
- [Maintaining Reference Metadata](#maintaining-reference-metadata)
  - [CREATE, UPDATE or DELETE a single Reference Metadataset](#create-update-or-delete-a-single-reference-metadataset)
  - [CREATE or UPDATE multiple Reference Metadatasets](#create-or-update-multiple-reference-metadatasets)
  - [Client](#client-4)
  - [Server](#server-4)
- [ANNEX I: RegistryInterface messages for Subscription and Registration](#annex-i-registryinterface-messages-for-subscription-and-registration)
  - [Introduction](#introduction)
  - [Retrieving Subscriptions and Registrations](#retrieving-subscriptions-and-registrations)
  - [Maintaining Subscriptions and Registrations](#maintaining-subscriptions-and-registrations)
- [ANNEX II: Structure response message](#annex-ii-structure-response-message)
- [ANNEX III: Nested Items](#annex-iii-nested-items)
- [ANNEX IV: ANNEX IV: Summary of HTTP response codes for structure management](#annex-iv-summary-of-http-response-codes-for-structure-management)
- [ANNEX V: Examples for structure management](#annex-v-examples-for-structure-management)
  - [Difference between replace and partially update](#difference-between-replace-and-partially-update)

---------------

## Maintaining Structural Metadata

### CREATE

Inserting or creating new SDMX Structural Metadata is detailed in this secion.

#### Artefact (Structure)

Submitting SDMX Artefacts in bulk, either of the same or of different types, is achieved with an HTTP `POST` method.
Creating new Artefact(s) may be issued by:
- `POST` one or more Maintainable Artefacts under the proper resource type, e.g. for Codelists: `/structure/codelist/`
- `POST` one or more Maintainable Artefacts under the abstract structure resource type, e.g. `/structure/`

##### Client

In order to create Artefacts, the client:

- MAY set the `Accept` header to indicate the preferred response format;
- MUST set the `Content-type` header according to the format of the submitted Artefact(s);
- MUST include in the request body, one or more Maintainable Artefacts in the SDMX format indicated in the `Content-type` header and of the SDMX type indicated in the resource, i.e.:
  - any set of Maintainable Artefacts under resource `/structure/`
  - a set of specific type of Maintainable Artefacts under the corresponding resource type, e.g. for Codelists: `/structure/codelist/`

##### Server

In response to Artefact(s) creation, the server:

- MUST return `201` upon successful creation (or `207` for partial success);
- MUST return a `SubmitStructureResponse` message with the result of the action(s), according to the `Accept` header, or the default, if the `Accept` type is not supported (currently available only in SDMX-ML format);
- MUST set the `Content-type` according to the returned format;
- MAY set the `Location` header to point to the created/primary resource/Artefact (only one instance is allowed);

##### Response

The `SubmitStructureResponse` message must be returned in any case (success, partial success, failure).
This is defined as part of the `RegistryInterface` messages.
The details of the message are explained in section **Structure response message**, below.

Especially when different results occur on the Artefacts (e.g. partial success), the server should act as follows:
- Return a multi-status return code (like `207`);
- Return a `JSON` or `XML` message with the details of the result (currently available only in SDMX-ML);

| Method | Exists | Versioning respected | Is Referenced | References exist/provided | Response Code |
|---|---|---|---|---|---|
| POST | F | T | - | T | `201` (successful) or `207` (partially successful) |
| POST | F | T | - | F | `409` missing references |
| POST | F | F | - | I | `409` failure to respect the versioning rules |

<sup>T: True, F: False, I: Irrelevant, -: Not applicable</sup>

<sup>NOTE: The rules for versioning in SDMX 3.0 are based on Semantic Versioning. In addition, Artefacts with no version are supported. SDMX 3.0 does not specify rules for legacy versioning - i.e. versioning prior to SDMX 3.0. The versioning rules in that case are up to the organisation using them.</sup>

### UPDATE

Updating existing SDMX Structural Metadata is detailed in this section.

#### Replace full Artefacts

Following the current SDMX practices, updating (replacing) means providing the new version of any Maintainable Artefact.
According to RFC7231, `PUT` is the proper way to update a resource, as identified by the URL, but `POST` may also be used in case more than one resources need to be updated.

The `POST` method may be used like this:

- under `/structure` for different types of Maintainable Artefacts, i.e. an SDMX Structure message; 
- under `/structure/{maintainable}` for Maintainable Artefacts of type `{maintainable}`, i.e. an SDMX Structure message including only a specific type of Structures; e.g. an SDMX message with one or more Dataflows, under `/structure/dataflow`;

The `PUT` method may be used like this:

- under `/structure/{maintainable}/{identifier}` for the Maintainable Artefact of type `{maintainable}`, identified by `{identifier}`, i.e. an SDMX DSD message with `ESTAT:NA_MAIN(1.1)` under `/structure/datastructure/ESTAT/NA_MAIN/1.1`

The result, following the current practices of updates in SDMX, is to **completely replace** the identified Artefact, unless a Maintainable Artefact has the `isPartialLanguage` property set to `true`, in which case only the included languages are added or updated and other languages are not changed.

##### Client

In order to update Artefact(s), the client:

- MAY set the `Accept` header to indicate the preferred response format;
- MUST set the `Content-type` header according to the format of the submitted Artefact(s);
- MUST include in the request body, one or more Maintainable Artefacts in the SDMX format indicated in the `Content-type` header and of the SDMX type indicated in the resource, i.e.:
- for `POST`:
  - any set of Maintainable Artefacts under resource `/structure/`
  - one or more Maintainable Artefacts of the specific type under the corresponding resource type, e.g. Dataflows under `/structure/dataflow/`
- for `PUT`:
  - one Maintainable Artefact, of the specific type under the corresponding resource type and identified according to the resource parameters, e.g., DSD `ESTAT:NA_MAIN(1.1)` under `/structure/datastructure/ESTAT/NA_MAIN/1.1`

##### Server

In response to Artefact(s) update, the server:

- MUST respond with `200` in case of successful update;
- MUST return a `SubmitStructureResponse` message with the result of the action(s), according to the `Accept` header, or the default, if the `Accept` type is not supported (currently available only in SDMX-ML);
- MUST set the `Content-type` according to the returned format;
- MUST respond with `422` in case of resource type mismatch, i.e. the resource type identified in the URL does not match the Artefact type(s) of the included SDMX Artefact(s), or the identification of the Artefact (in case of `PUT`) does not match the resource path parameters.
- MUST respond with `404` if the resource was not found;

##### Response

The `SubmitStructureResponse` message must be returned in any case (success, partial success, failure).
This is defined as part of the `RegistryInterface` messages.
The details of the message are explained in section **Structure response message**, below.

The following matrix summarises the returned `HTTP` response codes.

| Method | Exists | Versioning respected | Is Referenced | References exist/provided | Return Code |
|---|---|---|---|---|---|
| PUT/POST | T | T | F | T | `200` (successful) or `207` (partially successful)  |
| PUT/POST | T | T | F | F | `409` |
| PUT/POST | T | T | T | T | `200` if update is possible - `409` if references would break |
| PUT/POST | T | F | - | - | `409` in case of changes that violate the versioning requirements |
| PUT/POST | - | - | - | - | `422` see section **Server** |
| PUT | F | - | - | - | `404` |

<sup>T: True, F: False, I: Irrelevant, -: Not applicable</sup>

#### Partially update Item Schemes

In the case of Item Schemes, it is possible to submit a partial Item Scheme, including the Items that must be updated.
The usage of `POST` and `PUT` is the same as in the case of full replacement. The only difference is that the submitted Items Schemes to be partially updated (i.e. not completely replaced) must include the flag `isPartial="true"`.

In this case, any Item included in the submitted partial Item Scheme:
- fully replaces the Item, if it existed in the stored Item Scheme; the Item stays in the same position;
- is added as a new Item, if it did not exist
  - in a selected position, if it is submitted relatively to existing Items;
  - else, at the end of the Item Scheme.

For more details on nested Items (like Categories), please refer to section **Nested Items** below.

In addition to the Items, the Item Scheme details are also updated accordingly, i.e. the names, description, annotations and any other attributes.
Names and Descriptions are replaced if they exist for the submitted language (i.e. the value of their `lang` attribute), or simply added, if new. Annotations and other Item Scheme attributes are fully replaced.

##### Client

In order to partially update an Item Scheme, the client:

- MAY set the `Accept` header to indicate the preferred response format;
- MUST set the `Content-type` header according to the format of the submitted Item Scheme(s);
- MUST include the flag `isPartial="true"` in the submitted Item Scheme(s);
- MUST include in the request body:
  - For `PUT`, **one** Item Scheme in the SDMX format indicated in the `Content-type` header and of the Item Scheme type indicated in the resource, e.g. a Concept Scheme under resource `/conceptscheme`;
  - For `POST`, one or more Item Schemes in the SDMX format indicated in the `Content-type` header and
    - of the Item Scheme type indicated in the resource, in case of a specific Item Scheme resource, e.g. a Codelist under resource `/structure/codelist/`
    - of any Item Scheme type, in case of the `/structure` resource.

##### Server

In response to an Item Scheme partial update, the server:

- MUST respond with `200` in case of successful update;
- MUST return a `SubmitStructureResponse` message with the result of the action, according to the `Accept` header, or the default, if the `Accept` type is not supported (currently available only in SDMX-ML);
- MUST set the `Content-type` according to the returned format;
- MUST respond with `422` in case of resource type mismatch, i.e. the artefact identified in the URL does not match either to the resource type or identification of the included SDMX Artefact.
- MUST respond with `404` if the resource was not found;

##### Response

The `SubmitStructureResponse` message must be returned in any case (success, partial success, failure).
This is defined as part of the `RegistryInterface` messages.
The details of the message are explained in section **Structure response message**, below.

The following matrix summarises the returned `HTTP` response codes.

| Method | Exists | Versioning respected | Is Referenced | Refs exist/ provided | Response Code |
|---|---|---|---|---|---|
| PUT/POST | T | T | F | T | `200` (successful) or `207` (partially successful)  |
| PUT/POST | T | T | F | F | `409` |
| PUT/POST | T | T | T | T | `200` if update is possible - `409` if references would break |
| PUT/POST | T | F | - | - | `409` in case of changes that violate the versioning requirements |
| PUT/POST | - | - | - | - | `422` see section **Server** |
| PUT | F | - | - | - | `404` |

<sup>T: True, F: False, I: Irrelevant, -: Not applicable</sup>


### DELETE

Always concerns one Maintainable Artefact or one Item. 
For example:
- A fully identified Maintainable Artefact, e.g. `/structure/codelist/SDMX/CL_FREQ/1.0`
- A fully identified Item, e.g. `/structure/codelist/SDMX/CL_FREQ/1.0/M`

In case the deleted Item was acting as a parent to other Item(s), then the server should make sure that:
- in case of flat Item Schemes the children become orphans, i.e. their parent is removed;
- in case of nested Item Schemes, all children are also deleted.

##### Client

In order to delete an Artefact, the client:

- MAY set the `Accept` header to indicate the preferred response format;
- MUST fully identify exactly **one** Maintainable Artefact or **one** Item, by means of the proper URL;

##### Server

In response to an Artefact deletion, the server:

- MUST respond with `200` in case of successful deletion;
- MUST return a `SubmitStructureResponse` message with the result of the action, according to the `Accept` header, or the default, if the `Accept` type is not supported (currently available only in SDMX-ML);
- MUST respond with `404` if the resource was not found;

##### Response

The `SubmitStructureResponse` message must be returned in any case (success, failure).
This is defined as part of the `RegistryInterface` messages.
The details of the message are explained in section **Structure response message**, below.

The following matrix summarises the returned `HTTP` response codes.

| Method | Exists | Versioning respected | Is Referenced | References exist/ provided | Response Code |
|---|---|---|---|---|---|
| DELETE | F | - | - | - | `404` |
| DELETE | T | F | F | I | `200` |
| DELETE | T | T | I | I | `409` when trying to delete a stable artefact, a deprecation strategy may be followed |
| DELETE | T | F | T | I | `409` |

<sup>T: True, F: False, I: Irrelevant, -: Not applicable</sup>


## Maintaining Reference Metadata

This section fully applies the [RFC7231 standard](https://www.rfc-editor.org/rfc/rfc7231#section-4.3) for the meaning of HTTP methods. 

### CREATE, UPDATE or DELETE a single Reference Metadataset

Inserting (creating a new), updating (an existing) and deleting an individual (maintainable) SDMX Reference Metadataset is detailed in this secion.

Use the `PUT` method on the related target resource `/metadata/metadataset/{identifier}` to **create** or **update** an individual Reference Metadataset, i.e. an SDMX  message containing the representation of the `ESTAT:MDS_EXAMPLE(1.0)` Reference Metadataset under `/metadata/metadataset/ESTAT/MDS_EXAMPLE/1.0`.  
The Reference Metadataset is created if it doesn't exist yet in the underlying registry. It is updated if it already exists. If the Reference Metadataset has the `isPartialLanguage` property set to `true` then only the included languages are added or updated and other languages are not changed, else the Reference Metadataset is **completely replaced**.

Use the `DELETE` method on the related target resource `/metadata/metadataset/{identifier}` to **delete** an individual Reference Metadataset, i.e. the `ESTAT:MDS_EXAMPLE(1.0)` Reference Metadataset under `/metadata/metadataset/ESTAT/MDS_EXAMPLE/1.0`.

### CREATE or UPDATE multiple Reference Metadatasets

Inserting (creating new) and updating (completely replacing existing) multiple (maintainable) SDMX Reference Metadatasets is detailed in this secion.

Use the `POST` method on the metadata resource `/metadata/metadataset` or simply on the metadataset resource `/metadata` to **create** or **update** multiple Reference Metadatasets, i.e. an SDMX message containing the representation of one or more Reference Metadatasets.  
All Reference Metadatasets that don't exist yet in the underlying registry are created, all others are updated. If a Reference Metadataset has the `isPartialLanguage` property set to `true` then only the included languages are added or updated and other languages are not changed, else the Reference Metadataset is **completely replaced**.

### Client

In order to create, update or delete Reference Metadatasets, the client:

- MAY set the `Accept` header to indicate the preferred response format;

In order to create or update Reference Metadatasets, the client:

- MUST set the `Content-type` header according to the format of the submitted Reference Metadataset(s);
- MUST include in the request body, one or more Reference Metadatasets in the SDMX format indicated in the `Content-type` header

### Server

In response to Reference Metadataset(s) creation, update or deletion, the server:

- MUST respond with `201` in case of successful creation (or `207` for multi-status);
- MAY set the `Location` header to point to the created Reference Metadataset (only one instance is allowed);
- MUST respond with `200` in case of successful update or deletion (or `207` for multi-status);
- MUST return a `SubmitStructureResponse` message with the result of the action(s) as defined for the `RegistryInterface` messages (success, partial success, failure), in the SDMX format requested by the `Accept` header, or the default, if the `Accept` type is not supported (currently available only in SDMX-ML format). This is especially important when varying results occur for different Reference Metadatasets. The details of the message are explained in section [**Structure response message**](#ANNEX-II-Structure-response-message);
- MUST set the `Content-type` according to the returned format (currently only in SDMX-ML format);
- MUST respond with `422` in case of semantic errors, i.e. the content submitted in the request body is not Reference Metadatsets, or the identification of the Reference Metadatset (in case of `PUT`) does not match the resource path parameters (or `207` for multi-status).
- MUST respond with `404` if the to be deleted Reference Metadataset was not found;

The following matrix summarises the returned `HTTP` response codes.

| Method | Exists | Versioning respected | Is Referenced | References exist/provided | Response Code |
|--------|--------|----------------------|---------------|---------------------------|---------------|
| PUT/POST | F    | T                    | -             | T                         | `201` (successful) \*   |
| PUT/POST | F    | T                    | -             | F                         | `409` (missing references) \* |
| PUT/POST | T    | T                    | F             | T                         | `200` (successful) \*  |
| PUT/POST | T    | T                    | F             | F                         | `409` (missing references) \* |
| PUT/POST | T    | T                    | T             | T                         | `200` if update is possible or `409` if references would break \* |
| PUT/POST | T/F  | F                    | I             | I                         | `409` in case of changes that violate the versioning requirements \* |
| PUT/POST | -    | -                    | -             | -                         | `422` (semantic error) \* |
| DELETE | F      | -                    | -             | -                         | `404` (not found) |
| DELETE | T      | F                    | F             | I                         | `200` (successful) |
| DELETE | T      | F                    | T             | I                         | `409` |
| DELETE | T      | T                    | I             | I                         | `409` when trying to delete a stable Reference Metadatset, a deprecation strategy may be followed |

(*) Whenever multiple Reference Metadatsets are created or updated through a POST and the outcome diverges between these Reference Metadatsets then the overall response code is `207` (multi-status).

<sup>T: True, F: False, I: Irrelevant, -: Not applicable</sup>  
<sup>NOTE: The rules for versioning in SDMX 3.0 are based on Semantic Versioning. SDMX 3.0 does not specify rules for legacy versioning - i.e. versioning prior to SDMX 3.0. The versioning rules in that case are up to the organisation using them.</sup>
<sup>NOTE: The previous message header property “DataSetAction” is deprecated for Reference Metadatasets and to be ignored if still present.</sup>


### ANNEX I: RegistryInterface messages for Subscription and Registration

Support for Subscriptions and Registrations are not yet part of the SDMX RESTful API.
Thus, an intermediate solution for supporting them in SDMX 3.0 (after the deprecation of the SOAP API) is needed.
This section describes how to use the RegistryInterface messages for submitting and retrieving Subscriptions and Registrations via the RESTful API.

#### Introduction

The usage of the RegistryInterface messages is foreseen via the POST verb, under resources:
- ```/rest/subscription``` for Subscriptions
- ```/rest/registration``` for Registrations
- ```/rest``` for any RegistryInterface message

The available ```RegistryInterface``` messages are the following:
- ```SubmitRegistrationsRequest```
- ```SubmitRegistrationsResponse```
- ```SubmitSubscriptionsRequest```
- ```SubmitRegistrationsResponse```
- ```QueryRegistrationRequest```
- ```QueryRegistrationResponse```
- ```QuerySubscriptionRequest```
- ```QuerySubscriptionResponse```

In addition, the above shall remain as part of the generic ```RegistryInterface``` message.


#### Retrieving Subscriptions and Registrations

For Subscriptions:
- The client may POST a ```QuerySubscriptionRequest``` or a ```RegistryInterface/QuerySubscriptionRequest``` message under resource ```/rest/subscription``` for retrieving Subscriptions. 
- As a result, a ```QuerySubscriptionResponse``` or a ```RegistryInterface/QuerySubscriptionResponse``` message will be returned as a response to describe the requested Subscriptions.

Similarly, for Registrations:
- The client may POST a ```QueryRegistrationRequest``` or a ```RegistryInterface/QueryRegistrationRequest``` message under resource ```/rest/registration``` for retrieving Registrations.
- As a result, a ```QueryRegistrationResponse``` or a ```RegistryInterface/QueryRegistrationResponse``` message will be returned as a response to describe the requested Registrations.

#### Maintaining Subscriptions and Registrations

For Subscriptions:
- The client may POST a ```SubmitSubscriptionsRequest``` or a ```RegistryInterface/SubmitSubscriptionsRequest``` message under resource ```/rest/subscription``` for inserting/updating/deleting Subscriptions. 
- As a result, a ```SubmitSubscriptionsResponse``` or a ```RegistryInterface/SubmitSubscriptionsResponse``` message will be returned as a response to describe the outcome of the insertion/update/deletion of Subscriptions.

Similarly, for Registrations:
- The client may POST a ```SubmitRegistrationsRequest``` or a ```RegistryInterface/SubmitRegistrationsRequest``` message under resource ```/rest/registration``` for inserting/updating/deleting Registrations. 
- As a result, a ```SubmitRegistrationsResponse``` or a ```RegistryInterface/SubmitRegistrationsResponse``` message will be returned as a response to describe the outcome of the insertion/update/deletion of Registrations.


### ANNEX II: Structure response message

The `SubmitStructureResponse` message must be returned in any case (success, partial success, failure).
This is defined as part of the `RegistryInterface` messages.
This message includes the following information per submitted Artefact:
- The `action`, e.g. `Append`, `Replace` or `Delete` (`Information` is also available)
- A reference to a specific Maintainable Artefact
- A status message with the result of the action, which contains:
  - The status, e.g. `Success`, `Failure` or `Warning`
  - One or more message texts to explain the result (we need only one per Artefact), which in turn contains:
    - A code (the specific HTTP code, per Artefact)
    - A multilingual text message

An example is shown below:

```xml
<reg:SubmissionResult>
  <reg:SubmittedStructure action="Append"> <!-- Append|Delete|Replace -->
    <reg:MaintainableObject>
      <Ref agencyID="SDMX" id="CL_FREQ" version="1.0.0" package="codelist" class="Codelist" />
    </reg:MaintainableObject>
  </reg:SubmittedStructure>
  <reg:StatusMessage status="Success"> <!-- Success|Failure|Warning -->
    <reg:MessageText code="204"> <!-- Could be the HTTP code 200, 201, 204, ... -->
      <com:Text xml:lang="en">Codelist successfully deleted</com:Text>
      <com:Text xml:lang="fr">Codelist supprimé avec succès</com:Text>
    </reg:MessageText>
  </reg:StatusMessage>
</reg:SubmissionResult>
```

Especially when different results occur on the Artefacts (e.g. partial success), the following should occur:
- Return a multi-status return code (like `207`);
- Return a JSON/XML message with the results details (currently available only in SDMX-ML);

In the case of a multi-status response, the `SubmitStructureResponse` message will include the corresponding code per Artefact, e.g.:

```xml
<reg:SubmissionResult>
  <reg:SubmittedStructure action="Replace"> <!-- Append|Delete|Replace -->
    <reg:MaintainableObject>
      <Ref agencyID="SDMX" id="CODELIST" version="1.0" package="codelist" class="Codelist"/>
    </reg:MaintainableObject>
  </reg:SubmittedStructure>
  <reg:StatusMessage status="Success"> <!-- Success|Failure|Warning -->
    <reg:MessageText code="200">  <!-- Could be the HTTP code 200, 204, ... -->
      <com:Text xml:lang="en">Successfully SDMX:CODELIST(1.0) updated Codelist</com:Text>
      <com:Text xml:lang="de">Codelist SDMX:CODELIST(1.0) erfolgreich aktualisiert</com:Text>
    </reg:MessageText>
  </reg:StatusMessage>
</reg:SubmissionResult>

<reg:SubmissionResult>
  <reg:SubmittedStructure action="Replace">
    <reg:MaintainableObject>
      <Ref agencyID="SDMX" id="CL_FREQ" version="1.1.1" package="codelist" class="Codelist" />
    </reg:MaintainableObject>
  </reg:SubmittedStructure>
  <reg:StatusMessage status="Failure">
    <reg:MessageText code="409">
      <com:Text xml:lang="en">Codelist SDMX:CODELIST(1.1.1) failed to update, due to semantic versioning violation</com:Text>
    </reg:MessageText>
  </reg:StatusMessage>
</reg:SubmissionResult>
```


### ANNEX III: Nested Items

This section aims at explaining the particularities of nested Items for a subset of the available Item Schemes, namely:
- CategoryScheme (Category)
- ReportingTaxonomy (ReportingCategory)

In all the above cases, Items may contain other Items in a tree-like hierarchy. As a result, the resource for an Item within such an hierarchy need to include the full path of that Item in order to exactly identify it. 
For example, for the following Category Scheme (excerpt of [SDMX:STAT_SUBJECT_MATTER(1.0)](https://registry.sdmx.org/ws/public/sdmxapi/rest/categoryscheme/SDMX/STAT_SUBJECT_MATTER/1.0/) from the Global SDMX Registry):

```xml
<str:CategoryScheme agencyID="SDMX" id="STAT_SUBJECT_MATTER" version="1.0">
  <com:Name xml:lang="en">SDMX Statistical Subject-Matter Domains</com:Name>
  <str:Category id="DEMO_SOCIAL_STAT">
    <com:Name xml:lang="en">Demographic and social statistics</com:Name>
  </str:Category>
  <str:Category id="ECO_STAT">
    <com:Name xml:lang="en">Economic statistics</com:Name>
    <str:Category id="MACROECO_STAT">
      <com:Name xml:lang="en">Macroeconomic statistics</com:Name>
    </str:Category>
    <str:Category id="SECTORAL_STAT">
      <com:Name xml:lang="en">Sectoral statistics</com:Name>
      <str:Category id="AGRI_FOREST_FISH">
        <com:Name xml:lang="en">Agriculture, forestry, fisheries</com:Name>
      </str:Category>
      <str:Category id="ENERGY">
        <com:Name xml:lang="en">Energy</com:Name>
      </str:Category>
    </str:Category>
    <str:Category id="GOV_FINANCE_PUBLIC_SECTOR">
      <com:Name xml:lang="en">Government finance, fiscal and public sector statistics</com:Name>
    </str:Category>
  </str:Category>
  <str:Category id="ENVIRONMENT_MULTIDOMAIN_STAT">
    <com:Name xml:lang="en">Environment and multi-domain statistics</com:Name>
  </str:Category>
</str:CategoryScheme>
```

In order to get Item/Category `ENERGY` we need to request the following resource: `categoryscheme/SDMX/CAT/1.0/ECO_STAT.SECTORAL_STAT.ENERGY`
Instead of the identifier of the Item, the full path of identifiers that lead to that Item are required, i.e.: `ECO_STAT` -> `SECTORAL_STAT` -> `ENERGY`.

Similarly, when trying to delete that Item (Category) the same resource must be used. The behaviour of the server, then, should be that the Category and all its children (if any) are deleted. In the above example, deleting the Category `ECO_STAT.SECTORAL_STAT` would result into deleting also `ECO_STAT.SECTORAL_STAT.ENERGY` and `ECO_STAT.SECTORAL_STAT.AGRI_FOREST_FISH`.

When submitting a nested Item Scheme for partial update (using `PUT` or `POST` with `isPartial="true"`) then all root level Categories appearing in the body are going to fully replace existing root level Categories. This means that all Categories comprising any root Category are going to be replaced by the new Categories submitted.
Let's assume submitting this partial Category Scheme:

```xml
<str:CategoryScheme agencyID="SDMX" id="STAT_SUBJECT_MATTER" version="1.0.0" isPartial="true">
  <com:Name xml:lang="en">SDMX Statistical Subject-Matter Domains</com:Name>
  <str:Category id="ECO_STAT">
    <com:Name xml:lang="en">Economic statistics</com:Name>
    <str:Category id="MACROECO_STAT">
      <com:Name xml:lang="en">Macroeconomic statistics</com:Name>
    </str:Category>
  </str:Category>
</str:CategoryScheme>
```

When applied to the initial Category Scheme, this update would mean that Category `ECO_STAT` only has one child Category, i.e. the resulting Category Scheme would be:

```xml
<str:CategoryScheme agencyID="SDMX" id="STAT_SUBJECT_MATTER" version="1.0.0">
  <com:Name xml:lang="en">SDMX Statistical Subject-Matter Domains</com:Name>
  <str:Category id="DEMO_SOCIAL_STAT">
    <com:Name xml:lang="en">Demographic and social statistics</com:Name>
  </str:Category>
  <str:Category id="ECO_STAT">
    <com:Name xml:lang="en">Economic statistics</com:Name>
    <str:Category id="MACROECO_STAT">
      <com:Name xml:lang="en">Macroeconomic statistics</com:Name>
    </str:Category>
  </str:Category>
  <str:Category id="ENVIRONMENT_MULTIDOMAIN_STAT">
    <com:Name xml:lang="en">Environment and multi-domain statistics</com:Name>
  </str:Category>
</str:CategoryScheme>
```

Moreover, in order to add a new Category under `SECTORAL_STAT` in the initial Category Scheme, then the whole new tree of `ECO_STAT` would need to be submitted, i.e.:

```xml
<str:CategoryScheme agencyID="SDMX" id="STAT_SUBJECT_MATTER" version="1.0.0">
  <com:Name xml:lang="en">SDMX Statistical Subject-Matter Domains</com:Name>
  <str:Category id="ECO_STAT">
    <com:Name xml:lang="en">Economic statistics</com:Name>
    <str:Category id="MACROECO_STAT">
      <com:Name xml:lang="en">Macroeconomic statistics</com:Name>
    </str:Category>
    <str:Category id="SECTORAL_STAT">
      <com:Name xml:lang="en">Sectoral statistics</com:Name>
      <str:Category id="AGRI_FOREST_FISH">
        <com:Name xml:lang="en">Agriculture, forestry, fisheries</com:Name>
      </str:Category>
      <str:Category id="NEW_SECTORAL_CATEGORY">
        <com:Name xml:lang="en">New</com:Name>
      </str:Category>
      <str:Category id="ENERGY">
        <com:Name xml:lang="en">Energy</com:Name>
      </str:Category>
    </str:Category>
    <str:Category id="GOV_FINANCE_PUBLIC_SECTOR">
      <com:Name xml:lang="en">Government finance, fiscal and public sector statistics</com:Name>
    </str:Category>
  </str:Category>
</str:CategoryScheme>
```

### ANNEX IV: Summary of HTTP response codes for structure management

| Method | Exists | Versioning respected | Is Referenced | References exist/provided | Response Code |
|---|---|---|---|---|---|
| POST | F | T | - | T | `201` (successful) or `207` (partially successful) |
| POST | F | T | - | F | `409` missing references |
| POST | F | F | - | I | `409` failure to respect the versioning rules |
| PUT/POST | T | T | F | T | `200` (successful) or `207` (partially successful)  |
| PUT/POST | T | T | F | F | `409` |
| PUT/POST | T | T | T | T | `200` if update is possible - `409` if references would break |
| PUT/POST | T | F | - | - | `409` in case of changes that violate the versioning requirements |
| PUT/POST | - | - | - | - | `422` see section **Server** |
| PUT | F | - | - | - | `404` |
| DELETE | F | - | - | - | `404` |
| DELETE | T | F | F | I | `200` |
| DELETE | T | T | I | I | `409` when trying to delete a stable artefact, a deprecation strategy may be followed |
| DELETE | T | F | T | I | `409` |

<sup>T: True, F: False, I: Irrelevant, -: Not applicable</sup>


### ANNEX V: Examples for structure management

#### Difference between replace and partially update

To explain the updating semantics, the difference between replacing and partially updating the same SDMX Artefact shall be illustrated in the following example. 
For the sake of simplicity, a Codelist will be utilised, i.e. let's assume:

```xml
<str:Codelist agencyID="SDMX" id="CL_DECIMALS" version="1.0">
   <com:Name>Code list for Decimals (DECIMALS)</com:Name>
   <com:Description xml:lang="en">It provides a list of values showing the 
     number of decimal digits used in the data.</com:Description>
   <str:Code id="0">
      <com:Name>Zero</com:Name>
   </str:Code>
   <str:Code id="1">
      <com:Name>One</com:Name>
   </str:Code>
   <str:Code id="2">
      <com:Name>Two</com:Name>
   </str:Code>
</str:Codelist>
```

Let's assume that we `PUT` the following Codelist, under `/codelist/SDMX/CL_DECIMALS/1.0`, or `POST` it under `/codelist/`:

```xml
<str:Codelist agencyID="SDMX" id="CL_DECIMALS" version="1.0">
   <com:Name>Code list for Decimals (DECIMALS)</com:Name>
   <com:Description xml:lang="en">It provides a list of values showing the 
     number of decimal digits used in the data.</com:Description>
   <str:Code id="0">
      <com:Name>No decimal</com:Name>
   </str:Code>
   <str:Code id="1">
      <com:Name>One</com:Name>
   </str:Code>
 </str:Codelist>
```

This will result into replacing the original Codelist with the one submitted. Hence, in the new Codelist only two Codes will exist; the first one (Code `0`) with an updated name.

If, instead, we added the flag `isPartial="true"` in the above Codelist, i.e.:

```xml
<str:Codelist agencyID="SDMX" id="CL_DECIMALS" version="1.0" isPartial="true">
   <com:Name>Code list for Decimals (DECIMALS)</com:Name>
   <com:Description xml:lang="en">It provides a list of values showing the 
     number of decimal digits used in the data.</com:Description>
   <str:Code id="0">
      <com:Name>No decimal</com:Name>
   </str:Code>
   <str:Code id="1">
      <com:Name>One</com:Name>
   </str:Code>
 </str:Codelist>
```

Then the result would be a bit different. The new Codelist will still have three Codes, but the first one (Code `0`) would have an updated name, i.e.:

```xml
<str:Codelist agencyID="SDMX" id="CL_DECIMALS" version="1.0">
   <com:Name>Code list for Decimals (DECIMALS)</com:Name>
   <com:Description xml:lang="en">It provides a list of values showing the 
     number of decimal digits used in the data.</com:Description>
   <str:Code id="0">
      <com:Name>No decimal</com:Name>
   </str:Code>
   <str:Code id="1">
      <com:Name>One</com:Name>
   </str:Code>
   <str:Code id="2">
      <com:Name>Two</com:Name>
   </str:Code>
</str:Codelist>
```
