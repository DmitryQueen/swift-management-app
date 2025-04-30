# SWIFT Code Parser Application

This is an application that parses SWIFT data from a specified file on startup and stores it in a PostgreSQL database. Configuration is managed via the `application.yaml` file, where you can define the input file path and name.

## Features

- Parses SWIFT codes from a CSV file on startup.
- Automatically sets the first inserted branch as the headquarter if none exists.
- Accepts valid SWIFT codes with 8 to 11 characters.
- Exposes a RESTful API to manage and query SWIFT codes.
- Containerized using Docker Compose.
- App covered with unit and integration tests

⚠️ **Important Notes:**
- The application **ONLY** supports .csv files. Please convert spreadsheets to .csv before use.
- By default, the database does **NOT** persist data across container restarts. To enable persistence, configure a named Docker volume.
- Endpoints are accessible at http://localhost:8080/

---

## Prerequisites

Make sure you have the following installed:

- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)

---

## Configuration

Before running the application, edit the `application.yaml` file to specify the file path and name of the SWIFT data to parse.

Example:
```yaml
parser:
  file-path: /data
  filename: swift_data.csv
```

- file-path: The directory containing the .csv file
- filename: The name of the file (including .csv extension)

⚠️ **Important note**

Filename should include extension of the file too. Application works only with .csv extensions

## How to run Application

### 1. Run Docker Desktop

### 2. Ensure **application.yaml** is properly configured
   
 Make sure the file path and name point to the correct location of your spreadsheet

### 3. Navigate to the root project directory
### 4. Start the application using the startup script
    - ./run.sh
### 5. Access the API at: http://localhost:8080
### 6. To stop the application use this script
    - ./stop.sh

⚠️ **Important note**

In order to parse another file you should restart the application!

## SWIFT Code API Endpoints

### 1. Get Details of a Specific SWIFT Code

**GET** `/v1/swift-codes/{swift-code}`

- swift-code: SWIFT/BIC code (8-11 characters)

Returns detailed information about a branch or headquarters.

---

### 2. Get All SWIFT Codes by Country ISO

**GET** `/v1/swift-codes/country/{countryISO2code}`

- countryISO2code: ISO2 code of the country {PL, DE, RU}

Returns all SWIFT codes for the specified country.

---

### 3. Add a New SWIFT Code

**POST** `/v1/swift-codes`

### Request Body
```json
{
  "address": "string",
  "bankName": "string",
  "countryISO2": "string",
  "countryName": "string",
  "isHeadquarter": boolean,
  "swiftCode": "string"
}
```

### 4. Delete a SWIFT Code

**DELETE** `/v1/swift-codes/{swift-code}`

- swift-code: SWIFT/BIC code (8-11 characters)

Deletes a SWIFT code entry by code.