#!/bin/sh

#!/bin/bash
set -e

echo "Testing ping"
curl --fail http://localhost:8000/ping

echo "All APIs are working!"
