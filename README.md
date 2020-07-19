# personio

How to run the project

- Docker way (need docker installed):
  - gradle build
  - docker build -t personio . 
  - docker run -m512M --cpus 2 -it -p 8080:8080 --rm personio

- locally:
  - gradle build
  - gradle run


Endpoints:

- http://localhost:8080/employeeHierarchy
- http://localhost:8080/employeeSupervisors/Sophie
- http://localhost:8080/newHierarchy

To run the tests:

- gradle test
