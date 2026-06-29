import coreConfig from "@aimarchirico/core-ts/eslint";

export default [
  {
    ignores: [
      'node_modules/**',
      'packages/core-ts/eslint.config.mjs',
      'packages/core-ts/bin/cli.js',
      'packages/core-conventions/bin/cli.js',
      'packages/core-conventions/markdownlint.cjs',
      'packages/core-api/bin/cli.js',
      'eslint.config.mjs'
    ],
  },
  ...coreConfig,
];
