# Overview

> [!IMPORTANT]
> For the best reading experience, use the SDMX documentation website:
> [SDMX REST API: Developers' documentation](https://sdmx-twg.github.io/sdmx-docs/3.1/rest_api/)

This repository is used to maintain the SDMX REST API.

The API allows implementers to offer programmatic access to statistical data and metadata over HTTP.

This repository contains:

- The normative part of the specification, i.e. the [Open API definition](api/sdmx-rest.yaml).
- The [Developers' documentation](doc/index.md), including a [cheat sheet](doc/rest_cheat_sheet.pdf?raw=true).

> [!TIP]
> Each release of the SDMX-REST API is associated with a tag. Retrieving **previous versions** of the SDMX-REST API (Open API definition, cheatsheet, documentation, etc.) is
> easy, using either **tags** or **releases**:
>
> * **tags**: Tags can be used to retrieve the Open API definition, cheatsheet and documentation of a specific version of the SDMX-REST API. Tags are located towards the top of this page.
> * **releases**: Releases are located in the navigation bar on the right of this page. They too can be used to retrieve the Open API definition, cheatsheet and documentation of a specific version of the SDMX-REST API.
## Repository Structure

-   `docs/` — Markdown content pages for the REST API and registry
    specification documentation.
-   `api/sdmx-rest.yaml` — normative OpenAPI definition.

## Version Branches

Each minor release of this component is maintained on a dedicated documentation
branch following the naming convention `docs_vX.Y` (e.g., `docs_v2.1`,
`docs_v3.0`).

These branches exist solely to support the documentation website and are not
used for regular development. Changes to the specification continue to go
through the normal development and release process (via `develop`). Older
documentation branches may additionally require file reorganization and
formatting adaptations for MkDocs.

The branch tracked by the
[`sdmx-docs`](https://github.com/sdmx-twg/sdmx-docs) parent repository is
declared in `.gitmodules` at the root of that repo. Switching the tracked
branch in the parent repository is how a new version of this component is
published on the documentation site.


## Formatting Conventions

For Markdown and MkDocs formatting conventions that apply to content in `docs/`,
see the [`sdmx-docs` README](https://github.com/sdmx-twg/sdmx-docs#readme).