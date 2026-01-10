# Enabling Sonar scans in CI 🔎

This repository includes a GitHub Actions workflow at `.github/workflows/ci.yml` which runs backend tests and Android unit tests on pushes and pull requests. The workflow also includes an optional Sonar scan step that runs only when Sonar secrets are configured.

How to enable Sonar scanning:

1. Obtain a Sonar token and host URL
   - For SonarCloud: create a token from your SonarCloud account (https://sonarcloud.io) and use `https://sonarcloud.io` as the host URL.
   - For a self-hosted SonarQube: use your instance's URL and generate a token from your user profile.

2. Add repository secrets on GitHub
   - Go to the repository -> Settings -> Secrets and variables -> Actions -> New repository secret
   - Add `SONAR_HOST_URL` (e.g., `https://sonarcloud.io`) and `SONAR_TOKEN` (the token value)

3. Trigger the workflow
   - Push a commit or open a pull request. The `ci` workflow will run; the Sonar job will run only when both `SONAR_HOST_URL` and `SONAR_TOKEN` secrets are present.

Notes:
- The Sonar step uses the official `sonarsource/sonar-scanner-cli` Docker image. You can customize the command in `.github/workflows/ci.yml` if needed.
- If you use SonarCloud, make sure your Sonar organization/project is configured to accept analyses from GitHub Actions.

If you want, I can prepare the exact Sonar project configuration or help create a SonarCloud organization and link it to the repo. ✅