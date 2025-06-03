#!/bin/sh

#!/bin/bash
set -e

echo "Testing ping"
curl --fail http://localhost:8000/ping

curl -X POST http://localhost:8000/register \
  -H "Content-Type: application/json" \
  -d '{"email": "user@example.com", "username": "testuser", "password": "testpass"}'

curl -X POST http://localhost:8000/login \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "testpass"}'

curl -X GET http://localhost:8000/user

curl -X POST http://localhost:8000/users/1/transform-trainer

curl -X GET http://localhost:8000/trainers

curl -X POST http://localhost:8000/users/2/assign-trainer/3

curl -X POST http://localhost:8000/abonament \
  -H "Content-Type: application/json" \
  -d '{"id_user": 4, "tip_abonament": "Standard"}'

curl -X POST http://localhost:8000/abonament/dezactivare \
  -H "Content-Type: application/json" \
  -d '{"id_user": 4}'

curl -X POST http://localhost:8000/messages \
  -H "Content-Type: application/json" \
  -d '{"id_conversation": 1, "id_sender": 4, "mesaj": "Hello, trainer!"}'

curl -X POST http://localhost:8000/conversations/1/users/4/mark-seen

echo "All APIs are working!"
