#Judgels Gabriel

[![Build Status](https://travis-ci.org/ia-toki/judgels-gabriel.svg?branch=master)](https://travis-ci.org/ia-toki/judgels-gabriel)

##Description
Gabriel is an application built using Java and [SBT](http://www.scala-sbt.org/) to grade any programming problems.

When grading programming problems, Gabriel use [Isolate](http://www.ucw.cz/moe/isolate.1.html) as sandbox, thus Gabriel should only be run on Linux compatible Operating Systems. Gabriel depends on [Sandalphon](https://github.com/ia-toki/judgels-sandalphon) to get grading resources (testcases, subtasks, checker, etc) and [Sealtiel](https://github.com/ia-toki/judgels-sandalphon) to connect to Sandalphon.

##Set Up And Run
To set up Gabriel, you need to:

1. Install [SBT](http://www.scala-sbt.org/release/tutorial/Setup.html) on your Operating System.

2. Clone [Gabriel Commons](https://github.com/ia-toki/judgels-gabriel-commons) into the same level of Gabriel directory, so that the directory looks like:
    - Parent Directory
        - gabriel-commons
        - judgels-gabriel

2. Copy src/main/resources/conf/application_default.conf into src/main/resources/conf/application.conf and change the configuration accordingly. **Refer to the default configuration file for explanation of the configuration keys.** In the application configuration, Gabriel need to connect to running Sandalphon (to fetch grading resources) and Sealtiel (to accept grading requests) application. In order to connect Gabriel to running Sandalphon and Sealtiel, Gabriel must be registered as Sandalphon and Sealtiel clients.

To run Gabriel, just run "sbt" then it will check and download all dependencies and enter SBT Console.
In sbt console use "run" command to run Gabriel. By default it will run on several threads depending on your CPU cores (**It is still not configurable at the moment**).

The version that is recommended for public use is [v0.1.0](https://github.com/ia-toki/judgels-gabriel/tree/v0.1.0).
