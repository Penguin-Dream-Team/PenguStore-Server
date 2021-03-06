# PenguStore-Server

## Setup



### Database Setup

The API uses a database connection to the pengustore database.
When running the API the following environment variables have to be set for the API to successfully connect
to the databases.

#### Database Environment Variables
| Variable | Description |
| ------ | ----- |
| DB_HOST | The host of the db |
| DB_PORT | The port of the db |
| DB_DATABASE | The database of the db |
| DB_USER | The username of the db |
| DB_PASSWORD | The password of the db |


### Migrate Database Schema Files

The API is using [flyway](https://flyway.org/) to migrate the database schemas.
This will allow the application to automatically create the tables and populate them accordingly.
The files for the migrations can be found in the `resources/db/migrations` folder and need to be prepended with
the version in the format of `V{#}__migration_name`.
To migrate the files to the database, you can use the following gradle task:
```bash
flywayMigrate
```
For this to work, gradle needs to know the database credentials for the database server that is being
scanned. They are passed as environment variables with the same names as in the [Database Setup](#database-setup) section.
After running the migrations you will need to regenerate the Database files as in the [Database files](#generate-database-files)

### Generate Database Files

The API is using [jooq](https://www.jooq.org/) to create database queries, which requires
scanning the databases used to generate a typesafe schema.
The schema is committed to the repository, but in case any new tables are added and they
are required in the API, they can be generated using the following gradle task:
```bash
generatePengustoreJooq
```
For this to work, gradle needs to know the database credentials for the database server that is being
scanned. They are passed as environment variables with the same names as in the [Database Setup](#database-setup) section.


### Database Full Setup

If you want to run both the migrations and generate the resulting files you can use the following gradle task:
```bash
migrateDatabase
```


## Running

To run the API after setting it up completely, simply run the gradle task `run` and you will be able to
access the API on port 8080.