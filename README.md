# fictional-fms

###How to run
* Clone the repo https://github.com/Posnakidesd/fictional-fms.git
* Make sure that Docker daemon is up and running
* Run `mvn clean install`. This will build the docker images as well.
* Run `docker-compose up -d`


The flow of the system is the following:

The main component in the system is `Trip` which controls the starting and stopping of flows.

There are 3 main services (besides kafka, postgresql etc... )
* Service A. Accepts REST requests and pushes Trips to Kafka
* Service B. Listens for Trips and generates heartbeats
* Service C. Listens for Heartbeats, calculates and generates Penalties. Saves the results into Postgresql.

Run case:

1) POST some cars.
* `curl -X POST -H 'Content-Type: application/json' -d '{ "model": "Honda Civic", "plate" : "AAA999" }' localhost:8084/api/v1/car`
* `curl -X POST -H 'Content-Type: application/json' -d '{ "model": "Ford Focus", "plate" : "BBB999" }' localhost:8084/api/v1/car`
2) POST some Drivers
* `curl -X POST -H 'Content-Type: application/json' -d '{ "name": "Dimitrios" }' localhost:8084/api/v1/driver`
* `curl -X POST -H 'Content-Type: application/json' -d '{ "name": "Orestis" }' localhost:8084/api/v1/driver`
3) POST Trip. If Trip has status 'START' or 'ACTIVE', then new Heartbeats will be generated. These heartbeats will result in penalties added to the driver. You can have multiple Trips created for multiple drivers.
* `curl -X POST -H 'Content-Type: application/json' -d '{ "driverId": 1, "carId": 1, "state" : "START" }' localhost:8084/api/v1/trip`
* `curl -X POST -H 'Content-Type: application/json' -d '{ "driverId": 2, "carId": 2, "state" : "START" }' localhost:8084/api/v1/trip`
4) PUT new Trip with state 'STOP' to stop Heartbeat generation. (DELETE should work as well)
* `curl -X PUT -H 'Content-Type: application/json' -d '{ "id": 1, "driverId": 2, "carId": 2, "state" : "START" }' localhost:8084/api/v1/trip`
5) Connect to Postgresql to check for penalties
* `docker exec -it <postgresql container id> psql -U username -W fms` Passsword: passsword
* `select * from penalty;`

