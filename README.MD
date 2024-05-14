# Introducing DineDecider

DineDecider is a new application for teams to collectively decide on a location to head to for lunch.

## Requirements

The project requires [Java 21].

The project makes use of Maven, Docker, Docker Compose, and PostgreSql.

## Useful Maven commands

The project makes use of Maven to carry some common tasks such as building the project or running test.

### Build the project

Compiles the project, runs the test and then creates an executable JAR file

```console
$ mvn clean install
```

### Run the tests

- Run unit tests

  ```console
  $ mvn test
  ```

The project makes use of Docker and Docker Compose. The files are located under `docker` folder and the commands listed are to be run from said folder.

## Useful Docker commands

### Build application image

```console
$ docker build . -t dinedecider
```

### Run application and database images
```console
$ docker compose up
```

### List containers
```console
$ docker ps
```

### Connect to DB container

```console
$ docker exec -it <<db container name>> bash
```

The project makes use of PostgreSql DB that is run in a docker container.

## Useful PostgreSql commands

Please not that PostgreSql container needs to be up and the commands need to be run from the container.

### Connect to PostgreSQL
```console
$ psql --username dineuser --dbname dinedb
```

### List all tables
```console
$ \dt
```

## API

### Start Dining Session

Endpoint

```text
POST /sessions
```

Example of body

```json
{
  "admin": {
    "userUuid": <adminUuid>,
    "name": <adminName>
  },
  "sessionName": <sessionName>,
  "invitees": [
    {
      "userUuid": <userUuid>,
      "name": <userName>
    }
  ]
}
```

Parameters

| Parameter     | Description                                                                                                                     |
|---------------|---------------------------------------------------------------------------------------------------------------------------------|
| `adminUuid`   | Unique Identifier of a user that starts the dining session. If this value does not exist in db, it will be added as new User.   |
| `adminName`   | Name of the user that starts the dining session. If user already exists, this value is not going to update the existing record. |
| `sessionName` | A name to the session                                                                                                           |
| `invitees`     | This value can be set when starting a session, but more can be invited afterward                                                |
|  `userUuid`   | Unique Identifier of a user who is invited to this session. If this value does not exist in db, it will be added as new User.   |
| `userName`   | Name of the user who is invited to this session. If user already exists, this value is not going to update the existing record. |                                                                                                                                |

Example response

```json
{
    "sessionUuid": "b859ca90-c19e-4764-b065-7a369e6c0fa2",
    "admin": {
        "userUuid": "3195d230-1000-4c78-9b5d-800f6f1d098e",
        "name": "Admin 2"
    },
    "sessionName": "Lunch 2",
    "invitees": [
        {
            "userUuid": "4195d230-1000-4c78-9b5d-800f6f1d098e",
            "name": "User 2"
        }
    ],
    "attendees": null
}
```

In the above example, a new session has been created with sessionUuid returned.

The following POST request, is an example request using CURL, starts the dining session shown in the table above.

```console
$ curl \
  -X POST \
  -H "Content-Type: application/json" \
  "http://localhost:8080/sessions" \
  -d '{"admin": {"userUuid": "3195d230-1000-4c78-9b5d-800f6f1d098e", "name": "Admin 2" }, "sessionName": "Lunch 2", "invitees": [ { "userUuid": "4195d230-1000-4c78-9b5d-800f6f1d098e", "name": "User 2" } ]}'
```

