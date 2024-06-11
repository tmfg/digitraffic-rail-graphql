# GraphlQL implementation for rata.digitraffic.fi

# Build and test:

```
$ mvn clean test
```

# Run:

```
$ mvn spring-boot:run
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
    * Spring Boot config, GraphQL instruments etc
* entities
    * Classes modeling rows returned from database
* links
    * Logic implementing "links" in GraphQL graph
* queries
    * Logic implementing GraphQL queries
* querydsl
    * Logic for creating querydsl queries
* to
    * Logic for converting entities to GraphQL objects (=TOs)

# How to add GraphlQL stuff

## Query

...

## Link

Basic workflow

1. Modify schema.graphqls to include your new data
1. Create Hibernate entity
1. Generate QueryDSL and GraphQL DTOs with `mvn compile`
1. Add Hibernate fields to `AllFields`
1. Create Hibernate -> GraphQL DTO converter (example `TrackSectionTOConverter`)
1. Create GraphQL link (example `TrainTrackingMessageToTrackSectionLink`)
1. Done

See `0271e6a9926dfb1be99f08632f7f35f5ba654ffe` for an example

## Updating schema.svg

The [schema visualization](src/main/resources/static/schema.svg) has been generated
with [http://nathanrandal.com/graphql-visualizer/](http://nathanrandal.com/graphql-visualizer/) using
the
below introspection query:

```graphql
query IntrospectionQuery {
    __schema {
        queryType { name }
        mutationType { name }
        subscriptionType { name }
        types {
            ...FullType
        }
        directives {
            name
            description
            args {
                ...InputValue
            }
        }
    }
}

fragment FullType on __Type {
    kind
    name
    description
    fields(includeDeprecated: true) {
        name
        description
        args {
            ...InputValue
        }
        type {
            ...TypeRef
        }
        isDeprecated
        deprecationReason
    }
    inputFields {
        ...InputValue
    }
    interfaces {
        ...TypeRef
    }
    enumValues(includeDeprecated: true) {
        name
        description
        isDeprecated
        deprecationReason
    }
    possibleTypes {
        ...TypeRef
    }
}

fragment InputValue on __InputValue {
    name
    description
    type { ...TypeRef }
    defaultValue
}

fragment TypeRef on __Type {
    kind
    name
    ofType {
        kind
        name
        ofType {
            kind
            name
            ofType {
                kind
                name
            }
        }
    }
}
```




