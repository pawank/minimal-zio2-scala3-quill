# Rapidor Starter Kit 

## Introduction 

Starter kit / project using ZIO 2.0, Scala 3, Quill and GraphQL with Postgresql database.

## Getting started

```
git clone <project repository url>
```

### Instructions
1. Download and install docker.
3. Run the start script: `./start.sh` to start the postgres docker image. You might need `sudo` access to do the same.
4. Compile and run the examples:
5. `sbt ~reStart` to start the RESTful API or 
  ```
  > sbt 'runMain rapidor.api.RestService'
  [info] Server started on port 8888
  ```
6. Try the various APIs:
  ```
  > curl http://localhost:8888/customers
  [{"name":"Joe Smith","age":44,"membership":"k","id":1,"hid":1},{"name":"Joe Rolland","age":55,"membership":"k","id":2,"hid":2}]
  ```
7. Download GraphQL client from [Altair GraphQL client](https://altair.sirmuel.design)
8. `sbt 'runMain rapidor.graphql.GraphqlService'` to start the GraphQL server on
  ```
  [info] running (fork) rapidor.graphql.GraphqlService 
  [info] Server started on port: 8877
  ```
9. Use the graphql client and try running
  ```
  query {
    customers(age:44) {
    name,
    age,
    membership
    }
  }
  ```
  Using the POST address http://localhost:8877

10. `sbt` and then `run` inside will show you all applications available.

### How to access the database?

Follow the below steps,

1. `docker exec -ti rapidor-api-starter-kit_postgres_1 /bin/bash`
2. `psql -U postgres -p 15432 -h localhost -d starterkit`
Password is `password` which can be changed easily in the `docker-compose.yml` file and also in the `application.conf` file.


## Credits
[Querying Like It's Tomorrow - ZIO World 2022](https://github.com/deusaquilus/querying-like-its-tomorrow)