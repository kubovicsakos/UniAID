# Git Conventions

- **Branch Naming**
    - Use short, descriptive names (e.g. `feature/login-screen`, `bugfix/typo`).
    - Keep branch names all in lowercase, with hyphens for word separators.

- **Commit Message Format**
    1. **Type**: feat/fix/refactor/perf/style/test/docs/build/ops/chore
    2. **Subject Line**: Summarize changes in one line (max. 75 chars).
  3**Body**: Explain what and why (optional), wrapping lines at about 75 chars.
    
- **Commit Message Types**
    - **feat**: Add or remove new feature.
    - **fix**: A bug fix of a feat.
    - **refactor**: A code change that doesn't change behaviour.
    - **perf**: A code change that improves performance.
    - **style**: Changes that do not affect the meaning of the code (white-space, formatting, etc.).
    - **test**: Adding missing tests or correcting existing tests.
    - **docs**: Documentation only changes.
    - **build**: Changes that affect the build system or external dependencies.
    - **ops**: Changes related to operations or deployment.
    - **chore**: Miscellaneous commits.

- **Examples**
**Without Body**
    - `feat: add login feature`
    - `fix: correct typo in README`
    - `refactor: simplify user authentication logic`
    - `perf: optimize image loading`
    - `style: format code with Prettier`
    - `test: add unit tests for user service`
    - `docs: update API documentation`
    - `build: upgrade dependencies`
    - `ops: configure CI/CD pipeline`
    - `chore: update .gitignore file`
**With Body**
    - `feat: add login feature`
        ```
        Added a new login feature to the application.
        
        - Implemented user authentication using JWT.
        - Added UI components for login screen.
        - Updated navigation to include login flow.
        ```
    - `fix: correct typo in README`
        ```
        Fixed a typo in the README file.
        
        Changed "recieve" to "receive".
        ```
    - `refactor: simplify user authentication logic`
        ```
        Refactored the user authentication logic to improve readability and maintainability.
        
        - Removed redundant checks.
        - Simplified error handling.
        ```