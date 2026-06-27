# Core

Reusable foundation for Chirico services: shared backend modules, frontend
packages, and agent skills consumed by downstream repositories such as the
service template.

## What's Inside

The repository is a monorepo with three roots:

- **`modules/`** — Gradle/Kotlin backend components published as Maven
  artifacts (`core-security`, `core-test`) plus the shared convention plugin
  (`core-build-logic`, applied as `id("core.kotlin")`).
- **`packages/`** — npm packages published to GitHub Packages:
  - `@aimarchirico/core-eslint` — shared ESLint configuration.
  - `@aimarchirico/core-typescript` — shared TypeScript configuration.
  - `@aimarchirico/core-markdown` — shared markdownlint configuration.
  - `@aimarchirico/core-openapi` — OpenAPI client/documentation generator CLI.
  - `@aimarchirico/core-docs` — single source of truth for the contributing
    guide and GitHub templates.
- **`skills/`** — agent skills (`commit`, `docs`, `implement`, `issues`,
  `pr`) added to a consumer via `npx skills add aimarchirico/core --skill
  <name>`.

## Usage

Backend modules apply the convention plugin and depend on the libraries:

```kotlin
plugins { id("core.kotlin") }

dependencies {
  implementation("no.chirico.core:core-security")
  testImplementation("no.chirico.core:core-test")
}
```

Frontend packages expose configs (extended in eslint/tsconfig/markdownlint)
and CLIs:

```sh
npx core-openapi   # generate the OpenAPI client and docs
npx core-docs      # write CONTRIBUTING.md and .github templates into the repo
```

## Development

Requires Java 25, Node 20+, and [Task](https://taskfile.dev).

```sh
npm install
task check
```

- `task build` — build the backend modules.
- `task check` — run module tests, quality checks, and Markdown lint.
- `task fix` — format and auto-fix.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).
