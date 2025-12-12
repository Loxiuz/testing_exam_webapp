# Comprehensive Testing Suite Documentation

## Overview

This project includes a comprehensive testing suite with **100+ unit tests** covering both backend (Spring Boot) and frontend (React) components. The tests demonstrate various black box and white box testing techniques.

## Test Statistics

- **Total Tests**: 120+ unit tests
- **Backend Tests**: 80+ tests
- **Frontend Tests**: 15+ tests
- **Test Coverage**: High coverage across all service layers, controllers, and key frontend components

## Testing Techniques Demonstrated

### Black Box Testing

#### 1. Equivalence Partitioning

Equivalence partitioning divides input data into equivalent classes that should produce similar outputs. We test one representative value from each partition.

**Examples in this project:**

- **Date of Birth**:
  - Valid past date (e.g., 1990-01-01)
  - Today's date
  - Future date (invalid)
  - Null value

- **Ward Type Enum**:
  - CARDIOLOGY
  - NEUROLOGY
  - GENERAL_MEDICINE

- **Doctor Speciality Enum**:
  - CARDIOLOGY
  - NEUROLOGY
  - GENERAL_MEDICINE
  - SURGERY

- **Appointment Status**:
  - SCHEDULED
  - COMPLETED
  - CANCELLED

- **Username Length**:
  - Minimum (3 characters)
  - Maximum (50 characters)
  - Valid range (4-49 characters)

**Test Examples:**
- `PatientServiceTest.createPatient_DateOfBirthYesterday_CreatesPatient()` - Tests valid past date
- `WardServiceTest.createWard_AllWardTypes_CreatesWard()` - Tests all enum values
- `AuthServiceTest.register_UsernameMinLength_CreatesUser()` - Tests boundary username length

#### 2. Boundary Value Analysis

Boundary value analysis tests values at the boundaries of input domains, including minimum, maximum, and just inside/outside boundaries.

**Examples in this project:**

- **Date Boundaries**:
  - Today
  - Yesterday (just past)
  - 1 day in future (invalid boundary)
  - Far past (1900)
  - Far future (2100)

- **Max Capacity (Ward)**:
  - 0 (minimum)
  - 1 (just above minimum)
  - 1000 (large value)
  - Negative (invalid)

- **String Length**:
  - Empty string
  - 1 character
  - 255 characters
  - 256+ characters

- **UUID Boundaries**:
  - Valid UUID format
  - Invalid UUID format
  - Null UUID

**Test Examples:**
- `WardServiceTest.createWard_MaxCapacityZero_CreatesWard()` - Tests minimum capacity
- `WardServiceTest.createWard_MaxCapacityOne_CreatesWard()` - Tests just above minimum
- `AppointmentServiceTest.createAppointment_DateToday_CreatesAppointment()` - Tests today's date boundary
- `AuthServiceTest.register_UsernameMaxLength_CreatesUser()` - Tests maximum username length

#### 3. Decision Table Testing

Decision tables systematically test all combinations of conditions and their resulting actions. This is particularly useful for complex business logic.

**Primary Use Case: Ward-Hospital Validation**

The decision table for ward-hospital validation in `createPatient()`, `createDoctor()`, and `createNurse()`:

| Condition | Case 1 | Case 2 | Case 3 | Case 4 | Case 5 | Case 6 | Case 7 |
|-----------|--------|--------|--------|--------|--------|--------|--------|
| Ward ID provided? | N | N | Y | Y | Y | Y | N |
| Hospital ID provided? | N | Y | N | Y | Y | Y | Y |
| Ward exists? | - | - | Y | Y | Y | N | - |
| Hospital exists? | - | Y | - | Y | Y | - | N |
| Ward belongs to hospital? | - | - | - | Y | N | - | - |
| **Action** | Success | Success | Success | Success | ValidationException | EntityNotFoundException | EntityNotFoundException |

**Test Examples:**
- `PatientServiceTest.createPatient_BothWardAndHospitalNull_CreatesPatient()` - Case 1
- `PatientServiceTest.createPatient_WardNullHospitalProvided_CreatesPatient()` - Case 2
- `PatientServiceTest.createPatient_WardProvidedHospitalNull_CreatesPatient()` - Case 3
- `PatientServiceTest.createPatient_WardAndHospitalBothProvidedAndValid_CreatesPatient()` - Case 4
- `PatientServiceTest.createPatient_WardDoesNotBelongToHospital_ThrowsValidationException()` - Case 5
- `PatientServiceTest.createPatient_WardNotFound_ThrowsEntityNotFoundException()` - Case 6
- `PatientServiceTest.createPatient_HospitalNotFound_ThrowsEntityNotFoundException()` - Case 7

#### 4. State Transition Testing

State transition testing verifies that the system correctly handles transitions between different states.

**Appointment Status Transitions:**

Valid transitions:
- SCHEDULED → COMPLETED
- SCHEDULED → CANCELLED
- SCHEDULED → CONFIRMED (if implemented)

Invalid transitions:
- COMPLETED → SCHEDULED (should not be allowed)
- CANCELLED → COMPLETED (should not be allowed)

**Test Examples:**
- `AppointmentServiceTest.createAppointment_AllStatusTypes_CreatesAppointment()` - Tests all status values
- Tests verify that status can be set during creation and update

### White Box Testing

#### 1. Statement Coverage

Statement coverage ensures every line of code is executed at least once. Our tests achieve high statement coverage by testing all code paths.

**Coverage Areas:**
- All service methods (CRUD operations)
- All exception handling paths
- All conditional branches
- All query methods

#### 2. Branch Coverage

Branch coverage tests all if/else branches and conditional statements.

**Examples:**
- `createPatient()` method:
  - Branch: `if (wardId != null)` - tested with null and non-null values
  - Branch: `if (hospitalId != null)` - tested with null and non-null values
  - Branch: `if (ward != null && hospital != null)` - tested with all combinations

**Test Examples:**
- `PatientServiceTest.createPatient_BothWardAndHospitalNull_CreatesPatient()` - Tests null-null branch
- `PatientServiceTest.createPatient_WardNullHospitalProvided_CreatesPatient()` - Tests null-provided branch
- `PatientServiceTest.createPatient_WardProvidedHospitalNull_CreatesPatient()` - Tests provided-null branch
- `PatientServiceTest.createPatient_WardAndHospitalBothProvidedAndValid_CreatesPatient()` - Tests provided-provided branch

#### 3. Condition Coverage

Condition coverage tests all boolean conditions independently, ensuring all combinations of conditions are tested.

**Example: `ward != null && hospital != null`**

All 4 combinations tested:
1. `ward == null && hospital == null` → Skip validation
2. `ward == null && hospital != null` → Skip validation
3. `ward != null && hospital == null` → Skip validation
4. `ward != null && hospital != null` → Execute validation

#### 4. Path Coverage

Path coverage tests all possible execution paths through the code, including nested conditions and loops.

**Example: `createPatient()` with diagnosis handling**

Paths tested:
1. No diagnosis IDs → Skip diagnosis loop
2. Single diagnosis ID → Execute loop once
3. Multiple diagnosis IDs → Execute loop multiple times
4. Invalid diagnosis ID → Throw exception in loop

## Test Organization

### Backend Test Structure

```
src/test/java/com/testing_exam_webapp/
├── service/
│   ├── PatientServiceTest.java (20+ tests)
│   ├── DoctorServiceTest.java (15+ tests)
│   ├── NurseServiceTest.java (10+ tests)
│   ├── HospitalServiceTest.java (10+ tests)
│   ├── WardServiceTest.java (10+ tests)
│   ├── AppointmentServiceTest.java (12+ tests)
│   ├── AuthServiceTest.java (8+ tests)
│   ├── DiagnosisServiceTest.java (6+ tests)
│   ├── MedicationServiceTest.java (5+ tests)
│   ├── PrescriptionServiceTest.java (4+ tests)
│   └── SurgeryServiceTest.java (5+ tests)
├── controller/
│   └── PatientControllerTest.java (7+ tests)
└── util/
    └── TestDataBuilder.java (Test utilities)
```

### Frontend Test Structure

```
frontend/src/__tests__/
├── components/
│   └── PatientForm.test.tsx (2+ tests)
└── services/
    ├── patientService.test.ts (8+ tests)
    └── doctorService.test.ts (3+ tests)
```

## White Box Testing

### Explicit White Box Tests

A dedicated white box test class has been created: `PatientServiceWhiteBoxTest.java`

This class explicitly demonstrates:

#### 1. Branch Coverage
Tests every branch in conditional statements:
- `if (request.getDiagnosisIds() != null)` - TRUE and FALSE branches
- `if (wardId != null)` - TRUE and FALSE branches
- `if (hospitalId != null)` - TRUE and FALSE branches
- `if (ward != null && hospital != null)` - TRUE and FALSE branches
- `if (!wardBelongsToHospital)` - TRUE and FALSE branches
- `if (ward.getHospitals() != null)` - TRUE and FALSE branches

#### 2. Condition Coverage
Tests all combinations of compound conditions:
- `(ward != null && hospital != null)` - All 4 combinations:
  - FALSE && FALSE (both null)
  - TRUE && FALSE (ward not null, hospital null)
  - FALSE && TRUE (ward null, hospital not null)
  - TRUE && TRUE (both not null)
- `ward.getHospitals() != null && stream().anyMatch(...)` - All combinations

#### 3. Path Coverage
Tests all possible execution paths through `createPatient()`:
- Path 1: No diagnosis, no ward, no hospital
- Path 2: With diagnosis, no ward, no hospital
- Path 3: No diagnosis, with ward, no hospital
- Path 4: No diagnosis, no ward, with hospital
- Path 5: No diagnosis, with ward, with hospital (validation passes)
- Path 6: No diagnosis, with ward, with hospital (validation fails)
- Path 7: With diagnosis, with ward, with hospital (all branches taken)

#### 4. Statement Coverage
Ensures every statement is executed:
- Tests for-loop execution with multiple diagnoses
- Verifies all assignment statements
- Confirms all method calls are executed

### Code Coverage with JaCoCo

JaCoCo plugin has been configured to generate code coverage reports:

```bash
# Run tests and generate coverage report
./gradlew test jacocoTestReport

# View HTML report
# Open: build/reports/jacoco/html/index.html
```

The coverage report shows:
- **Line Coverage**: Percentage of lines executed
- **Branch Coverage**: Percentage of branches taken
- **Method Coverage**: Percentage of methods called
- **Class Coverage**: Percentage of classes tested

## Running Tests

### Backend Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests PatientServiceTest

# Run with coverage (if configured)
./gradlew test jacocoTestReport
```

### Frontend Tests

```bash
cd frontend

# Install dependencies (first time only)
# Note: Use --legacy-peer-deps due to React 19 compatibility with @testing-library/react
npm install --legacy-peer-deps

# Run tests
npm test

# Run tests in watch mode
npm test -- --watch

# Run tests with UI
npm run test:ui
```

**Note**: The `--legacy-peer-deps` flag is required because React 19 is newer than what `@testing-library/react@14.x` officially supports. The library still works correctly with React 19, but npm requires this flag to bypass the peer dependency check.

## Test Naming Conventions

Tests follow the pattern: `methodName_scenario_expectedResult`

Examples:
- `getPatientById_ValidId_ReturnsPatient()`
- `createPatient_WardHospitalMismatch_ThrowsValidationException()`
- `deletePatient_NullId_ThrowsException()`

## Key Test Files

### Backend

1. **PatientServiceTest.java** - Most comprehensive test suite demonstrating:
   - Decision table testing (ward-hospital validation)
   - Boundary value analysis (date of birth)
   - Equivalence partitioning (diagnosis handling)
   - Branch and condition coverage

2. **DoctorServiceTest.java** - Similar to PatientService with:
   - Speciality enum equivalence partitioning
   - Ward-hospital validation decision table

3. **WardServiceTest.java** - Demonstrates:
   - Boundary value analysis for maxCapacity (0, 1, large values)
   - Equivalence partitioning for WardType enum

4. **AppointmentServiceTest.java** - Demonstrates:
   - Date boundary analysis
   - Status enum equivalence partitioning
   - Date range queries

5. **AuthServiceTest.java** - Demonstrates:
   - Login equivalence partitioning (valid/invalid username/password)
   - Boundary analysis for username/password length
   - Register validation scenarios

### Frontend

1. **patientService.test.ts** - Tests API service layer:
   - Success scenarios
   - Error handling
   - Network failures

2. **PatientForm.test.tsx** - Tests React component:
   - Rendering
   - Form field presence
   - Loading states

## Test Utilities

### TestDataBuilder

A utility class for creating test data objects with default or custom values:

```java
Hospital hospital = TestDataBuilder.createHospital();
Ward ward = TestDataBuilder.createWard(WardType.CARDIOLOGY, 30);
Patient patient = TestDataBuilder.createPatient("John Doe", LocalDate.of(1990, 1, 1), "Male");
TestDataBuilder.associateWardWithHospital(ward, hospital);
```

## Coverage Goals

- **Service Layer**: 90%+ coverage
- **Controller Layer**: 80%+ coverage
- **Frontend Services**: 85%+ coverage
- **Frontend Components**: 70%+ coverage (key components)

## Best Practices Demonstrated

1. **Isolation**: Each test is independent and can run in any order
2. **Mocking**: External dependencies are mocked using Mockito
3. **Arrange-Act-Assert**: Clear test structure
4. **Descriptive Names**: Test names clearly describe what is being tested
5. **Documentation**: Inline comments explain complex test scenarios
6. **Edge Cases**: Boundary values and error conditions are thoroughly tested
7. **Equivalence Classes**: Representative values from each partition are tested

## Future Enhancements

1. Integration tests with TestContainers
2. End-to-end tests with Playwright/Cypress
3. Performance/load tests
4. Mutation testing
5. ~~Code coverage reports with JaCoCo~~ ✅ **COMPLETED**

## Conclusion

This testing suite demonstrates comprehensive coverage using both black box and white box testing techniques. The 100+ tests ensure high code quality, catch regressions early, and serve as living documentation of the system's behavior.

