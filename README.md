# Personio

Prerequisite:

sdkman: curl -s https://get.sdkman.io | bash
gradle: sdk install gradle 6.5.1
kotlin: sdk install kotlin

How to run the project:

- Docker way (need docker and gradle installed):
  - gradle build
  - docker build -t personio . 
  - docker run -m512M --cpus 2 -it -p 8080:8080 --rm personio

- locally (need openjdk8 installed):
  - gradle build
  - gradle run

Endpoints:
(Credentials: "user" - "password")

- http://localhost:8080/employeeHierarchy
- http://localhost:8080/employeeSupervisors/Sophie
- http://localhost:8080/newHierarchy

To run the tests:

- gradle test
