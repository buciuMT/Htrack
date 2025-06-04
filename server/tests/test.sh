#!/bin/sh
set -e

BASE_URL="http://localhost:8000"

echo "Testing ping"
curl "$BASE_URL/ping"

echo "Registering new user"
curl -X POST "$BASE_URL/register" \
  -H "Content-Type: application/json" \
  -d '{"email": "user@example.com", "username": "testuser", "password": "testpass"}'

echo "Logging in"
curl -X POST "$BASE_URL/login" \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "testpass"}'

echo "Getting user info"
curl -X GET "$BASE_URL/user"

echo "Transform user to trainer"
curl -X POST "$BASE_URL/users/1/transform-trainer"

echo "Get all trainers"
curl -X GET "$BASE_URL/trainers"

echo "Assign trainer to user"
curl -X POST "$BASE_URL/users/2/assign-trainer/3"

echo "Create subscription"
curl -X POST "$BASE_URL/abonament" \
  -H "Content-Type: application/json" \
  -d '{"id_user": 4, "tip_abonament": "Standard"}'

echo "Deactivate subscription"
curl -X POST "$BASE_URL/abonament/dezactivare" \
  -H "Content-Type: application/json" \
  -d '{"id_user": 4}'

echo "Mark conversation as seen"
curl -X POST "$BASE_URL/conversations/1/users/4/mark-seen"

echo "Create poll for trainer"
curl -X POST "$BASE_URL/poll" \
  -H "Content-Type: application/json" \
  -d '{"id_trainer": 3}'

echo "Get poll for trainer"
curl -X GET "$BASE_URL/poll/trainer/3"

echo "Get poll for user"
curl -X GET "$BASE_URL/poll/user/2"

echo "All APIs are working!"
