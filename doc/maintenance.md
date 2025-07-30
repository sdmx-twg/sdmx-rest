# Maintaining Structural and Reference Metadata and their underlying Data via the SDMX REST API

The following sections describe the foreseen **HTTP methods** for the maintenance of SDMX Structural and Reference Metadata and their underlying data via the SDMX REST API. These specifications align with [RFC7231](https://www.rfc-editor.org/rfc/rfc7231). 

-------------

**Content:**

- [Maintaining Structural Metadata](#maintaining-structural-metadata)
  - [Create/Replace](#create-replace)
    - [Client](#client)
    - [Server](#server)
    - [Response](#response)
  - [Partial update](#partial-update)
    - [Partially update or extend Item Schemes](#partially-update-or-extend-item-schemes)
    - [Partially add or update specific localised properties](#partially-add-or-update-specific-localised-properties)
  - [Delete](#delete)
    - [Client](#client-1)
    - [Server](#server-1)
    - [Response](#response-1)
- [Maintaining Reference Metadatasets](#maintaining-reference-metadatasets)
  - [Create, Update or Delete a single Reference Metadataset](#create-update-or-delete-a-single-reference-metadataset)
  - [Create or Update multiple Reference Metadatasets](#create-or-update-multiple-reference-metadatasets)
  - [Client](#client-2)
  - [Server](#server-2)
- [Maintaining related Data](#maintaining-related-data)
  - [Actions](#actions)
    - [Merge](#merge)
    - [Replace](#replace)
    - [Delete](#delete-1)
  - [Client](#client-3)
  - [Server](#server-3)
  - [Response](#response-2)
- [ANNEX I: RegistryInterface messages for Subscription and Registration](#annex-i-registryinterface-messages-for-subscription-and-registration)
  - [Introduction](#introduction)
  - [Retrieving Subscriptions and Registrations](#retrieving-subscriptions-and-registrations)
  - [Maintaining Subscriptions and Registrations](#maintaining-subscriptions-and-registrations)
- [ANNEX II: Structure maintenance response message](#annex-ii-structure-maintenance-response-message)
- [ANNEX III: Data maintenance response message](#annex-iii-data-maintenance-response-message)
- [ANNEX III: Nested Items](#annex-iii-nested-items)
- [ANNEX IV: Summary of HTTP response codes for structure management](#annex-iv-summary-of-http-response-codes-for-structure-management)
- [ANNEX V: Examples for structure management](#annex-v-examples-for-structure-management)
  - [Difference between replace and partially update](#difference-between-replace-and-partially-update)

---------------

## Maintaining Structural Metadata

### Create/Replace

Inserting (or creating) new SDMX Structural Metadata (Maintainable Artefacts) or fully replacing them is detailed in this secion.

Submitting individual SDMX Artefacts can be achieved with an HTTP `PUT` method.  
Submitting individual SDMX Artefacts or in bulk, either of the same or of different types, is achieved with an HTTP `POST` method.

The `PUT` method may be used like this:

- under `/structure/{maintainable}/{identifier}` for the Maintainable Artefact of type `{maintainable}`, identified by `{identifier}`, i.e. an SDMX DSD message with `ESTAT:NA_MAIN(1.1)` under `/structure/datastructure/ESTAT/NA_MAIN/1.1`

The `POST` method may be used like this:

- under `/structure` for different types of Maintainable Artefacts, i.e. an SDMX Structure message
- under `/structure/{maintainable}` for Maintainable Artefacts of type `{maintainable}`, i.e. an SDMX Structure message including only a specific type of Structures; e.g. an SDMX message with one or more Dataflows, under `/structure/dataflow`

In case, a submitted Maintainable Artefact already exists and the submitted Maintainable Artefact has the `isPartialLanguage` or `isPartial` property set to `true`, the Maintainable Artefact is not fully replaced but updated. For more details see section [PARTIAL UPDATE](#partial-update) below.

#### Client

In order to create Artefacts, the client:

- MAY set the `Accept` header to indicate the preferred response format
- MUST set the `Content-type` header according to the format of the submitted Artefact(s)
- MUST include in the request body, one or more Maintainable Artefacts in the SDMX format indicated in the `Content-type` header and of the SDMX type indicated in the resource, i.e.:
  - for `POST`:
    - any set of Maintainable Artefacts under resource `/structure/`
    - one or more Maintainable Artefacts of the specific type under the corresponding resource type, e.g. Dataflows under `/structure/dataflow/`
  - for `PUT`:
    - one Maintainable Artefact, of the specific type under the corresponding resource type and identified according to the resource parameters, e.g., DSD `ESTAT:NA_MAIN(1.1)` under `/structure/datastructure/ESTAT/NA_MAIN/1.1`

#### Server

In response to the client request, the server:

- MUST return `201` (created) upon successful insertion/creation of all submitted artefacts
- MUST return `200` (ok) or `204` (no content) upon successful full replacement or partial update of all submitted artefacts
- MUST return `207` (multi-status) if the outcome differs between the submitted artefacts, including unsuccessful submissions
- In case of any response other than `201`, `204` or `404`:
  - MUST return a `SubmitStructureResponse` message indicating the outcome for each of the submitted artefacts, as `JSON` (default) or `XML` message according to the `Accept` header
  - MUST set the `Content-type` according to the returned format
- MAY set the `Location` header to point to the inserted/created Artefact if only one Maintainable Artefact was submitted (only one instance is allowed)
- MUST return `409` (conflict) in case versioning rules are violated or references are missing or would break
- MUST return `422` (unprocessable content) in case of resource type mismatch, i.e. the resource type identified in the URL does not match the Artefact type(s) of the included SDMX Artefact(s), or the identification of the Artefact (in case of `PUT`) does not match the resource path parameters
- MUST return `404` (not found) if the submitted Maintainable Artefact has the `isPartialLanguage` or `isPartial` property set to `true` but the corresponding Maintainable Artefact doesn't exist

#### Response

The `SubmitStructureResponse` message (defined as part of the `RegistryInterface` messages) must be returned in case of any response other than `201`, `204` and `404`.
The details of the message are explained in [annex II **Structure maintenance response message**](#annex-ii-structure-maintenance-response-message), below.

The following matrix summarises the returned `HTTP` response codes.

| Method | Exists | Semantic Versioning respected | Is Referenced | References exist/provided | Response Code |
|----------|---|-----|---|---|--------------------------------------------------------------------------------|
| POST     |   |     |   |   | `207` (multi-status) if differing outcome between different submitted artefacts |
| PUT/POST | F | T\* |   | T | `201` (created) if successful insertion/creation of all submitted artefacts |
| PUT/POST | T |     | F | T | `200` (ok) or `204` (no content) if successful replacement/update of all submitted artefacts, which are all not semantically versioned |
| PUT/POST |   | F   |   |   | `409` (conflict) in case semantic versioning rules are violated |
| PUT/POST | T |     | T |   | `409` (conflict) especially in case of semantic versioning since references would break |
| PUT/POST | F | T\* |   | F | `409` (conflict) since missing references |
| PUT/POST |   |     |   |   | `422` (unprocessable content) see section **Server** above |
| PUT/POST | F |     |   |   | `404` (not found) if the submitted artefact has the `isPartialLanguage` or `isPartial` property set to `true` |

<sup>T: True, F: False, [empty]: Irrelevant/Not applicable, *: at least for the semantically versioned artefacts</sup>

<sup>NOTE: The rules for versioning in SDMX 3.0 are based on Semantic Versioning. In addition, Artefacts with no version are supported. SDMX 3.0 does not specify strict rules for legacy versioning - i.e. versioning prior to SDMX 3.0. The versioning rules in that case are up to the organisation using them.</sup>


### Partial Update

Partial updates use the rules for submissions defined above for insertion/creation and full replacements, see section [CREATE/REPLACE](#create-replace). 

#### Partially update or extend Item Schemes

In the case of Item Schemes, it is possible to submit a partial Item Scheme, including only the Items that must be added or updated.
The usage of `POST` and `PUT` is the same as in the case of insertion/creation and full replacement. The only difference is that the submitted Items Schemes to be partially updated or extended (i.e. not completely replaced) must include the flag `isPartial="true"`.

In this case, any Item included in the submitted partial Item Scheme:
- fully replaces the Item, if it existed in the stored Item Scheme; the Item stays in the same position
- is added as a new Item, if it did not exist
  - in a selected position, if it is submitted relatively to existing Items
  - else, at the end of the Item Scheme

For more details on nested Items (like Categories), please refer to [ANNEX III: Nested Items](#annex-iii-nested-items) below.

In addition to the Items, the Item Scheme details are also updated accordingly, i.e. the names, description, annotations and any other attributes.
Names and Descriptions are replaced if they exist for the submitted language (i.e. the value of their `lang` attribute), or simply added, if new. Annotations and other Item Scheme attributes are fully replaced.

#### Partially add or update specific localised properties

In case a submitted Maintainable Artefact has the `isPartialLanguage` property set to `true`, only the included languages are added or updated and other languages are not changed.


### Delete

Always concerns one Maintainable Artefact or one Item. 
For example:
- A fully identified Maintainable Artefact, e.g. `/structure/codelist/SDMX/CL_FREQ/1.0`
- A fully identified Item, e.g. `/structure/codelist/SDMX/CL_FREQ/1.0/M`

In case the deleted Item was acting as a parent to other Item(s), then the server should make sure that:
- in case of flat Item Schemes the children become orphans, i.e. their parent is removed
- in case of nested Item Schemes, all children are also deleted

#### Client

In order to delete an Artefact, the client:

- MAY set the `Accept` header to indicate the preferred response format
- MUST fully identify exactly **one** Maintainable Artefact or **one** Item, by means of the proper URL

#### Server

In response to an Artefact deletion, the server:

- MUST return `200` (ok) or `204` (no content) in case of successful deletion
- In case of any response other than `204` and `404`:
  - MUST return a `SubmitStructureResponse` message indicating the outcome for each of the submitted artefacts, as `JSON` (default) or `XML` message according to the `Accept` header
  - MUST set the `Content-type` according to the returned format
- MUST return `409` (conflict) in case versioning rules are violated or references are missing or would break
- MUST respond with `404` (not found) if the resource was not found

#### Response

The `SubmitStructureResponse` message (defined as part of the `RegistryInterface` messages) must be returned in case of any response other than `204` and `404`.
The details of the message are explained in [annex II **Structure maintenance response message**](#annex-ii-structure-maintenance-response-message), below.

The following matrix summarises the returned `HTTP` response codes.

| Method | Exists | Semantic Versioning used | Is Referenced | References exist/ provided | Response Code |
|---|---|---|---|---|---|
| DELETE | T | F | F |   | `200` (ok) or `204` (no content) |
| DELETE | F |   |   |   | `404` (not found) |
| DELETE | T | T |   |   | `409` (conflict) since (internal/external) references could break; however, a deprecation strategy may be followed |
| DELETE | T | F | T |   | `409` (conflict) since references would break |

<sup>T: True, F: False, [empty]: Irrelevant/Not applicable</sup>


## Maintaining Reference Metadatasets

This section fully applies the [RFC7231 standard](https://www.rfc-editor.org/rfc/rfc7231#section-4.3) for the meaning of HTTP methods. 

### Create, Update or Delete a single Reference Metadataset

Inserting (creating a new), updating (an existing) and deleting an individual (maintainable) SDMX Reference Metadataset is detailed in this secion.

Use the `PUT` method on the related target resource `/metadata/metadataset/{identifier}` to **create** or **update** an individual Reference Metadataset, i.e. an SDMX  message containing the representation of the `ESTAT:MDS_EXAMPLE(1.0)` Reference Metadataset under `/metadata/metadataset/ESTAT/MDS_EXAMPLE/1.0`.  
The Reference Metadataset is created if it doesn't exist yet in the underlying registry. It is updated if it already exists. If the Reference Metadataset has the `isPartialLanguage` property set to `true` then only the included languages are added or updated and other languages are not changed, else the Reference Metadataset is **completely replaced**.

Use the `DELETE` method on the related target resource `/metadata/metadataset/{identifier}` to **delete** an individual Reference Metadataset, i.e. the `ESTAT:MDS_EXAMPLE(1.0)` Reference Metadataset under `/metadata/metadataset/ESTAT/MDS_EXAMPLE/1.0`.

### Create or Update multiple Reference Metadatasets

Inserting (creating new) and updating (completely replacing existing) multiple (maintainable) SDMX Reference Metadatasets is detailed in this secion.

Use the `POST` method on the metadata resource `/metadata/metadataset` or simply on the metadataset resource `/metadata` to **create** or **update** multiple Reference Metadatasets, i.e. an SDMX message containing the representation of one or more Reference Metadatasets.  
All Reference Metadatasets that don't exist yet in the underlying registry are created, all others are updated. If a Reference Metadataset has the `isPartialLanguage` property set to `true` then only the included languages are added or updated and other languages are not changed, else the Reference Metadataset is **completely replaced**.

### Client

In order to create, update or delete Reference Metadatasets, the client:

- MAY set the `Accept` header to indicate the preferred response format

In order to create or update Reference Metadatasets, the client:

- MUST set the `Content-type` header according to the format of the submitted Reference Metadataset(s)
- MUST include in the request body, one or more Reference Metadatasets in the SDMX format indicated in the `Content-type` header

### Server

In response to Reference Metadataset(s) creation, update or deletion, the server:

- MUST respond with `201` in case of successful creation (or `207` for multi-status)
- MAY set the `Location` header to point to the created Reference Metadataset (only one instance is allowed)
- MUST respond with `200` in case of successful update or deletion (or `207` for multi-status)
- MUST return a `SubmitStructureResponse` message with the result of the action(s) as defined for the `RegistryInterface` messages (success, partial success, failure), in the SDMX format requested by the `Accept` header, or the default, if the `Accept` type is not supported (currently available only in SDMX-ML format). This is especially important when varying results occur for different Reference Metadatasets. The details of the message are explained in section [**Structure response message**](#ANNEX-II-Structure-response-message).
- MUST set the `Content-type` according to the returned format (currently only in SDMX-ML format)
- MUST respond with `422` in case of semantic errors, i.e. the content submitted in the request body is not Reference Metadatsets, or the identification of the Reference Metadatset (in case of `PUT`) does not match the resource path parameters (or `207` for multi-status)
- MUST respond with `404` if the to be deleted Reference Metadataset was not found

The following matrix summarises the returned `HTTP` response codes.

| Method | Exists | Semantic Versioning respected | Is Referenced | References exist/provided | Response Code |
|--------|--------|-------------------------------|---------------|---------------------------|---------------|
| POST   |        |                               |               |                           | `207` (multi-status) if differing outcome between different submitted artefacts |
| PUT/POST | F    | T\*                           |               | T                         | `201` (created) if successful insertion/creation of all submitted artefacts |
| PUT/POST | T    |                               | F             | T                         | `200` (ok) or `204` (no content) if successful replacement/update of all submitted artefacts, which are all not semantically versioned |
| PUT/POST |      | F                             |               |                           | `409` (conflict) in case semantic versioning rules are violated |
| PUT/POST | T    |                               | T             |                           | `409` (conflict) especially in case of semantic versioning since references would break |
| PUT/POST | F    | T\*                           |               | F                         | `409` (conflict) since missing references |
| PUT/POST |      |                               |               |                           | `422` (unprocessable content) semantic error |
| PUT/POST | F    |                               |               |                           | `404` (not found) if the submitted artefact has the `isPartialLanguage` property set to `true` |
| DELETE   | T    | F                             | F             |                           | `200` (ok) or `204` (no content) if successfully deleted |
| DELETE   | F    |                               |               |                           | `404` (not found) |
| DELETE   | T    | T                             |               |                           | `409` (conflict) when trying to delete a stable Reference Metadatset, a deprecation strategy may be followed |
| DELETE   | T    | F                             | T             |                           | `409` (conflict) since references would break |

<sup>T: True, F: False, [empty]: Irrelevant/Not applicable, *: at least for the semantically versioned artefacts</sup>  
<sup>NOTE: The rules for versioning in SDMX 3.0 are based on Semantic Versioning. SDMX 3.0 does not specify rules for legacy versioning - i.e. versioning prior to SDMX 3.0. The versioning rules in that case are up to the organisation using them.</sup>
<sup>NOTE: The previous message header property “DataSetAction” is deprecated for Reference Metadatasets and to be ignored if still present.</sup>


## Maintaining related Data

Managing SDMX Data or data-related Reference Metadata is detailed in this secion.

Submitting individual SDMX Datasets or in bulk, either with the same or of different type of actions (see [Actions](#actions) below), is achieved with an HTTP `POST` method.

### Actions

The action to be executed per dataset is defined by the `Action` property of the related dataset contained in the submitted SDMX data message.

For format-specific details see here:
- [SDMX-ML](https://github.com/sdmx-twg/sdmx-ml/blob/master/documentation/SDMX_3-1_SECTION_3A_PART_IV_DATA.md#41-Data-Actions)
- [SDMX-JSON](https://github.com/sdmx-twg/sdmx-json/blob/master/data-message/docs/1-sdmx-json-field-guide.md#dataset)
- [SDMX-CSV](https://github.com/sdmx-twg/sdmx-csv/blob/master/data-message/docs/sdmx-csv-field-guide.md#column-content-all-rows-after-header)

The following types of actions are defined:

#### Merge

Data or data-releated reference metadata is to be merged, through either update or insertion depending on already existing information. This operation does not allow deleting any component values. Updating individual values in multi-valued measure, attribute or reference metadata values is not supported either. The complete multi-valued value is to be provided.  
Only non-dimensional components (measure, attribute or reference metadata values) can be **omitted** as long as at least one of those components is present. Bulk merges are thus not supported. Only the provided values are merged.  
Dimension values for higher-level (reference metadata) attributes can be **switched-off** when those are not attached to these dimensions.  
All observations as well as the sets of reference metadata attributes at specific dimension combinations impacted by the *merge* action change their time stamp.

Note: The previous ***append*** and ***information*** actions have been deprectated. It is recommended to discontinue using these action types. If a dataset with one of these actions is uploaded, it is to be interpreted as a *merge* action.

#### Replace

Data or reference metadata is to be replaced, through either update, insert or delete depending on already existing information. A full replacement is hereby assumed to take place at specific “replacement levels”: either for entire observations or for any specific dimension combination for reference metadata attributes. Within these “replacement levels” the provided values are inserted or updated, and omitted values are deleted. Values provided for the other attributes (those above the observation level) are merged (see Merge action).  
Only non-dimensional components (measure, attribute or reference metadata values) can be **omitted**. Bulk replacing is thus not supported.  
Dimension values for higher-level (reference metadata) attributes can be **switched-off** when those are not attached to these dimensions.  
Replacing non-existing elements is not resulting in an error.  
All observations as well as the sets of reference metadata attributes at specific dimension combinations impacted by the *replace* action change their time stamp.  
Because the *replace* action always takes place at specific levels, it cannot be used to replace a whole dataset. However, a “replace all” effect can be achieved by combining an empty *delete* dataset with a *merge* or *replace* dataset within the same data message. To replace a whole time series, a message can combine a *delete* dataset containing only the partial key of the time series (where not used dimension values are omitted) with a *merge* or *replace* dataset for that time series.

#### Delete

Data or reference metadata is to be deleted. Deletion is hereby assumed to take place at the lowest level of detail provided in the message.  
Any component (including dimensions) can be **omitted**. Omitting dimension values allows for bulk deletions. Partially omitting non-dimension component values allows restricting the deletion of measure, attribute or reference metadata values to the ones being present. Instead of real values for non-dimensional components, it is sufficient to use any valid value.   
With this, whole datasets, any slices of observations for dimension groups such as time series, observations or individual measure, attribute and reference metadata attributes values can be deleted.  
Dimension values for higher-level (reference metadata) attributes can be **switched-off** when those are not attached to these dimensions.  
Deleting non-existing elements or values is not resulting in an error.  
All observations as well as the sets of attributes and reference metadata at higher partial keys impacted by the *delete* action change their time stamp.

### Client

In order to upload datasets, the client:

- MAY set the `Accept` header to indicate the preferred response format
- MUST set the `Content-type` header according to the format of the submitted Artefact(s)
- MUST include in the request body, one or more SDMX Datasets in the SDMX format indicated in the `Content-type` header, i.e.:
  - for `POST`:
    - one or more datasets under resource `/data/`

Note: For the moment, the SDMX standard doesn't define yet ways of uploading files that exceed the allowed body size, nor compressed messages.

### Server

In response to the client request, the server:

- MUST return `200` (ok) upon successful execution of the actions defined for each of the submitted datasets
- MUST return a `SubmitDataResponse` message indicating the outcome for each of the submitted datasets, as `JSON` message
- MUST set the `Content-type` according to this returned format
- MUST return `404` (not found) if a corresponding Maintainable Artefact doesn't exist for any of the submitted datasets. No data is modified in this case.
- MUST return `422` (unprocessable content) in case any content of the datasets submitted does not match the definitions in any of the corresponding Maintainable Artefacts, or if any other applied validation rules are not respected. No data is modified in this case. 
- MUST return `409` (conflict) in case the server cannot process the message because it is already occupied with uploading data for the same scope, and when queueing is not supported. No data is modified in this case. 

Notes:
- The terms “*delete*”, “*merge*” and “*replace*” do not imply a physical replacement or deletion of values in the underlying database. To minimize the physical resource requirements, SDMX web service implementations that do not support the *includeHistory* URL parameter might physically replace the existing values in the database. SDMX web services that neither support the *updatedAfter* URL parameter might also implement physical deletions. However, SDMX web services that support these parameters (or other time-machine features), would not overwrite or delete the physical values. 
- SDMX web services that support the *includeHistory* URL parameter should never allow deleting their historic data content because this would interfere with the interests of data consumers, such as data aggregators. Therefore, a specific feature to physically delete previous (outdated) content is intentionally not supported by the SDMX standard syntax. If such a feature is required by an organisation, then it needs to be implemented as a custom feature outside the SDMX standard.
- Likewise, all SDMX-compliant systems that do (or are configured to) support the *updatedAfter* URL parameter need to systematically retain the information about deleted data (or metadata).
- All datasets – even with varying actions – within a single data message have always to be treated as **ACID transaction** to guarantee “transactional safety” (full data consistency and validity despite errors, power failures, and other mishaps). These datasets are to be processed in the order of appearance in the message. The advantage of such data messages is thus the ability to bundle separate *delete* and *replace* or *merge* actions into one transactional data message.


### Response

The `SubmitDataResponse` message must always be returned.
The details of the message are explained in [annex II **Data maintenance response message**](#annex-iii-data-maintenance-response-message), below.

The following matrix summarises the returned `HTTP` response codes.

| Method | all corresponding Maintainable Artefact exist | Response Code |
|---|---|---|---|---|---|
| POST | T | `200` (ok) upon successful execution of the actions defined for each of the submitted datasets |
| POST | F | `404` (not found) |
| POST | T | `422` (unprocessable content) in case any content of the datasets submitted does not match the definitions in any of the corresponding Maintainable Artefacts, or if any other applied validation rules are not respected. |
| POST | T | `409` (conflict) in case the server cannot process the message because it is already occupied with uploading data for the same scope, and when queueing is not supported. |

<sup>T: True, F: False</sup>


## ANNEX I: RegistryInterface messages for Subscription and Registration

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


## ANNEX II: Structure maintenance response message

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

## ANNEX III: Data maintenance response message

The `SubmitDataResponse` message must be returned in any case (success, partial success, failure).
This message includes the following information:
- Per submitted dataset:
  - A URN reference to a specific Maintainable Artefact
  - The `action`, e.g. `Merge`, `Replace` or `Delete`
  - Intermediate status message(s) with the result of the current action, containing:
    - The status, e.g. `Success`, `Failure` or `Warning`
    - One or more multilingual texts to explain the status
- Final submission result of the action, which contains:
    - Final status code (the finally resulting specific HTTP code)
    - Final status message with the result of the submission, containing:
      - The status, e.g. `Success`, `Failure` or `Warning`
      - One or more multilingual texts to explain the result

An example is shown below:

```xml
<reg:SubmitDataResponse>

  <reg:SubmittedData urn="...datastructure.Dataflow:SDMX:DF_EXAMPLE(1.0.0)" action="Merge"/> <!-- Merge|Replace|Delete -->
    <reg:StatusMessage status="Success"> <!-- status: Success|Failure|Warning -->
      <com:Text xml:lang="en">Data successfully read</com:Text>
      <com:Text xml:lang="fr">Données lues avec succès</com:Text>
    </reg:StatusMessage>
    <reg:StatusMessage status="Success"> <!-- status: Success|Failure|Warning -->
      <com:Text xml:lang="en">Data successfully merged</com:Text>
      <com:Text xml:lang="fr">Données fusionnées avec succès</com:Text>
    </reg:StatusMessage>
  </reg:SubmittedData>

 <reg:SubmissionResult code="200" > <!-- code: Could be the HTTP code 200, 404, 409, 422... -->
    <reg:StatusMessage status="Success"> <!-- status: Success|Failure|Warning -->
      <com:Text xml:lang="en">All changes have been successfully completed</com:Text>
      <com:Text xml:lang="fr">Toutes les modifications ont été effectuées avec succès</com:Text>
    </reg:StatusMessage>
  </reg:SubmissionResult>

</reg:SubmitDataResponse>
```

In the case of multiple datasets, the `SubmitDataResponse` message will include the corresponding information per dataset, e.g.:

```xml
<reg:SubmitDataResponse>

  <reg:SubmittedData urn="...datastructure.Dataflow:SDMX:DF_EXAMPLE(1.0.0)" action="Merge"/> <!-- Merge|Replace|Delete -->
    <reg:StatusMessage status="Success"> <!-- status: Success|Failure|Warning -->
      <com:Text xml:lang="en">Data successfully read</com:Text>
      <com:Text xml:lang="fr">Données lues avec succès</com:Text>
    </reg:StatusMessage>
    <reg:StatusMessage status="Success"> <!-- status: Success|Failure|Warning -->
      <com:Text xml:lang="en">Data successfully merged</com:Text>
      <com:Text xml:lang="fr">Données fusionnées avec succès</com:Text>
    </reg:StatusMessage>
  </reg:SubmittedData>

  <reg:SubmittedData urn="...datastructure.Dataflow:SDMX:DF_EXAMPLE2(1.0.0)" action="Replace"/> <!-- Merge|Replace|Delete -->
    <reg:StatusMessage status="Failure"> <!-- status: Success|Failure|Warning -->
      <com:Text xml:lang="en">The dataflow SDMX:DF_EXAMPLE2(1.0.0) doesn't exist</com:Text>
      <com:Text xml:lang="fr">Le dataflow SDMX:DF_EXAMPLE2(1.0.0) n'existe pas</com:Text>
    </reg:StatusMessage>
  </reg:SubmittedData>

 <reg:SubmissionResult code="409" > <!-- code: Could be the HTTP code 200, 404, 409, 422... -->
    <reg:StatusMessage status="Failure"> <!-- status: Success|Failure|Warning -->
      <com:Text xml:lang="en">Due to the previous error, all changes have been completely aborted</com:Text>
      <com:Text xml:lang="fr">En raison de l'erreur précédente, toutes les modifications ont été complètement annulées</com:Text>
    </reg:StatusMessage>
  </reg:SubmissionResult>

</reg:SubmitDataResponse>

```

## ANNEX IV: Nested Items

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

## ANNEX V: Examples for structure management

### Difference between replace and partially update for a non-semantically-versioned artefact

To explain the updating semantics, the difference between replacing and partially updating the same non-semantically-versioned SDMX Artefact shall be illustrated in the following example. 
For the sake of simplicity, a Codelist will be utilised, i.e. let's assume:

```xml
<str:Codelist agencyID="SDMX" id="CL_DECIMALS" version="1.0">
   <com:Name>Code list for Decimals (DECIMALS)</com:Name>
   <com:Description xml:lang="en">It provides a list of values showing the number of decimal digits used in the data.</com:Description>
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
   <com:Description xml:lang="en">It provides a list of values showing the number of decimal digits used in the data.</com:Description>
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
   <com:Description xml:lang="en">It provides a list of values showing the number of decimal digits used in the data.</com:Description>
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
   <com:Description xml:lang="en">It provides a list of values showing the number of decimal digits used in the data.</com:Description>
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
