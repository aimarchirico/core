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
  - `@aimarchirico/core-markdown` — shared markdownlint configuration and lint
    task.
  - `@aimarchirico/core-openapi` — OpenAPI client/documentation generator.
  - `@aimarchirico/core-docs` — single source of truth for the contributing
    guide and GitHub templates.
- **`skills/`** — agent skills (`commit`, `docs`, `implement`, `issues`,
  `pr`) added to a consumer via `npx skills add aimarchirico/core --skill
  <name>`.

## Usage

Consumers depend on the published artifacts and import the self-contained
Taskfiles from the installed packages, for example:

```yaml
includes:
  api:
    taskfile: ./node_modules/@aimarchirico/core-openapi/Taskfile.yml
```

Shared repository files (contributing guide, pull request and issue
templates) are written into a consumer with:

```sh
task docs:core-docs:materialize
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
