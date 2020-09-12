# Judgels 

[![Build Status](https://img.shields.io/travis/ia-toki/judgels/master.svg)](https://travis-ci.org/ia-toki/judgels)
[![License](https://img.shields.io/github/license/ia-toki/judgels.svg)](https://github.com/ia-toki/judgels/blob/master/LICENSE.txt)

<img src="https://raw.githubusercontent.com/ia-toki/judgels/master/judgels-frontends/raphael/src/assets/images/logo.png" align="left" height="100" hspace="5"/>

**Judgels** is a competitive programming platform. With Judgels, you can prepare problems with various types and languages, set up test data, and test solutions. Then, you can run contests with various configurations. Users can be managed with various authorizations: as contestants, supervisors, and managers. Equipped with sandboxed grader as well.

## Features

These are non-exhaustive lists.

### Problem management
- multilanguage problem statements
- batch, interactive, output-only, and functional (like IOI 2010 and above) problem types
- custom checker (scorer)
- subtasks with different points
- version control

### Contest management
- IOI- and ICPC-style contests
- virtual contests, where contestants can start at different times
- public contest registration
- announcements, clarifications, scoreboards

### As a platform
- distributed microservices that can be scaled independently
- easy deployment using Docker

## Docs

See the [wiki](https://github.com/ia-toki/judgels/wiki).

## Credit

This work is initiated based on an IOI 2014 paper: [Components and Architectural Design
of an Autograder System Family](http://www.ioinformatics.org/oi/pdf/v8_2014_69_80.pdf), written by Jordan Fernando and Inggriani Liem.

## License

GNU GPL version 2.
