// The commit-type table in CONTRIBUTING.md ("## Commits") mirrors
// @commitlint/config-conventional's default type-enum exactly, so extending
// the shared config is all the enforcement we need — no type-enum override.
module.exports = {
  extends: ['@commitlint/config-conventional'],
};
