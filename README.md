# WhatsApp-API Project: Manual API & Kafka Test Guide

> **Live Swagger UI:**
> [http://13.60.174.32:8000/swagger-ui/index.html#](http://13.60.174.32:8000/swagger-ui/index.html#)
> *Interactively explore and test all endpoints here!*

---

## Table of Contents

1. [Deployment (Docker on EC2)](#deployment-docker-on-ec2)
2. [Prerequisites](#prerequisites)
3. [Create a Chat Room](#create-a-chat-room)
4. [Send a Text Message](#send-a-text-message)
5. [Send a Message with Image or Video Attachment](#send-a-message-with-image-or-video-attachment)
6. [Negative Test: Invalid Attachment](#negative-test-invalid-attachment)
7. [Reply to a Message](#reply-to-a-message)
8. [Emoji Reaction](#emoji-reaction)
9. [Delete a Message](#delete-a-message)
10. [List Messages in a Chat Room](#list-messages-in-a-chat-room)
11. [Kafka Integration Test](#kafka-integration-test)
---

## Deployment (Docker on EC2)

* The API and Kafka services are deployed on an AWS EC2 instance using Docker.
* **API Base URL:** `http://13.60.174.32:8000`
* **Swagger UI:** [http://13.60.174.32:8000/swagger-ui/index.html#](http://13.60.174.32:8000/swagger-ui/index.html#)
* Ensure your EC2 security group allows inbound access on port 8000.

---

## Prerequisites

* API and Kafka are running and accessible via the public URLs above.
---

## Create a Chat Room

```bash
curl -X POST 'http://13.60.174.32:8000/api/chatrooms' \
  -H 'Content-Type: application/json' \
  -d '{"name": "Test Room"}'
```

---

## Send a Text Message

```bash
curl -X POST 'http://13.60.174.32:8000/api/chatrooms/1/messages' \
  -H 'Content-Type: application/json' \
  -d '{"senderId": 1, "content": "Hello, World!", "messageType": "text"}'
```

---

## Send a Message with Image or Video Attachment

**Image Example:**

```bash
curl -X POST 'http://13.60.174.32:8000/api/chatrooms/1/messages' \
  -H 'accept: */*' \
  -H 'Content-Type: multipart/form-data' \
  -F 'message={"senderId": 1, "content": "Photo upload!", "messageType":"image"}' \
  -F 'file=@your_image.jpg;type=image/jpeg'
```

**Video Example:**

```bash
curl -X POST 'http://13.60.174.32:8000/api/chatrooms/1/messages' \
  -H 'accept: */*' \
  -H 'Content-Type: multipart/form-data' \
  -F 'message={"senderId": 1, "content": "Video upload!", "messageType":"video"}' \
  -F 'file=@your_video.mp4;type=video/mp4'
```

---

## Negative Test: Invalid Attachment

Try to send a non-image/non-video (should fail):

```bash
curl -X POST 'http://13.60.174.32:8000/api/chatrooms/1/messages' \
  -H 'accept: */*' \
  -H 'Content-Type: multipart/form-data' \
  -F 'message={"senderId": 1, "content": "Should fail!", "messageType":"pdf"}' \
  -F 'file=@test.pdf;type=application/pdf'
```

**Expected:**
Response with error, such as:

```
{"message":"Unsupported file type: Only image and video files are allowed."}
```

---

## Reply to a Message

Assuming a message with ID `10` exists in chat room `1`:

```bash
curl -X POST 'http://13.60.174.32:8000/api/chatrooms/1/messages' \
  -H 'Content-Type: application/json' \
  -d '{"senderId": 1, "content": "This is a reply", "messageType": "text", "replyToId": 10}'
```

---

## Delete a Message

```bash
curl -X DELETE 'http://13.60.174.32:8000/api/chatrooms/1/messages/10'
```

**Expected:**
HTTP 204 No Content

---

## List Messages in a Chat Room

```bash
curl -X GET 'http://13.60.174.32:8000/api/chatrooms/1/messages?page=0&size=20'
```

**Expected:**
A JSON page/list of messages, with fields like `attachments`, `replyToId`, etc.

---

## Emoji Reactions

### Add/Update an Emoji Reaction on a Message

```bash
curl -X POST 'http://13.60.174.32:8000/api/messages/10/emoji?userId=1&emoji=love'
```
**Expected:**
Returns a JSON object for the new/updated emoji reaction.

---

### List Emoji Reactions for a Message

```bash
curl -X GET 'http://13.60.174.32:8000/api/messages/10/emoji?page=0&size=20'
```

**Expected:**
Paginated list of all emoji reactions for the given message.

---

## Kafka Integration Test

* **Consumer:**
  On the EC2 instance where Kafka is running, use:

  ```bash
  kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic chat-messages --from-beginning
  ```
* **Producer:**
API call that sends a message should trigger a Kafka event.
* **Expected:**
  As of now the messages would appear in the consumer terminal.

---
