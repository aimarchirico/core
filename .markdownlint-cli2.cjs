const baseConfig = require('@aimarchirico/core-conventions/markdownlint');

module.exports = {
  ...baseConfig,
  ignores: [
    '**/node_modules/**',
    '**/build/**',
    '**/dist/**',
    '**/.gradle/**',
    '**/.git/**',
    '**/tmp/**'
  ]
};
