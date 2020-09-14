# GraphlQL implementation for rata.digitraffic.fi

Build:
```
$ ./gradlew clean build
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
