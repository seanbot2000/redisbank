# About this repository

This repository uses Redis core data structures, Streams, RediSearch and TimeSeries to build a
Java/Spring Boot/Spring Data Redis Reactive application that shows a searchable transaction overview with realtime updates
as well as a personal finance management overview with realtime balance and biggest spenders updates. UI in Bootstrap/CSS/Vue.

Features in this demo:

- Redis Streams for the realtime transactions
- RediSearch for searching transactions
- Sorted Sets for the 'biggest spenders'
- Redis hashes for session storage (via Spring Session)

# Architecture
<img src="architecture.png"/>

# Getting Started

## Prerequisites

1. JDK 17 or higher (https://openjdk.java.net/install/index.html)
2. Docker Desktop (https://www.docker.com/products/docker-desktop), or Colima with a docker/k8s/containerd runtime
3. For running on Azure only: Azure CLI (https://docs.microsoft.com/en-us/cli/azure/install-azure-cli)
4. For running on Azure only: Azure Spring Cloud extension for the Azure CLI (https://docs.microsoft.com/en-us/cli/azure/spring-cloud?view=azure-cli-latest)
5. If you are going to demo geo-replication (A-A) make sure that your Redis Enteprise cluster is added to a replication group so that your A-A cluster can join it. 

## Running locally

1. Checkout the project
2. `docker-compose.sh up`
3. Navigate to http://localhost:8080 and login with user redis and password redisPass
4. Stop and clean with `docker-compose down -v --rmi local --remove-orphans`

## Running on Azure Spring Cloud

1. Follow the steps from 'Running locally'
2. Make sure you are logged into the Azure CLI
3. Add the Azure Spring Cloud extension to the Azure CLI `az extension add --name spring-cloud` If you already have the extension, make sure it's up to date using `az extension update --name spring-cloud`
2. Create an Azure Spring Cloud instance using `az spring-cloud create -n acrebank -g rdsLroACRE -l northeurope` (this may take a few minutes)
3. Create an App in the newly created Azure Spring Cloud instance using `az spring-cloud app create -n acrebankapp -s acrebank -g rdsLroACRE --assign-endpoint true --runtime-version Java_11`
4. Modify the application.properties so it points to your newly created ACRE instance

```
spring.redis.host=your ACRE hostname
spring.redis.port=your ACRE port (default: 10000)
spring.redis.password= your ACRE access key
```

5. Modify the application.properties so the websocket config will point to the Azure Spring Cloud app instance endpoint createed in step 3.

```
stomp.host=your ASC app endpoint URL (Default: <appname>-<service-name>.azuremicroservices.io)
stomp.port=443
stomp.protocol=wss
```

6. Rebuild the app using `./mvnw package`
7. Deploy the app to Azure Spring Cloud using `az spring-cloud app deploy -n acrebankapp -s acrebank -g rdsLroAcre --jar-path target/redisbank-0.0.1-SNAPSHOT.jar`

## To deploy geo-replicated instances

1. Using the Azure portal, create a second Redis Enteprise (E10) cluster in another region and join to the replication group from above.
2. Follow the steps for Running on Azure Spring Cloud in the same region as step 1 and reconfigure for that endpoint. 
   
## Troubleshooting tips on Azure Spring Cloud

To get the application logs:

`az spring-cloud app logs -n acrebankapp -g rdsLroAcre -s acrebank`

Note: project is compiled with JDK11 as that's currently the max LTS version that's supported by Azure Spring Cloud. Project will run fine when running locally or on other platforms up to JDK16.

## Known issues

1. Thread safety. Data is currently generated off of a single stream of transactions, which means it's the same for all users. Not a problem with the current iteration because it's single user, but beware when expanding this to multi-user.
2. Hardcoded values. Code uses hardcoded values throughout the code, these need to be replaced with proper variables.
