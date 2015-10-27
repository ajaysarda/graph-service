
# GraphServer
GraphServer (aka "OmniStore") is REST based service which supports GET, PUT and DELETE of object and connections in the graph.
It supports the concept of scope where objects and connections are accessible with-in scope. (Scope could be “app”, “partner”).

# Features
* Graph APIs. (fully working)
* Horizontal partitioned Graph Storage (uses Cassandra)
* Whitelisting of objects and connections. (Currently service configuration)
* Directionality of connections. (Connection could be directed or undirected)
* Multi connection graph for recurring activities/event (For e.g check-ins at location, opened a app on device)

# Data Model

## Object
Object is uniquely identified by its type and id.  Object have properties (key-value pair, e.g. {“name” : “Ajay Kumar Sarda”, “gender” : “male”})

## Object/Scope
Object under the scope is uniquely identified by its type, id and scope.

## Connection
Connection is unique identified by  source-object-id, source-object-type, connection-type, destination-object-id, destination-object-type, properties. Connections have properties.

## Connection/Scope
Connection is uniquely identified by source-object-id, source-object-type, scope, connection-type, destination-object-id, destination-object-type. 

## MultiConnection
Connection is unique identified by  source-object-id, source-object-type, connection-type, destination-object-id, destination-object-type, created, properties.

Currently, connections and objects are whitelisted in the service. In future, we can open up this for apps to define their custom objects and connections.(something like open graph stories)

## Getting Started

#### Clone the repo & build

```bash
./gradlew clean shadowJar
java -jar build/libs/graph-service-1.0.0-fat.jar -conf conf.json -instances 2
```

#### Setup the Cassandra DB

Options:
 * run standalone
 * run ccm
 * point at your internal cluster

Execute the scheme file in **db/scheme.cql** on your setup.

#### Configuration
Look at conf.json for configuration.

## REST APIs

### Object APIs

PUT localhost:9301/user/4444 with body

```json
{
  "properties" : {
    "name" : “Ajay Sarda“
  }
} 
``` creates new user object.

GET localhost:9301/user/4444 fetches the object

DELETE localhost:9301/user/4444 deletes the object. 

### Object Scope APIs
PUT localhost/app/myapp/user/4444  with body

```json
{
  "properties" : {
    "name" : "Ajay Sarda"
  } 
} 
``` creates new user object. 

GET localhost:9301/app/myapp/user/4444 fetches the object

DELETE localhost:9301/app/myapp/user/4444 deletes the object.

### Connection APIs
 
PUT localhost:9301/user/4444  with body

```json
{
  "properties" : {
    "name" : "Ajay Sarda"
  } 
}
```

PUT localhost:9301/user/5555  with body

```json
{
  "properties" : {
    "name" : "Ankur Singla"
  } 
}
```

PUT localhost:9301/user/4444/friend/user/5555 with body

```json
{
  "properties" : {
    "group" : "cyanogen"
  } 
}
``` creates new friend connection.

GET localhost:9301/user/4444/friend  gets all friends for user with id=4444.
GET localhost:9301/user/5555/friend  gets all friends for user with id=5555.


GET localhost:9301/user/4444/friend/user/5555 gets the properties associated with that particular connection,

DELETE  localhost:9301/user/4444/friend/user/5555 deletes the connection.

### Connection Scope APIs
 
PUT localhost:9301/app/boxer/user/4444  with body

```json
{
  "properties" : {
    "name" : "Ajay Sarda"
  } 
}
``` 

PUT localhost:9301/app/boxer/user/5555  with body

```json
{
  "properties" : {
    "name" : "Ankur Singla"
  } 
}
``` 

PUT localhost:9301/app/boxer/user/4444/friend/user/5555 with body

```json
{
  "properties" : {
    "group" : "friends"
  } 
} 
``` creates new friend connection.

GET localhost:9301/app/myapp/user/4444/friend  gets all friends for user with id=4444.
GET localhost:9301/app/myapp/user/5555/friend  gets all friends for user with id=5555.


GET localhost:9301/app/myapp/user/4444/friend/user/5555 gets the properties associated with that particular connection,

DELETE  localhost:9301/user/4444/friend/user/5555 deletes the connection.
