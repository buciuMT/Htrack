#!/bin/sh
set -e

BASE_URL="http://localhost:8000"

echo "Testing ping"
curl --fail "$BASE_URL/ping"

echo "Registering new user"
curl --fail -X POST "$BASE_URL/register" \
  -H "Content-Type: application/json" \
  -d '{"email": "user@example.com", "username": "testuser", "password": "testpass"}'

echo "Logging in"
curl --fail -X POST "$BASE_URL/login" \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "testpass"}'

echo "Getting user info"
curl --fail -X GET "$BASE_URL/user"

echo "Transform user to trainer"
curl --fail -X POST "$BASE_URL/users/1/transform-trainer"

echo "Get all trainers"
curl --fail -X GET "$BASE_URL/trainers"

echo "Assign trainer to user"
curl --fail -X POST "$BASE_URL/users/2/assign-trainer/3"

echo "Create subscription"
curl --fail -X POST "$BASE_URL/abonament" \
  -H "Content-Type: application/json" \
  -d '{"id_user": 4, "tip_abonament": "Standard"}'

echo "Deactivate subscription"
curl --fail -X POST "$BASE_URL/abonament/dezactivare" \
  -H "Content-Type: application/json" \
  -d '{"id_user": 4}'

echo "Mark conversation as seen"
curl --fail -X POST "$BASE_URL/conversations/1/users/4/mark-seen"

echo "Create poll for trainer"
curl --fail -X POST "$BASE_URL/poll" \
  -H "Content-Type: application/json" \
  -d '{"id_trainer": 3}'

echo "Get poll for trainer"
curl --fail -X GET "$BASE_URL/poll/trainer/3"

echo "Get poll for user"
curl --fail -X GET "$BASE_URL/poll/user/2"

echo "Submit vote"
curl --fail -X POST "$BASE_URL/vote" \
  -H "Content-Type: application/json" \
  -d '{"id_poll": 1, "id_user": 2, "ora": 10}'

echo "Vote conflict (should fail if already voted)"
if curl -X POST "$BASE_URL/vote" \
  -H "Content-Type: application/json" \
  -d '{"id_poll": 1, "id_user": 2, "ora": 10}'; then
  echo "Error: expected voting conflict!"
  exit 1
else
  echo "Correctly detected voting conflict"
fi

echo "Get user vote for poll"
curl --fail -X GET "$BASE_URL/votes/1/2"

echo "Update vote hour"
curl --fail -X POST "$BASE_URL/vote/update" \
  -H "Content-Type: application/json" \
  -d '{"id_poll": 1, "id_user": 2, "ora": 12}'

echo "Get all votes for poll"
curl --fail -X GET "$BASE_URL/poll/1/votes"

echo "Get all polls voted by user"
curl --fail -X GET "$BASE_URL/polls/votate/2"

echo "Deactivate poll"
curl --fail -X POST "$BASE_URL/poll/dezactivare/1"

echo "Try to vote on deactivated poll (should fail)"
if curl -X POST "$BASE_URL/vote" \
  -H "Content-Type: application/json" \
  -d '{"id_poll": 1, "id_user": 2, "ora": 15}'; then
  echo "Error: expected vote on deactivated poll to fail!"
  exit 1
else
  echo "Correctly detected voting on deactivated poll"
fi

echo "All APIs are working!"
