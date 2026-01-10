#!/usr/bin/env bash
set -eo pipefail

# Poll SonarQube/Cloud API to check Quality Gate status for the project.
# Requires environment variables: SONAR_HOST_URL, SONAR_TOKEN, PROJECT_KEY

SONAR_HOST_URL=${SONAR_HOST_URL:?}
SONAR_TOKEN=${SONAR_TOKEN:?}
PROJECT_KEY=${PROJECT_KEY:-stockapp}
TIMEOUT=${TIMEOUT:-180}
SLEEP=${SLEEP:-5}

ELAPSED=0
while [ "$ELAPSED" -lt "$TIMEOUT" ]; do
  # Fetch project quality gate status
  STATUS=$(curl -s -u "$SONAR_TOKEN": "$SONAR_HOST_URL/api/qualitygates/project_status?projectKey=$PROJECT_KEY" | python3 -c "import sys,json;print(json.load(sys.stdin)['projectStatus']['status'])")
  if [ "$STATUS" != "IN_PROGRESS" ]; then
    echo "Quality Gate status: $STATUS"
    if [ "$STATUS" != "OK" ]; then
      echo "Quality Gate failed"
      exit 1
    fi
    exit 0
  fi
  echo "Quality Gate in progress, waiting... ($ELAPSED/$TIMEOUT)"
  sleep "$SLEEP"
  ELAPSED=$((ELAPSED + SLEEP))
done

echo "Timed out waiting for Sonar quality gate after $TIMEOUT seconds"
exit 1
