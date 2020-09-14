# GraphlQL implementation for rata.digitraffic.fi

# Build:
```
$ ./gradlew clean build
```

# Run:
```
$ ./gradlew bootRun
```

GraphiQL, an in-browser IDE for exploring GraphQL, is embedded through `graphiql-spring-boot-starter`
and available at `http://localhost:8081/graphiql`.

Sample query to be run:

```
{
  train(trainNumber: 51, departureDate: "2020-06-29") {
    cancelled
    commuterLineid
    deleted
    departureDate
    runningCurrently
    timetableAcceptanceDate
    timetableType
    trainNumber
    version
    operator {
      name
    }
    timeTableRows {
      scheduledTime
      actualTime
      station {
        shortCode
      }
      causes {
        categoryCode {
          code
        }
      }
    }
  }
}

```

# Architecture:
* config
    * Spring Boot config etc
* entities
    * Classes modeling rows returned from database
* fetchers
    * Logic implementing "jumps" between Graphql types
* repositories
    * Logic for fetching data from database
* to
    * Logic for converting entities to GraphQL objects (=TOs)

