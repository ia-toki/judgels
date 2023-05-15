import json
import os
import subprocess
import sys

from collections import OrderedDict


FORCE_CI = '[force ci]'

MODULES = OrderedDict([
    ('judgels-commons', set()),

    ('judgels-grader-api', set()),
    ('judgels-grader-engines', {'judgels-commons', 'judgels-grader-api'}),
    ('judgels-grader-app', {'judgels-grader-engines', 'judgels-grader-api'}),
    ('judgels-grader', {'judgels-grader-app', 'judgels-grader-engines', 'judgels-grader-api'}),

    ('judgels-server-api', {'judgels-commons', 'judgels-grader-api'}),
    ('judgels-server-app', {'judgels-grader-engines', 'judgels-server-api'}),
    ('judgels-server', {'judgels-server-app', 'judgels-server-api'}),

    ('judgels-client', set()),

    ('web', set())
])

SERVICES = [
    'judgels-commons',
    'judgels-grader',
    'judgels-server',
    'judgels-client',
    'web'
]

def flatten_dependencies():
    for module in MODULES.keys():
        deps = MODULES[module].copy()
        for dep in deps:
            MODULES[module] |= MODULES[dep]
        MODULES[module].add(module)


def run(command):
    p = subprocess.Popen(['bash', '-c', command], cwd='.', stdout=subprocess.PIPE)
    return p.communicate()[0].decode('utf-8')


def get_changed_modules(head_sha, base_sha, force_ci):
    if force_ci:
        return MODULES.keys()

    run('git fetch origin master')
    changed_files = run('git diff --name-only {} {}'.format(base_sha, head_sha)).split('\n')

    changed_modules = set()
    for module in MODULES.keys():
        for file in changed_files:
            if module in file:
                changed_modules.add(module)
                break
    return changed_modules

def check(head_sha, base_sha, force_ci):
    changed_modules = get_changed_modules(head_sha, base_sha, force_ci)

    for service in SERVICES:
        if MODULES[service].intersection(changed_modules):
            print('echo "{}=1" >> $GITHUB_OUTPUT'.format(service))
            if service == 'judgels-client':
                print('echo "yarn=1" >> $GITHUB_OUTPUT')
            elif service == 'judgels-server':
                print('echo "gradle=1" >> $GITHUB_OUTPUT')

flatten_dependencies()

print('set -x')

with open(os.environ['GITHUB_EVENT_PATH']) as event_path:
    event = json.load(event_path)

    if 'pull_request' in event:
        head_sha = event['pull_request']['head']['sha']
        base_sha = event['pull_request']['base']['sha']
        force_ci = False
    else:
        head_sha = event['after']
        base_sha = event['before'] if event['ref'] == 'refs/heads/master' else 'origin/master'
        force_ci = FORCE_CI in event['head_commit']['message']

    check(head_sha, base_sha, force_ci)
