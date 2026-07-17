#!/bin/bash
set -euo pipefail

cd "$(dirname "$0")/.."

echo "[verify] backend"
vibehopper_be/scripts/verify.sh

echo "[verify] ALL GREEN"
