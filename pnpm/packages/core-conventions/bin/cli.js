#!/usr/bin/env node
'use strict';

const fs = require('fs');
const path = require('path');

const packageRoot = path.resolve(__dirname, '..');
const cwd = process.cwd();
const command = process.argv[2];

function init() {
  fs.copyFileSync(
    path.join(packageRoot, 'CONTRIBUTING.md'),
    path.join(cwd, 'CONTRIBUTING.md'),
  );
  fs.cpSync(path.join(packageRoot, 'github'), path.join(cwd, '.github'), {
    recursive: true,
  });
  console.log('Materialized CONTRIBUTING.md and .github templates.');
}

if (command === 'docs') {
  init();
} else {
  console.error(
    'core-conventions: unknown command "' +
      (command || '') +
      '". Expected "docs".',
  );
  process.exit(1);
}
