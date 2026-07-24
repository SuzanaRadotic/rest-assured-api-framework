# RestAssured API Test Framework

Automated API test suite for [Practice Software Testing API](https://api-v4.practicesoftwaretesting.com) built with RestAssured and TestNG.

## What is covered

The framework currently automates testing of the following API areas:

- Authentication (login)
- Category management (CRUD operations)
- Request and response validation
- Positive and negative API scenarios
- JSON serialization/deserialization using Jackson
- Test data generation for unique test resources

## Technology Stack

- **Java 17**
- **Maven** (build & dependency management)
- **RestAssured 6.x** (API testing)
- **TestNG 7.x** (test runner)
- **Jackson** (JSON serialization/deserialization)

## Project Structure

```
src/test/java
├── base/
│   └── BaseTest.java          # Base test class with RestAssured setup
├── models/
│   ├── CategoryRequest.java   # Category create/update request DTO
│   ├── CategoryResponse.java  # Category response DTO
│   ├── LoginRequest.java      # Login request DTO
│   └── LoginResponse.java     # Login response DTO
├── tests/
│   ├── AuthenticationTests.java  # Auth endpoint tests
│   └── CategoriesTests.java      # Category CRUD tests
└── utils/
    ├── ConfigReader.java      # Configuration loader
    ├── TestConstants.java     # Test constants (email, password)
    └── TestDataGenerator.java # Test data generation
```

## Configuration

The API base URL is configured in `src/test/resources/config.properties`:

```properties
base.url=https://api-v4.practicesoftwaretesting.com
```

To run against a different environment, modify this file directly.

## Running Tests

```bash
# Run all tests
mvn clean test

# Run specific test class
mvn test -Dtest=AuthenticationTests
```

## Test Reports

TestNG reports are generated in `target/surefire-reports/`.