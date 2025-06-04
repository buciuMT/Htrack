#!/bin/sh

#!/bin/bash
set -e

echo "Testing ping"
curl --fail http://localhost:8000/ping

curl --fail -X POST http://localhost:8000/register \
  -H "Content-Type: application/json" \
  -d '{"email": "user@example.com", "username": "testuser", "password": "testpass"}'

curl --fail -X POST http://localhost:8000/login \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "testpass"}'

curl --fail -X GET http://localhost:8000/user

curl --fail -X POST http://localhost:8000/users/1/transform-trainer

curl --fail -X GET http://localhost:8000/trainers

curl --fail -X POST http://localhost:8000/users/2/assign-trainer/3

curl --fail -X POST http://localhost:8000/abonament \
  -H "Content-Type: application/json" \
  -d '{"id_user": 4, "tip_abonament": "Standard"}'

curl --fail -X POST http://localhost:8000/abonament/dezactivare \
  -H "Content-Type: application/json" \
  -d '{"id_user": 4}'

curl --fail -X POST http://localhost:8000/conversations/1/users/4/mark-seen

echo "All APIs are working!"
