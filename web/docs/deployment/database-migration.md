---
sidebar_position: 5
---

# Database migration

Sometimes, in a release note for a newer Judgels version, we are asked to "migrate" the database. This means the newer version requires us to apply some new schema changes to our Judgels database.

Database migration is powered by [Liquibase](https://www.liquibase.org/).

To migrate the database, run this command in the core VM:

```
docker exec -it judgels-server bash
./init.sh db migrate
```
