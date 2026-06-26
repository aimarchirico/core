#!/usr/bin/env node

const { execSync } = require('child_process');
const path = require('path');
const fs = require('fs');
const https = require('https');
const http = require('http');

const packageRoot = path.resolve(__dirname, '..');
const repoRoot = path.resolve(packageRoot, '../../..');

try {
  require('dotenv').config({ path: path.resolve(repoRoot, 'frontend/.env') });
} catch (e) {
}

const apiUrl = process.env.API_URL;
if (!apiUrl) {
  console.log('API_URL not set, skipping API generation.');
  process.exit(0);
}

const cfClientId = process.env.CF_ACCESS_CLIENT_ID;
const cfClientSecret = process.env.CF_ACCESS_CLIENT_SECRET;

async function fetchSpec() {
  const specUrl = `${apiUrl}/v3/api-docs`;
  const headers = {};
  
  if (cfClientId && cfClientSecret) {
    console.log('Using Cloudflare Access service token');
    headers['CF-Access-Client-Id'] = cfClientId;
    headers['CF-Access-Client-Secret'] = cfClientSecret;
  }

  return new Promise((resolve, reject) => {
    const client = specUrl.startsWith('https') ? https : http;
    const req = client.get(specUrl, { headers }, (res) => {
      if (res.statusCode !== 200) {
        reject(new Error(`Failed to fetch spec: ${res.statusCode}`));
        return;
      }
      let data = '';
      res.on('data', chunk => data += chunk);
      res.on('end', () => resolve(data));
    });
    req.on('error', reject);
  });
}

function generateClient(specPath) {
  console.log('Generating API client...');
  const outputDir = path.resolve(packageRoot, 'src/generated');
  const cmd = `rm -rf "${outputDir}" && npx @openapitools/openapi-generator-cli generate -i "${specPath}" -g typescript-axios -o "${outputDir}"`;
  execSync(cmd, { stdio: 'inherit', cwd: packageRoot });
  console.log(`OpenAPI client generated at ${outputDir}`);
}

function generateDocs(specPath) {
  console.log('Generating API documentation...');
  const docsDir = path.resolve(repoRoot, 'docs');
  if (!fs.existsSync(docsDir)) {
    fs.mkdirSync(docsDir);
  }

  const outputPath = path.resolve(docsDir, 'API.md');
  const cmd = `npx widdershins --code "${specPath}" -o "${outputPath}"`;
  execSync(cmd, { stdio: 'inherit', cwd: packageRoot });
  console.log(`OpenAPI documentation generated at ${outputPath}`);
}

async function main() {
  try {
    console.log('Fetching OpenAPI spec from', apiUrl);
    const spec = await fetchSpec();
    const specPath = path.resolve(packageRoot, 'openapi-spec.json');
    fs.writeFileSync(specPath, spec);

    generateClient(specPath);
    generateDocs(specPath);

    fs.unlinkSync(specPath);
    console.log('Done.');
  } catch (e) {
    console.error('API generation failed:', e.message || e);
    process.exit(2);
  }
}

main();
