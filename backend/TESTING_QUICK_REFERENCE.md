# 🚀 Quick Reference - Testing Commands

## Prerequisites Check

```powershell
# Check Java version (need Java 21)
java -version

# Check Maven version
mvn -version

# Check PostgreSQL running
pg_isready -U postgres
```

## Database Setup (One-Time)

```powershell
# Create test database
psql -U postgres -c "CREATE DATABASE ead_automobile_test;"

# Verify database exists
psql -U postgres -l | findstr ead_automobile_test
```

## Run Tests

### Option 1: Quick Test Run

```powershell
mvn test
```

### Option 2: Tests + Coverage Report

```powershell
mvn clean test jacoco:report
```

### Option 3: Using Batch Script

```powershell
.\run-tests.bat
```

### Option 4: Specific Test Class

```powershell
mvn test -Dtest=EmployeeServiceImplTest
```

### Option 5: Specific Test Method

```powershell
mvn test -Dtest=EmployeeServiceImplTest#testCreateEmployee_WithValidEmployeeRole_Success
```

## View Reports

### Coverage Report

```powershell
start target\site\jacoco\index.html
```

### Test Results Report

```powershell
start target\site\surefire-report.html
```

## Test Files Location

```
src/test/java/com/example/ead_backend/
├── service/impl/
│   ├── EmployeeServiceImplTest.java      (5 tests)
│   ├── CustomerServiceImplTest.java      (3 tests)
│   └── VehicleServiceImplTest.java       (7 tests)
├── controller/
│   └── AuthControllerIntegrationTest.java (5 tests)
└── repository/
    └── EmployeeRepositoryTest.java        (5 tests)
```

## Expected Output

```
Tests run: 25, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## Coverage Locations

- **JaCoCo Report:** `target/site/jacoco/index.html`
- **Surefire Report:** `target/site/surefire-report.html`
- **Test Logs:** `target/surefire-reports/`

## Common Issues

### Issue: Database doesn't exist

```powershell
psql -U postgres -c "CREATE DATABASE ead_automobile_test;"
```

### Issue: Tests fail to compile

```powershell
mvn clean compile test-compile
```

### Issue: Coverage not generated

```powershell
mvn clean test jacoco:report
```

## For Submission

1. Run tests: `mvn clean test jacoco:report`
2. Open coverage: `start target\site\jacoco\index.html`
3. Take screenshots
4. Note coverage percentage
5. Include TESTING_SUMMARY.md

## Test Count Breakdown

- **Unit Tests:** 15 (Service layer)
- **Integration Tests:** 5 (Controller layer)
- **Repository Tests:** 5 (Data layer)
- **Total:** 25 tests

## Key Files

- `pom.xml` - Build configuration with JaCoCo
- `application-test.properties` - Test database config
- `TESTING_README.md` - Full documentation
- `TESTING_SUMMARY.md` - Implementation summary
- `run-tests.bat` - Automated script

---

**Quick Start:** Just run `.\run-tests.bat` and everything will be done automatically! 🎯
