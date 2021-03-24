## Applying the HTTP methods to the SDMX REST API for Structural maintenance


### CREATE

#### Artefact (Structure)

Submitting SDMX Artefacts in bulk, either of the same or of different types, is achieved with a `POST` method.
Creating new Artefact(s) may be issued by:
- `POST` one or more Maintainable Artefacts under the proper resource type, e.g. for Codelists: `/structure/codelist/`
- `POST` one or more Maintainable Artefacts under the abstract structure resource type, e.g. `/structure/`

##### Client

In order to create Artefacts, the client:

- MAY set the `Accept` header to indicate the preferred response format;
- MUST set the `Content-type` header according to the format of the submitted Artefact;
- MUST include in the request body, one or more Maintainable Artefacts in the SDMX format indicated in the `Content-type` header and of the SDMX type indicated in the resource, i.e.:
  - For `POST`:
    - any set of Maintainable Artefacts under resource `/structure/`
    - a set of specific type of Maintainable Artefacts under the corresponding resource type, e.g. for Codelists: `/structure/codelist/`

##### Server

In response to an Artefact creation, the server:

- MUST return `201` upon successful creation (or `207` for partial success);
- MUST return a `SubmitStructureResponse` message with the result of the action(s), according to the `Accept` header, or the default, if the `Accept` type is not supported (currently available only in SDMX-ML 2.1);
- MUST set the `Content-type` according to the returned format;
- MAY set the `Location` header to point to the created/primary resource/Artefact (only one instance is allowed);

```
RFC7231
(https://tools.ietf.org/html/rfc7231)

6.3.2. 201 Created
(https://tools.ietf.org/html/rfc7231#section-6.3.2)
[...]
   The primary resource created by the request is identified
   by either a Location header field in the response or, if no Location
   field is received, by the effective request URI.

4.3.3. POST
(https://tools.ietf.org/html/rfc7231#section-4.3.3)
  [...]
   If one or more resources has been created on the origin server as a
   result of successfully processing a POST request, the origin server
   SHOULD send a 201 (Created) response containing a Location header
   field that provides an identifier for the primary resource created
   (Section 7.1.2) and a representation that describes the status of the
   request while referring to the new resource(s).
  [...]
```

##### Response

The `SubmitStructureResponse` message must be returned in any case (success, partial success, failure).
In SDMX 2.1 this is defined as part of the `RegistryInterface` messages.
The details of the message are explained in section **Structure response message**, below.

Especially when different results occur on the Artefacts (e.g. partial success), the following should occur:
- Return a multi-status return code (like `207`);
- Return a `JSON` or `XML` message with the details of the result (currently available only in SDMX-ML 2.1);

| Method | Exists | Is Final | Is Referenced | Refs exist/ provided | Response Code |
|---|---|---|---|---|---|
| POST | F | - | - | T | `201` (successful) or `207` (partially successful) |
| POST | F | - | - | F | `409` failed references |

<sup>T: True, F: False, I: Irrelevant, -: Not applicable</sup>


### UPDATE

#### Replace Artefacts

Following the current SDMX practices, updating (replacing) means providing the new version of any Maintainable Artefact.
According to RFC7231, `PUT` is the proper way to update a resource, as identified by the URL, but `POST` may also be used in case more than one resources need to be updated.

The `POST` method may be used like this:

- under `/structure` for different types of Maintainable Artefacts, i.e. an SDMX Structure message; 
- under `/structure/{maintainable}` for Maintainable Artefacts of type `{maintainable}`, i.e. an SDMX Structure message including only a specific type of Structures; e.g. an SDMX message with Dataflows, under `/structure/dataflow`;

The `PUT` method may be used like this:

- under `/structure/{maintainable}/{identifier}` for the Maintainable Artefact of type `{maintainable}`, identified by `{identifier}`, i.e. an SDMX DSD message with `ESTAT:NA_MAIN(1.1)` under `/structure/datastructure/ESTAT/NA_MAIN/1.1`

The result, following the current practices of updates in SDMX, is to **completely replace** the identified Artefact.

##### Client

In order to update Artefact(s), the client:

- MAY set the `Accept` header to indicate the preferred response format;
- MUST set the `Content-type` header according to the format of the submitted Artefact;
- MUST include in the request body, one or more Maintainable Artefacts in the SDMX format indicated in the `Content-type` header and of the SDMX type indicated in the resource, i.e.:
  - any set of Maintainable Artefacts under resource `/structure/`
  - one or more (only `POST`) Maintainable Artefacts of the specific type under the corresponding resource type, e.g. Dataflows under `/structure/dataflow/`

##### Server

In response to Artefact(s) update, the server:

- MUST respond with `200` in case of successful update;
- MUST return a `SubmitStructureResponse` message with the result of the action(s), according to the `Accept` header, or the default, if the `Accept` type is not supported (currently available only in SDMX-ML 2.1);
- MUST set the `Content-type` according to the returned format;
- MUST respond with `422` in case of resource type mismatch, i.e. the resource type identified in the URL does not match to the Artefact type(s) of the included SDMX Artefact(s).

##### Response

The `SubmitStructureResponse` message must be returned in any case (success, failure).
In SDMX 2.1 this is defined as part of the `RegistryInterface` messages.
The details of the message are explained in section **Structure response message**, below.

The following matrix summarises the returned `HTTP` response codes.

| Method | Exists | Is Final | Is Referenced | References exist/provided | Return Code |
|---|---|---|---|---|---|
| PUT/POST | T | F | F | T | `200` (successful) or `207` (partially successful)  |
| PUT/POST | T | F | F | F | `409` |
| PUT/POST | T | F | T | T | `200` if update is possible - `409` if references would break |
| PUT/POST | T | T | - | - | `409` (in case of structural changes)|
| PUT/POST | - | - | - | - | `422` see section **Server** |

<sup>T: True, F: False, I: Irrelevant, -: Not applicable</sup>

#### Partially update Item Schemes

In the case of Item Schemes, it is possible to submit a partial Item Scheme, including the Items that must be updated.
The usage of `POST` and `PUT` is the same as in the case of full replacement. The only difference is that the submitted Items Schemes to be partially updated (i.e. not completely replaced) must inlcude the flag `isPartial="true"`.

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
- MUST return a `SubmitStructureResponse` message with the result of the action, according to the `Accept` header, or the default, if the `Accept` type is not supported (currently available only in SDMX-ML 2.1);
- MUST set the `Content-type` according to the returned format;
- MUST respond with `422` in case of resource type mismatch, i.e. the artefact identified in the URL does not match either to the resource type or identification of the included SDMX Artefact.

```
11.2.  422 Unprocessable Entity
(https://tools.ietf.org/html/rfc4918#section-11.2)
   The 422 (Unprocessable Entity) status code means the server
   understands the content type of the request entity (hence a
   415(Unsupported Media Type) status code is inappropriate), and the
   syntax of the request entity is correct (thus a 400 (Bad Request)
   status code is inappropriate) but was unable to process the contained
   instructions.  For example, this error condition may occur if an XML
   request body contains well-formed (i.e., syntactically correct), but
   semantically erroneous, XML instructions.
```

##### Response

The `SubmitStructureResponse` message must be returned in any case (success, partial success, failure).
In SDMX 2.1 this is defined as part of the `RegistryInterface` messages.
The details of the message are explained in section **Structure response message**, below.

The following matrix summarises the returned `HTTP` response codes.

| Method | Exists | Is Final | Is Referenced | Refs exist/ provided | Response Code |
|---|---|---|---|---|---|
| PUT/POST | T | F | F | T | `200` (successful) or `207` (partially successful)  |
| PUT/POST | T | F | F | F | `409` |
| PUT/POST | T | F | T | T | `200` if update is possible - `409` if references would break |
| PUT/POST | T | T | - | - | `409` (in case of structural changes) |
| PUT/POST | - | - | - | - | `422` see section **Server** |

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
- MUST return a `SubmitStructureResponse` message with the result of the action, according to the `Accept` header, or the default, if the `Accept` type is not supported (currently available only in SDMX-ML 2.1);
- MUST respond with `404` if the resource was not found;

##### Response

The `SubmitStructureResponse` message must be returned in any case (success, failure).
In SDMX 2.1 this is defined as part of the `RegistryInterface` messages.
The details of the message are explained in section **Structure response message**, below.

The following matrix summarises the returned `HTTP` response codes.

| Method | Exists | Is Final | Is Referenced | Refs exist/ provided | Response Code |
|---|---|---|---|---|---|
| DELETE | F | - | - | - | `404` |
| DELETE | T | F | F | I | `200` |
| DELETE | T | T | I | I | `409` |
| DELETE | T | F | T | I | `409` |

<sup>T: True, F: False, I: Irrelevant, -: Not applicable</sup>


### Structure response message

The `SubmitStructureResponse` message must be returned in any case (success, partial success, failure).
In SDMX 2.1 this is defined as part of the `RegistryInterface` messages.
This message includes the following information per submitted Artefact:
- The `action`, e.g. `Append`, `Replace` or `Delete` (`Information` is also available)
- A reference to a specific Maintainable Artefact
- A status message with the result of the action, which contains:
  - The status, e.g. `Success`, `Failure` or `Warning`
  - One or more message texts to explain the result (we need only one per Artefact), which in turn contains:
    - A code (could be the HTTP code)
    - A multilingual text message

An example is shown below:

```xml
<reg:SubmissionResult>
  <reg:SubmittedStructure action="Append"> <!-- Append|Delete|Replace -->
    <reg:MaintainableObject>
      <Ref agencyID="SDMX" id="CL_FREQ" version="1.0" 
        package="codelist" class="Codelist" />
    </reg:MaintainableObject>
  </reg:SubmittedStructure>
  <reg:StatusMessage status="Success"> <!-- Success|Failure|Warning -->
    <reg:MessageText code="204"> <!-- Could be the HTTP code 200, 201, 204, ... -->
      <com:Text xml:lang="en">Codelist successfully deleted</com:Text>
      <com:Text xml:lang="en">Codelist supprimé avec succès</com:Text>
    </reg:MessageText>
  </reg:StatusMessage>
</reg:SubmissionResult>
```

Especially when different results occur on the Artefacts (e.g. partial success), the following should occur:
- Return a multi-status return code (like `207`);
- Return a JSON/XML message with the results details (currently available only in SDMX-ML 2.1);

In the case of a multi-status response, the `SubmitStructureResponse` message will include the corresponding code per Artefact, e.g.:

```xml
<reg:SubmissionResult>
  <reg:SubmittedStructure action="Append">
    <reg:MaintainableObject>
      <Ref agencyID="SDMX" id="CODELIST" version="1.0" 
        package="codelist" class="Codelist"/>
    </reg:MaintainableObject>
  </reg:SubmittedStructure>
  <reg:StatusMessage status="Warning">
    <reg:MessageText code="201"> 
      <com:Text xml:lang="en">Successfully created Codelist</com:Text>
    </reg:MessageText>
  </reg:StatusMessage>
</reg:SubmissionResult>

<reg:SubmissionResult>
  <reg:SubmittedStructure action="Delete"> <!-- Append|Delete|Replace -->
    <reg:MaintainableObject>
      <Ref agencyID="SDMX" id="CL_FREQ" version="1.0" 
        package="codelist" class="Codelist" />
    </reg:MaintainableObject>
  </reg:SubmittedStructure>
  <reg:StatusMessage status="Success"> <!-- Success|Failure|Warning -->
    <reg:MessageText code="204"> <!-- Could be the HTTP code 200, 201, 204, ... -->
      <com:Text xml:lang="en">Codelist successfully deleted</com:Text>
      <com:Text xml:lang="en">Codelist supprimé avec succès</com:Text>
    </reg:MessageText>
  </reg:StatusMessage>
</reg:SubmissionResult>
```


### Nested Items

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
			<com:Name xml:lang="en">Government finance, fiscal and public sector 
        statistics</com:Name>
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
<str:CategoryScheme agencyID="SDMX" id="STAT_SUBJECT_MATTER" version="1.0" isPartial="true">
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
	</str:Category>
	<str:Category id="ENVIRONMENT_MULTIDOMAIN_STAT">
		<com:Name xml:lang="en">Environment and multi-domain statistics</com:Name>
	</str:Category>
</str:CategoryScheme>
```

Moreover, if we wanted to add a new Category under `SECTORAL_STAT` in the initial Category Scheme, we would need to submit the whole new tree of `ECO_STAT`, i.e.:

```xml
<str:CategoryScheme agencyID="SDMX" id="STAT_SUBJECT_MATTER" version="1.0">
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
			<com:Name xml:lang="en">Government finance, fiscal and public sector 
        statistics</com:Name>
		</str:Category>
	</str:Category>
</str:CategoryScheme>
```

### Summary of HTTP response codes

| Method | Exists | Is Final | Is Referenced | Refs exist/ provided | Response Code |
|---|---|---|---|---|---|
| POST | F | - | - | T | `201` (successful) or `207` (partially successful) |
| POST | F | - | - | F | `409` failed references |
| PUT/POST | T | F | F | T | `200` (successful) or `207` (partially successful)  |
| PUT/POST | T | F | F | F | `409` |
| PUT/POST | T | F | T | T | `200` if update is possible - `409` if references would break |
| PUT/POST | T | T | - | - | `409` (in case of structural changes)|
| PUT/POST | - | - | - | - | `422` see section **Server** |
| DELETE | F | - | - | - | `404` |
| DELETE | T | F | F | I | `200` |
| DELETE | T | T | I | I | `409` |
| DELETE | T | F | T | I | `409` |

<sup>T: True, F: False, I: Irrelevant, -: Not applicable</sup>


### Examples

#### Difference between replace and partially update

To explain the updating semantics, the difference between using replacing and partially updating the same SDMX Artefact shall be illustrated in the following example. 
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
