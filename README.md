A couple of scripts to test the service are provided in `testing` folder

## Build and run

### With maven

```bash
# build and test
mvn clean install

# run the service
mvn spring-boot:run
```

### With docker

```bash
# build an image with the service
make build

# run the service in the container
make run
```
