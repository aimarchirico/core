#!/usr/bin/env node

const { execSync } = require('child_process');
const path = require('path');
const fs = require('fs');
const https = require('https');
const http = require('http');

const packageRoot = path.resolve(__dirname, '..');

// Find frontend/.env by searching up the directory tree
function findFrontendEnv() {
  let dir = process.cwd();
  while (dir && dir !== path.parse(dir).root) {
    const envPath = path.join(dir, 'frontend/.env');
    if (fs.existsSync(envPath)) return envPath;
    if (path.basename(dir) === 'frontend' && fs.existsSync(path.join(dir, '.env'))) return path.join(dir, '.env');
    const siblingPath = path.join(dir, 'template/frontend/.env');
    if (fs.existsSync(siblingPath)) return siblingPath;
    dir = path.dirname(dir);
  }
  const fallback = path.resolve(__dirname, '../../../template/frontend/.env');
  if (fs.existsSync(fallback)) return fallback;
  return null;
}

// Find repository root dynamically
function findRepoRoot() {
  let dir = process.cwd();
  while (dir && dir !== path.parse(dir).root) {
    if (fs.existsSync(path.join(dir, '.git')) || fs.existsSync(path.join(dir, 'package.json'))) {
      return dir;
    }
    dir = path.dirname(dir);
  }
  return path.resolve(packageRoot, '../../..');
}

const envPath = findFrontendEnv();
if (envPath) {
  console.log(`Loading environment variables from ${envPath}`);
  try {
    require('dotenv').config({ path: envPath });
  } catch (e) {
    console.error('Failed to load dotenv:', e);
  }
}

const apiUrl = process.env.API_URL;
if (!apiUrl) {
  console.log('API_URL not set, skipping API generation.');
  process.exit(0);
}

const cfClientId = process.env.CF_ACCESS_CLIENT_ID;
const cfClientSecret = process.env.CF_ACCESS_CLIENT_SECRET;

const repoRoot = findRepoRoot();

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
  const outputDir = process.env.API_CLIENT_OUTPUT_DIR || path.resolve(packageRoot, 'src/generated');
  const cmd = `rm -rf "${outputDir}" && npx @openapitools/openapi-generator-cli generate -i "${specPath}" -g typescript-axios -o "${outputDir}"`;
  execSync(cmd, { stdio: 'inherit', cwd: packageRoot });
  console.log(`OpenAPI client generated at ${outputDir}`);
}

function generateDocs(specPath) {
  console.log('Generating API documentation...');
  const docsDir = process.env.API_DOCS_OUTPUT_DIR || path.resolve(repoRoot, 'docs');
  if (!fs.existsSync(docsDir)) {
    fs.mkdirSync(docsDir, { recursive: true });
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
