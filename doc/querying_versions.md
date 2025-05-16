# Query syntax for specific, latest or all artefact versions

The SDMX REST API supports retrievals of specific, latest or all artefact versions according to the SDMX semantic versioning & dependency management rules ([cf. Section 4.3 of the SDMX Technical Notes](https://sdmx.org/wp-content/uploads/SDMX_3-0-0_SECTION_6_FINAL-1_0.pdf)) by means of the [SDMX REST API wildcard operators](https://github.com/sdmx-twg/sdmx-rest/blob/master/doc/index.md#use-of-operators).

Let's use the version form `X.Y.Z-EXT`, where 
- `X` is a non-negative number representing the MAJOR version part
- `Y` is a non-negative number representing the MINOR version part
- `Z` is a non-negative number representing the PATCH version part
- `-EXT` is a constrained character string representing the version EXTENSION part 

Capital letters `X`, `Y`, `Z` and `EXT` are used for the queried version parts and the exactly corresponding response version parts. In case the response version parts could be different from the version parts used in the query, we will use the small characters `x`, `y`, `z` and `ext` instead. Last version parts are omitted as required.

## Query for a specific version

- `X` This matches version **`X`**. Note that this version format is deprecated.
- `X.Y` This matches version **`X.Y`**.
- `X.Y.Z` This matches version **`X.Y.Z`**. 
- `X.Y.Z-EXT` This matches version **`X.Y.Z-EXT`**.

## Query for the latest stable (semantic) version 

### Without specific minimum version
- `+` This matches the _latest available stable semantic_ version of an artefact in form of **`x.y.z`** where `x` > 0. It is equivalent to querying for:
  - `+.0.0` or
  - `1+.0.0`
- `X.+.0` With `X` > 0. This matches the _latest available stable semantic_ version of an artefact in form of **`X.y.z`**. It is equivalent to querying for:
  - `X.0+.0`
- `X.Y.+` With `X` > 0. This matches the _latest available stable semantic_ version of an artefact in form of **`X.Y.z`**. It is equivalent to querying for:
  - `X.Y.0+`

### With specific minimum version
- `X+.Y.Z` With `X` > 0. This matches the _latest available stable semantic_ version of an artefact in form of **`x.y.z`** where `x.y.z` >= `X.Y.Z`.
- `X.Y+.Z` With `X` > 0. This matches the _latest available stable semantic_ version of an artefact in form of **`X.y.z`** where `y.z` >= `Y.Z`.
- `X.Y.Z+` With `X` > 0. This matches the _latest available stable semantic_ version of an artefact in form of **`X.Y.z`** where `z` >= `Z`.

## Query for the latest possibly unstable version 

### Without specific minimum version
- `~` This matches the _latest available_ version of an artefact in form of **`x`**, **`x.y`**, **`x.y.z`** or **`x.y.z-ext`**.
- `~.0` This matches the _latest available_ version of an artefact in form of **`x.y`**. It is equivalent to querying for:
  - `0~.0`
- `~.0.0` This matches the _latest available_ version of an artefact in form of **`x.y.z`** or **`x.y.z-ext`**. It is equivalent to querying for:
  - `0~.0.0`
- `X.~` This matches the _latest available_ version of an artefact in form of **`X.y`**. It is equivalent to querying for:
  - `X.0~`
- `X.~.0` This matches the _latest available_ version of an artefact in form of **`X.y.z`** or **`X.y.z-ext`**. It is equivalent to querying for:
  - `X.0~.0`
- `X.Y.~` This matches the _latest available_ version of an artefact in form of **`X.Y.z`** or **`X.Y.z-ext`**. It is equivalent to querying for:
  - `X.Y.0~`

### With specific minimum version
- `X~.Y` This matches the _latest available_ version of an artefact in form of **`x.y`** where `x.y` >= `X.Y`.
- `X~.Y.Z` This matches the _latest available_ version of an artefact in form of **`x.y.z`** or **`x.y.z-ext`** where `x.y.z` >= `X.Y.Z`.
- `X.Y~` This matches the _latest available_ version of an artefact in form of **`X.y`** where `y` >= `Y`.
- `X.Y~.Z` This matches the _latest available_ version of an artefact in form of **`X.y.z`** or **`X.y.z-ext`** where `y.z` >= `Y.Z`.
- `X.Y.Z~` This matches the _latest available_ version of an artefact in form of **`X.Y.z`** or **`X.Y.z-ext`** where `z` >= `Z`.

## Query for all versions

### Without specific minimum version
- `*` This matches _all_ versions of an artefact in form of **`x`**, **`x.y`**, **`x.y.z`** or **`x.y.z-ext`**
- `*.0` This matches _all_ versions of an artefact in form of **`x.y`**. It is equivalent to querying for:
  - `0*.0`
- `*.0.0` This matches _all_ versions of an artefact in form of **`x.y.z`** or **`x.y.z-ext`**. It is equivalent to querying for:
  - `0*.0.0`
- `X.*` This matches _all_ versions of an artefact in form of **`X.y`**. It is equivalent to querying for:
  - `X.0*`
- `X.*.0` This matches _all_ versions of an artefact in form of **`X.y.z`** or **`X.y.z-ext`**. It is equivalent to querying for:
  - `X.0*.0`
- `X.Y.*` This matches _all_ versions of an artefact in form of **`X.Y.z`** or **`X.Y.z-ext`**. It is equivalent to querying for:
  - `X.Y.0*`

### With specific minimum version
- `X*.Y` This matches _all_ versions of an artefact in form of **`x.y`** where `x.y` >= `X.Y`.
- `X*.Y.Z` This matches _all_ versions of an artefact in form of **`x.y.z`** or **`x.y.z-ext`** where `x.y.z` >= `X.Y.Z`.
- `X.Y*` This matches _all_ versions of an artefact in form of **`X.y`** where `y` >= `Y`.
- `X.Y*.Z` This matches _all_ versions of an artefact in form of **`X.y.z`** or **`X.y.z-ext`** where `y.z` >= `Y.Z`.
- `X.Y.Z*` This matches _all_ versions of an artefact in form of **`X.Y.z`** or **`X.Y.z-ext`** where `z` >= `Z`.

## `OR` operator

Any of the version query strings above can be combined with the OR operator (`,`). For example:
- `1~.2.0,1.2.0+`
- `+,1.2.1*` 

## Unsupported syntax

The following version query syntax cannot be correctly interpreted and is **not supported**.

Using a positive number in version parts after a part that is fully wildcarded with `+`, `~` or `*`:  
- `+.2.3` Use `+.0.0` instead. 
- `1.+.3` Use `1.+.0` instead.
- `~.2` Use `~.0` instead.
- `~.2.3` Use `~.0.0` instead.
- `1.~.3` Use `1.~.0` instead.
- `*.2` Use `*.0` instead.
- `*.2.3` Use `*.0.0` instead.
- `1.*.3` Use `1.*.0` instead.

Using `+` (querying for stable versions always having 3 parts) with only 2 version parts:  
- `+.0` Use `+` or `+.0.0` instead.
- `2.3+` Use `2.3+.0` instead.

Using two or more of the `+`, `~` or `*` wildcard operators within the same version:  
- `~.0.*` Use `~` or `~.0.0` instead.
- `3.2*.1+` Use `3.2*.1`, `3.2.1+` or `3.2*.1,3.2.1+` instead.
- `3.2+.1+` Use `3.2+.1` instead.
