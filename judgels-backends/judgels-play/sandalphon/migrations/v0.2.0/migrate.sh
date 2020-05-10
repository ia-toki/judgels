#!/bin/bash

mv problem problems
mv submission submissions
mkdir problem-clones

awk '{print "(cd problems/"$1"/grading; echo -n "$2" > engine.txt)"}' sandalphon_problem.csv | sh

pushd problems
for f in `ls`;
do
    pushd $f
    mv statement statements
    pushd statements
    echo -n '{"id-ID":"ENABLED"}' > availableLanguages.txt
    echo -n 'id-ID' > defaultLanguage.txt
    mv statement.html id-ID.html
    mv media resources
    popd
    pushd grading
    mv helper helpers
    echo -n '{"allowedLanguageNames":[],"isAllowedAll":true}' > languageRestriction.txt
    popd

    git init
    git add --all

    # fushar's JID
    git commit --author="JIDUSERkFl4m7hVGs5JUF76aS8g <no@email.com>" -m "Initial commit"

    popd
done
