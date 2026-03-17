# GraphQL implementation for rata.digitraffic.fi

# Build and test:

```
$ mvn clean test
```

# Run:

```
$ mvn spring-boot:run
```

GraphiQL, an in-browser IDE for exploring GraphQL, is embedded through `graphiql-spring-boot-starter`
and available at `http://localhost:8083/graphiql`.

Sample query to be run:

```graphql
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

* `config` — Spring Boot config, GraphQL wiring, instruments etc.
* `entities` — Hibernate `@Entity` classes mapping database tables
* `links` — Graph-edge resolvers: fetch related entities for a parent TO (DataLoader pattern)
* `queries` — Root-level GraphQL query fetchers and JPQL utilities (`JpqlWhereBuilder`, `JpqlOrderByBuilder`, `JpqlSafeIdentifier`)
* `to` — Converters from Hibernate entities to generated GraphQL TOs

GraphQL TOs are **code-generated** from `schema.graphqls` at compile time into `target/generated-sources/` — do not edit them manually.

# How to add GraphQL stuff

## Query

1. Add the new field to `schema.graphqls`
1. Run `mvn compile` to regenerate the GraphQL TOs
1. Create (or reuse) a Hibernate `@Entity` for the data
1. Create a `*TOConverter` with a `convertEntity(Entity)` method (e.g. `TrainTOConverter`)
1. Create a query class in `queries/` extending `BaseQuery<Entity, TO>`:
   - implement `getQueryName()` — must match the field name in the schema
   - implement `getEntityClass()`
   - implement `buildBaseWhereClause()` — add fixed conditions and bind GraphQL arguments as named parameters
   - implement `convertEntityToTO()`
   - annotate with `@Component`
1. Write an integration test extending `BaseWebMVCTest`

See `TrainsByDepartureDateQuery` for a simple example.

## Link

A link resolves a field on an existing type (e.g. `TimeTableRow.station`).

1. Add the new field to the parent type in `schema.graphqls`
1. Run `mvn compile` to regenerate the GraphQL TOs
1. Create (or reuse) a Hibernate `@Entity` and `*TOConverter` for the child type
1. Create a link class in `links/` extending `OneToOneLink` or `OneToManyLink`:
   - implement `getTypeName()` / `getFieldName()` — must match the parent type and field name in the schema
   - implement `getEntityClass()`
   - implement `createKeyFromParent()` / `createKeyFromChild()` — used for DataLoader batching
   - implement `buildKeyWhereClause()` — JPQL fragment filtering children by their parent keys (e.g. `"e.shortCode IN :keys"`)
   - implement `createChildTOFromEntity()`
   - annotate with `@Component`
1. Write an integration test extending `BaseWebMVCTest`

See `TimeTableRowToStationLink` for a one-to-one example and `TrainToTimeTableRowLink` for a one-to-many example.

## Updating schema.svg

The [schema visualization](src/main/resources/static/schema.svg) has been generated
with [http://nathanrandal.com/graphql-visualizer/](http://nathanrandal.com/graphql-visualizer/) using
the below introspection query:

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
