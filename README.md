## Introduction

Backend application that will simulate sports betting event
outcome handling and bet settlement via Kafka and RocketMQ.

* An API endpoint to publish a sports event outcome to Kafka.
* A Kafka consumer that listens to event-outcomes Kafka named-topic.
* Matches the event outcome to bets that need to be settled.
* A RocketMQ producer that sends messages to bet-settlements.
* A RocketMQ Consumer that listens to bet-settlements.

## 🛠 Tech Stack
* **Language:** Java 21
* **Framework:** Spring Boot 3.2+
* **Messaging:** Apache Kafka (Ingestion), Apache RocketMQ (Internal Job Queue)
* **Database:** H2 In-Memory (JPA/Hibernate)
* **Build Tool:** Maven

## 🚀 How to Run

### Prerequisites
* Docker & Docker Compose
* Java 21 SDK
* Maven

### Step 1: Start Infrastructure
We need Zookeeper, Kafka and Broker running.
```bash
docker-compose up -d
```

### Step 2: Run the Application
```bash
mvn spring-boot:run
```

The app will start on port 8080. On startup, the **LocalDataIngestion** will automatically create 2 test bets for Event ID EVT_123.
These data can be found in the H2 database, the database properties are as follows:

* URL: http://localhost:8080/h2-console
* JDBC URL: jdbc:h2:mem:bettingdb
* User: sa
* Password: password

### Step 3: Trigger a Settlement
Simulate an event outcome where Team A wins (Bet 1 wins, Bet 2 loses).

```bash
curl -X POST http://localhost:8080/api/v1/event/outcome \
-H "Content-Type: application/json" \
-d '{
    "eventId": "EVT_123",
    "eventName": "Championship Final",
    "winnerId": "TEAM_A"
}'
```

### Step 4: Check the Logs (Verify Results)
Check the logs in your terminal. You should see:

1. Publishing Event Outcome: *{eventId}*. Winner: *{winnerId}*
2. Received BetOutcome for Event: *{eventId}*
3. Sending settlement command for Bet ID: *{betId}*
4. Processing Settlement for Bet ID: *{betId}*
5. Bet *{betId}* settled as *{betOutcome}*. Amount: *{amount}*