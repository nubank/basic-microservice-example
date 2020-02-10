# basic-microservice-example

A simplistic example of how Nubank organizes and tests microservices.

This is a really basic savings account microservice that doesn't really do much
other than serve as an example.

## Architecture

### ports and adapters microservice

At Nubank we organize our microservices using the "ports and adapters
architecture", also known as "hexagonal architecture".

With this architecture, code is organized into several layers: `logic`,
`controllers`, `adapters`, and `ports`.

#### logic

Deals with pure business logic and shouldn't have side-effects or throw
exceptions.

#### controllers

The "glue" between all the other layers, orchestrating calls between pure
business logic, adapters, and ports.

#### adapters

The layer that converts external data representations into internal ones, and
vice-versa. Acts as buffer to protect the service from changes in the outside
world; when a data representation changes, you only need to change how the
adapters deal with it.

#### ports

The layer that communicates with the outside world, such as http, kafka, and
datomic.

### components

We use the [components](https://github.com/stuartsierra/component) abstraction
to organize our `ports` (e.g. HTTP client, datomic client, redis client) and any
other logic that needs to track mutable state or encode dependencies between
stateful components. For every environment (e.g. test, e2e, prod, staging...) we
have a different version of our component systems, enabling us to easily inject
mocks or different implementations for different contexts.

We make components available to incoming http and kafka handlers. For instance,
the pedestal http handlers have access to things like the datomic or HTTP
components, and pass them down to the controller level for general use.

#### http client

Our http client logic is split into two components:

 - `http`: this component defines serialization and error handling logic. In
   this example repository the this logic is basically non-existent due to the
   overhead making the code useful to the general public.
 - `http-impl`; this component defines the http client library we use. We
   started with `http-kit` but have recently migrated away from this to
   `finagle` due to stability issues.

#### storage

In the case of this example service, we define a rudimentary in-memory storage
component. In our actual services we generally use a datomic component.

#### pedestal-related components

We use pedestal for or http serving layer, but we deconstruct pedestal logic
into several different components, deviating from the structure you would see in
the pedestal starter template.

##### routes

Encapsulates the pedestal http routes. This example project doesn't make use of
this abstraction, but in Nubank's internal microservices we use the routes
component to give us the ability to create bookmarks for urls and reference them
in various contexts, like our http client component. In addition, we can extend
the routes programmatically with operational routes related to other components,
for instance providing http routes for starting and stopping the topic consumer
in our kafka component.

##### service

Builds the pedestal service conifguration. Since it defines the interceptors for
the http handlers, this `service` component needs to depend on all components we
want to be available in those handlers, such as the http client and storage
client.

##### servlet

Contains logic to start the servlet

For instance the difference between the dev and mock servlets is that the mock
servlet, used in integration tests, creates the server but doesn't actually
start it.


## Tests

At Nubank we use [Midje](https://github.com/marick/Midje) as our test framework.
We've structured our integration tests to follow a world-transition system that
is encoded in the [`selvage` `flow`](https://github.com/nubank/selvage) macro.
Lastly, to check the form of nested data-structures during testing we employ
[`matcher-combinators`](https://github.com/nubank/matcher-combinators).


### Unit

Straw-man examples of what our unit tests may look like can be found in
[`controller-test`](https://github.com/nubank/basic-microservice-example/blob/master/test/basic_microservice_example/controller_test.clj)

### Integration

A straw-man example of how we do integration testing can be found in
[`account-flow`](https://github.com/nubank/basic-microservice-example/blob/master/test/basic_microservice_example/account_flow.clj)

More of an explanation of how `selvage` `flow` tests work can be in the [selvage
repository](https://github.com/nubank/selvage).

### Running tests

```
lein midje :autotest
```

## Running the (dev) server

1. Start the application: `lein run`
2. Go to [localhost:8080](http://localhost:8080/) to see: `Hello World!`


## Developing your service

1. Start a new REPL: `lein repl`
2. Start your service in dev-mode: `(def dev-serv (run-dev))`
3. Connect your editor to the running REPL session.
   Re-evaluated code will be seen immediately in the service.

## Missing aspects

Since this is a simple example of how Nubank's microservices are structured,
many aspects are missing:

 - endpoint schemas: our service http and kafka endpoints are always annotated
   with schemas.
 - better adapter examples: since endpoint schemas aren't a part of this
   example, our adapters from external to internal data representations aren't
   very interesting or representative.
 - kafka component: we make heavy use of kafka and have wrapped producer and
   consumer logic in components and also developed mocks for them.
 - and much more: datomic component, proper config component that does a
   waterfall of overriding, etc.
