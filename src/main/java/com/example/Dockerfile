version: '3.8'

services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    ports:
      - "9092:9092"
    depends_on:
      - zookeeper
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:9092
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

  person-service:
    build:
      context: ./person_service
    ports:
      - "8081:8081"

  medical-api-service:
    build:
      context: ./medical_api_service
    ports:
      - "8080:8080"
    environment:
      - SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
      - PERSON_SERVICE_URL=http://person-service:8081

  qr-service:
    build:
      context: ./qr_code_storage_service
    ports:
      - "8082:8082"
    environment:
      - SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092

  api-gateway:
    build:
      context: ./api_gateway
    ports:
      - "8083:8083"
    environment:
      - PERSON_SERVICE_URL=http://person-service:8081
      - MEDICAL_SERVICE_URL=http://medical-api-service:8080
      - QR_SERVICE_URL=http://qr-code-storage-service:8082
