# ENTSO-E integration

This is a small integration to the ENTSO-E API for fetching Day Ahead Prices.

It is based on the "REST API Level 0 - Fuse Booster" from Red Hat Launcher at https://developers.redhat.com/launch/

## Requirements

You'll need to obtain a security token to the ENTSO-E Transparency Platform, see instructions at https://transparency.entsoe.eu/content/static_content/Static%20content/web%20api/Guide.html#_authentication_and_authorisation

Store the security token from ENTSO-E into an environment variable called ENTSOE_SECURITYTOKEN

Tested with Java 11

## Building and running

Use Maven or a Docker platform (Docker/Podman/OpenShift/etc) to build and run, or use a prebuilt Docker image from https://github.com/iivorait/ENTSO-E-integration/pkgs/container/entso-e-integration

```
Run with Maven: mvn spring-boot:run
Build a fat JAR with Maven: mvn clean package [-DskipTests]
Run a fat JAR: java -jar target/entso-e-integration-1.0.0.jar
Build with Docker: docker build -t entso-e-integration
Run with Docker: docker run --name entso-e-integration -p 8080:8080 entso-e-integration
```

If you want to run the tests, you need to have the ENTSOE_SECURITYTOKEN environment variable set

## Usage

Check that you have the ENTSOE_SECURITYTOKEN environment variable set and the server is running

See the API documentation at http://localhost:8080/camel/api-doc

Check your area code from https://transparency.entsoe.eu/content/static_content/Static%20content/web%20api/Guide.html#_areas 

An example call with Finland's area code:

```
curl http://localhost:8080/camel/dap/10YFI-1--------U
```

The returned value is the currently valid price in a dot separated float value, for example "160.02"

