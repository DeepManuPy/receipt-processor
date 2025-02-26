# Receipt Processor

A Spring Boot application that calculates points for receipts based on specific rules.

## Running with Docker

### Prerequisites

- Docker and Docker Compose

### Instructions

1. **Clone or download the repository**
   ```bash
   git clone https://github.com/DeepManuPy/receipt-processor.git
   cd receipt-processor
   ```

2. **Build and run with Docker Compose**
   ```bash
   docker compose up --build
   ```
   This command will:
    - Build the Docker image for the application
    - Start the container
    - The application will be ready when you see log messages indicating the server has started

3. **Access the application**
    - The application will be available at http://localhost:8080
    - API endpoints:
        - POST http://localhost:8080/receipts/process
        - GET http://localhost:8080/receipts/{id}/points

## API Usage

### Process a Receipt

**Endpoint:** `POST /receipts/process`

**Request:**

```bash
curl -X POST http://localhost:8080/receipts/process \
  -H "Content-Type: application/json" \
  -d '{
  "retailer": "Target",
  "purchaseDate": "2022-01-01",
  "purchaseTime": "13:01",
  "items": [
    {
      "shortDescription": "Mountain Dew 12PK",
      "price": "6.49"
    },{
      "shortDescription": "Emils Cheese Pizza",
      "price": "12.25"
    },{
      "shortDescription": "Knorr Creamy Chicken",
      "price": "1.26"
    },{
      "shortDescription": "Doritos Nacho Cheese",
      "price": "3.35"
    },{
      "shortDescription": "   Klarbrunn 12-PK 12 FL OZ  ",
      "price": "12.00"
    }
  ],
  "total": "35.35"
}'
```

**Response:**

```json
{
  "id": "a9a1da7c-e61d-4c2e-a8ef-6cc49665876a"
}
```

### Get Points for a Receipt

**Endpoint:** `GET /receipts/{id}/points`

**Request:**

```bash
curl -X GET http://localhost:8080/receipts/a9a1da7c-e61d-4c2e-a8ef-6cc49665876a/points
```

**Response:**

```json
{
  "points": 28
}
```

## Stopping the Application

When you're done testing:

```bash
# If in the same terminal where docker-compose is running, press Ctrl+C
# Or from another terminal in the same directory:
docker compose down
```

## Points Calculation Rules

Points are awarded based on the following rules:

* One point for every alphanumeric character in the retailer name
* 50 points if the total is a round dollar amount with no cents
* 25 points if the total is a multiple of 0.25
* 5 points for every two items on the receipt
* If the trimmed length of the item description is a multiple of 3, multiply the price by 0.2 and round up to the
  nearest integer. The result is the number of points earned
* 6 points if the day in the purchase date is odd
* 10 points if the time of purchase is after 2:00pm and before 4:00pm
