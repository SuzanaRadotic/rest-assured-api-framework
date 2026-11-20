# RestAssured API Test Framework

Automated API test suite for the [Practice Software Testing API](https://api.practicesoftwaretesting.com/api/documentation), built with RestAssured and TestNG.

## What Is Covered

The framework currently automates testing of the following API areas:

- Authentication (login)
- Category management (CRUD operations)
- Request and response validation
- Positive and negative API scenarios
- JSON serialization/deserialization using Jackson
- Test data generation for unique test resources

## Technology Stack

- **Java 17**
- **Maven** – build and dependency management
- **RestAssured 6.x** – API testing
- **TestNG 7.x** – test runner
- **Jackson** – JSON serialization/deserialization
- **Allure Report** – test reporting
- **GitHub Actions** – continuous integration

## Project Structure

```text
src/test/java
├── api/
│   ├── ApiSpecs.java
│   ├── AllureLoggingFilter.java
│   ├── AuthClient.java
│   └── CategoryClient.java
│
├── base/
│   └── BaseTest.java
│
├── models/
│   ├── CategoryRequest.java
│   ├── CategoryResponse.java
│   ├── LoginRequest.java
│   └── LoginResponse.java
│
├── tests/
│   ├── AuthenticationTests.java
│   └── CategoriesTests.java
│
└── utils/
    ├── AllureAttachments.java
    ├── ConfigReader.java
    ├── TestConstants.java
    └── TestDataGenerator.java
```

## Framework Design

Tests are layered so that they describe what is verified, not how a request is built:

**Test → API Client → Request Specification**

- **Request specification (`ApiSpecs`)** holds the base URI, content type, and reporting filter in one place. There is no global `RestAssured.baseURI`, avoiding shared static state.
- **API clients (`AuthClient`, `CategoryClient`)** wrap endpoints and return the raw `Response`, so every test keeps full control over its assertions.
- **Automatic reporting:** `AllureLoggingFilter` attaches the actual request and response for every call. Passwords, tokens, and Authorization/Cookie headers are masked.
- **Test cleanup:** Resources created by tests are registered for deletion and removed in an `@AfterMethod` hook, even if a test fails. Cleanup is best-effort: failures are attached to the Allure report rather than failing the test. Cleanup requests appear under *Tear Down* in Allure.
- **Unique test data:** Test data is generated with a random suffix, helping prevent collisions between tests and previous runs.

## Configuration

The API base URL is configured in `src/test/resources/config.properties`:

```properties
base.url=https://api-v4.practicesoftwaretesting.com
```

Test credentials are provided through environment variables and are not stored in the repository.

Required environment variables:

- `CUSTOMER_EMAIL`
- `CUSTOMER_PASSWORD`

## Running Tests

Run all tests:

```bash
mvn clean test
```

Run a specific test class:

```bash
mvn test -Dtest=AuthenticationTests
mvn test -Dtest=CategoriesTests
```

## Test Reporting

The project uses Allure Report for test execution visualization.

Generate and open the Allure report locally:

```bash
mvn io.qameta.allure:allure-maven:2.15.0:serve
```

The report contains:

- Test execution details
- Request and response payloads (with sensitive values masked)
- API status codes
- Execution time
- Test categorization by feature and story
- Environment information

## Continuous Integration

GitHub Actions automatically runs the test suite on pushes and pull requests targeting `master`.

The CI pipeline:

- Sets up Java 17 and Maven
- Runs the automated API tests
- Generates the Allure report
- Uploads the report as a downloadable GitHub Actions artifact
- Supports manual workflow execution through GitHub Actions

Allure reports can be downloaded from the **Artifacts** section of each workflow run.

**13 automated API tests** are currently included in the suite.
