# Security

## Repository rules

- Never commit `mongodb.properties`.
- Never commit course access files, passwords, tokens, private keys, or personal database exports.
- Do not place credentials directly in Java source, Maven files, OpenAPI examples, screenshots, or documentation.
- Use `mongodb.example.properties` only for placeholder values.
- Review staged changes before every push with `git diff --cached`.

## Secret checks

Before publishing, inspect tracked content for common credential patterns:

```bash
git grep -nEi 'password|passwd|secret|token|api[_-]?key|mongodb(\\+srv)?://'
```

Expected matches should be limited to configuration key names and explicit placeholder documentation. Add a dedicated secret scanner in CI if the repository becomes actively maintained.

## Incident response

If a credential is committed or shared:

1. Rotate or revoke it immediately.
2. Remove it from the current tree.
3. Remove it from Git history before wider distribution.
4. Verify the rewritten history and all release artifacts.
5. Notify affected collaborators.

Deleting a visible file is not sufficient once the value has entered Git history.
