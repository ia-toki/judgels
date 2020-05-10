Migrating v0.6.0 to v0.7.0
===========================

Migrating database
------------------

- execute this SQL script "CREATE TABLE `judgels_data_version`(id bigint(20) NOT NULL AUTO_INCREMENT, version bigint(20) NOT NULL, PRIMARY KEY(id))" on all judgels play applications.
