# TODO: Comprehensive Functional Tests for Tombers Application

## Test Structure
- [x] Create controller test classes
- [x] Create service test classes
- [x] Create repository test classes
- [x] Create mapper test classes
- [x] Create security test classes
- [x] Create exception handler test classes

## Controller Tests
- [x] AuthControllerTest
  - [x] Test register endpoint (success and validation errors)
  - [x] Test login endpoint (success and authentication failures)
- [x] UserControllerTest
  - [x] Test getUserProfile endpoint
  - [x] Test updateUserProfile endpoint
  - [x] Test searchUsers endpoint
  - [x] Test getAvailableUsers endpoint
- [x] ProjectControllerTest
  - [x] Test getAllProjects endpoint
  - [x] Test getProjectById endpoint (success and not found)
  - [x] Test createProject endpoint
  - [x] Test updateProject endpoint
  - [x] Test deleteProject endpoint
  - [x] Test searchProjects endpoint
  - [x] Test getActiveProjects endpoint
  - [x] Test getIncompleteProjects endpoint

## Service Tests
- [x] AuthServiceTest
  - [x] Test register method (success, email exists, username exists)
  - [x] Test login method (success, invalid credentials)
- [x] UserServiceTest
  - [x] Test getUserProfile method
  - [x] Test updateUserProfile method
  - [x] Test searchUsers method
  - [x] Test getAvailableUsers method
- [x] ProjectServiceTest
  - [x] Test getAllProjects method
  - [x] Test getProjectById method
  - [x] Test createProject method
  - [x] Test updateProject method
  - [x] Test deleteProject method
  - [x] Test searchProjects method
  - [x] Test getActiveProjects method
  - [x] Test getIncompleteProjects method

## Repository Tests
- [x] UserRepositoryTest
  - [x] Test findByEmail method
  - [x] Test findByUsername method
  - [x] Test existsByEmail method
  - [x] Test existsByUsername method
  - [x] Test findAvailableUsers method
  - [x] Test searchUsers method
- [x] ProjectRepositoryTest
  - [x] Test findByStatus method
  - [x] Test searchProjects method
  - [x] Test findActiveProjectsOrderByCreatedAt method
  - [x] Test findIncompleteProjectsOrderByProgress method

## Mapper Tests
- [x] UserMapperTest
  - [x] Test toDto method
  - [x] Test updateEntity method
- [x] ProjectMapperTest
  - [x] Test toResponse method
  - [x] Test toEntity method
  - [x] Test updateEntity method

## Security Tests
- [x] JwtServiceTest
  - [x] Test generateToken method
  - [x] Test extractUsername method
  - [x] Test isTokenValid method

## Exception Handler Tests
- [x] GlobalExceptionHandlerTest
  - [x] Test handleMethodArgumentNotValid method
  - [x] Test handleConstraintViolation method
  - [x] Test handleEntityNotFound method
  - [x] Test handleNoResourceFoundException method
  - [x] Test handleHttpRequestMethodNotSupported method
  - [x] Test handleIllegalArgumentException method
  - [x] Test handleGeneric method

## Followup Steps
- [ ] Run all tests to verify they pass
- [ ] Add test data setup if needed
- [ ] Ensure test coverage is comprehensive
