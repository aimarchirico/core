import {defineConfig} from 'eslint/config';
import expoConfig from 'eslint-config-expo/flat.js';
import baseConfig from '@aimarchirico/core-ts/eslint';

export default defineConfig([...expoConfig, ...baseConfig]);
