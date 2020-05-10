Migrating v0.1.0 to v.0.2.0
===========================

Storing grading engines to files
--------------------------------
Export jid and gradingEngine as space-separated values, and save as sandalphon_problem.csv.
Example:

    JIDXXX BatchWithSubtask
    JIDXXX Batch

Migrating database
------------------

- Rename sandalphon_programming_problem to sandalphon_problem
- Drop column gradingEngine in sandalphon_problem
- Rename sandalphon_programming_submission to sandalphon_submission_programming
- Rename sandalphon_user_role to sandalphon_user
- Change the type of additionalNote in sandalphon_problem to TEXT

Migrating data
--------------

Put migrate.sh and sandalphon_problem.csv in Sandalphon's base data directory. Then, run migrate.sh.
