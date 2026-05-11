#!/bin/bash
# Tests the running vehicle scanner API with a real photo.
# Usage: ./test.sh /path/to/some-vehicle-photo.jpg

IMAGE="${1:-/Users/user01/Downloads/4976164912_f9defdb4ee_b.jpg}"

echo "Sending: $IMAGE"
echo "------"
curl -s -X POST -F "image=@${IMAGE}" http://localhost:8080/api/inspect | python3 -m json.tool
