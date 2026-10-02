# API Documentation

## Authentication
### POST /api/auth/register
- **Body:** { "username": "admin", "password": "123" }
- **Response:** { "message": "Ðang ký thành công.", "token": "JWT_TOKEN" }

### POST /api/auth/login
- **Body:** { "username": "admin", "password": "123" }
- **Response:** { "message": "Ðang nh?p thành công.", "token": "JWT_TOKEN" }

## Players (Requires Header Authorization: Bearer <JWT>)
### GET /api/players
- **Response:** [ { "id": 1, "fullName": "Messi", "club": "T? do" ... } ]

### POST /api/players
- **Response:** Player object.
