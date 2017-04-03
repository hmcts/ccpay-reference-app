# Reference application backend

Spring boot application demoing how to use various common components services

## User auth integration

### Securing the backend

Please see:
- http://git.reform/reform-idam/auth-checker-lib for details how to use auth-checker-lib
- SpringSecurityConfiguration.java for example how to configure spring security and add AuthCheckerUserOnlyFilter
- AuthCheckerConfiguration.java for example how to configure application-wide authorization
- PaymentClient.java for example how to get current user's "Bearer" token and pass it to the payments service

### Service auth

Please see:
- http://git.reform/reform-idam/auth-checker-lib for details how to use auth-checker-lib
- PaymentClient.java for example how to generate service's "Bearer" token and pass it to the payments service
- application.properties for example how to setup service TOTP details 

## Payments integration

- PaymentClient.java for very basic RestTemplate based client
- PaymentFactory.java & Appeal.java for tracking the current state of the payment and recreating a payment if the 
old one got canceled or has expired 

## Fees register integration

TODO

## Testing 

### Mocking user for spring integration tests:

Please see:
- UserResolverBackdoor.java
- AppealTestDsl.java
- *ComponentTest.java

### Stubbing payment responses

Please see:
- src/test/resources/mappings
- ComponentTestBase.java
