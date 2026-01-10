#!/usr/bin/env bash
set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_DIR="${SCRIPT_DIR}/.."

echo "Instalando dependencias del backend..."
cd "${REPO_DIR}/stock_backend"
npm install

echo "¡Listo! Para arrancar el servidor:"
echo "  cd stock_backend && npm start"