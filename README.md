# RestAssured API Test Framework

Automated API test suite for Automated API test suite for [Practice Software Testing API](https://api.practicesoftwaretesting.com/api/documentation), built with RestAssured and TestNG, built with RestAssured and TestNG.

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
- **Allure Report** (test reporting)
- **GitHub Actions** (CI)

## Project Structure

```
src/test/java
├── base/
│   └── BaseTest.java                 # Base test class with RestAssured setup
│
├── models/
│   ├── CategoryRequest.java          # Category create/update request DTO
│   ├── CategoryResponse.java         # Category response DTO
│   ├── LoginRequest.java             # Login request DTO
│   └── LoginResponse.java            # Login response DTO
│
├── tests/
│   ├── AuthenticationTests.java      # Authentication endpoint tests
│   └── CategoriesTests.java          # Category CRUD tests
│
└── utils/
    ├── AllureAttachments.java        # Allure report attachment helper
    ├── ConfigReader.java             # Configuration loader
    ├── TestConstants.java            # Test constants (email, password)
    └── TestDataGenerator.java        # Test data generation
```

## Configuration

The API base URL is configured in `src/test/resources/config.properties`:

`base.url=https://api-v4.practicesoftwaretesting.com`

Test credentials are provided through environment variables and are not stored in the repository.

## Running Tests

```bash
# Run all tests
mvn clean test

# Run specific test class
mvn test -Dtest=AuthenticationTests

mvn test -Dtest=CategoriesTests
```

## Test Reporting

The project uses Allure Report for test execution visualization.

Generate and open Allure report locally:
mvn io.qameta.allure:allure-maven:serve

The report contains:
- test execution details
- request and response payloads
- API status codes
- execution time
- test categorization by feature and story

## Continuous Integration

GitHub Actions automatically runs the test suite on pushes and pull requests targeting master.

The CI pipeline:

- Sets up Java 17 and Maven
- Runs the automated API tests
- Generates the Allure report
- Uploads the report as a GitHub Actions artifact

**13 automated API tests are currently included in the suite.**
