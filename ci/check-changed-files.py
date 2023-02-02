import json
import os
import subprocess
import sys

from collections import OrderedDict


FORCE_CI = '[force ci]'

MODULES = OrderedDict([
    (':judgels-commons:judgels-fs', set()),
    (':judgels-commons:judgels-messaging', set()),
    (':judgels-commons:judgels-persistence-api', set()),
    (':judgels-commons:judgels-persistence-core', {':judgels-commons:judgels-persistence-testing'}),
    (':judgels-commons:judgels-persistence-testing', {':judgels-commons:judgels-persistence-api'}),
    (':judgels-commons:judgels-recaptcha', set()),
    (':judgels-commons:judgels-service-api', set()),
    (':judgels-commons:judgels-service-core', {':judgels-commons:judgels-service-api'}),
    (':judgels-commons:judgels-service-persistence', {':judgels-commons:judgels-persistence-core', ':judgels-commons:judgels-service-core'}),

    (':jophiel:jophiel-api', {':judgels-commons:judgels-persistence-api', ':judgels-commons:judgels-service-api'}),
    (':jophiel:jophiel-app', {':jophiel:jophiel-api', ':judgels-commons:judgels-fs', ':judgels-commons:judgels-persistence-core', ':judgels-commons:judgels-persistence-testing', ':judgels-commons:judgels-recaptcha', ':judgels-commons:judgels-service-persistence'}),
    (':jophiel:jophiel-client', {':jophiel:jophiel-api'}),
    (':jophiel:jophiel-dist', set()),
    (':jophiel', {':jophiel:jophiel-app', ':jophiel:jophiel-client', ':jophiel:jophiel-api', ':jophiel:jophiel-dist'}),

    (':gabriel:gabriel-api', set()),
    (':gabriel:gabriel-engine-api', {':gabriel:gabriel-api'}),
    (':gabriel:gabriel-engines', {':gabriel:gabriel-engine-api', ':judgels-commons:judgels-fs'}),
    (':gabriel:gabriel-app', {':gabriel:gabriel-engines', ':judgels-commons:judgels-messaging', ':judgels-commons:judgels-service-core'}),
    (':gabriel:gabriel-dist', set()),
    (':gabriel', {':gabriel:gabriel-app', ':gabriel:gabriel-dist', ':gabriel:gabriel-engines', ':gabriel:gabriel-engine-api', ':gabriel:gabriel-api'}),

    (':sandalphon:sandalphon-api', {':jophiel:jophiel-api', ':gabriel:gabriel-api'}),
    (':sandalphon:sandalphon-client', {':sandalphon:sandalphon-api', ':judgels-commons:judgels-fs', ':judgels-commons:judgels-persistence-core', ':judgels-commons:judgels-persistence-testing', ':judgels-commons:judgels-service-persistence', ':judgels-commons:judgels-messaging'}),
    (':sandalphon:sandalphon-app', {':sandalphon:sandalphon-client', ':jophiel:jophiel-client', ':gabriel:gabriel-engines'}),
    (':sandalphon', {':sandalphon:sandalphon-app', ':sandalphon:sandalphon-api', ':sandalphon:sandalphon-client'}),

    (':uriel:uriel-api', {':sandalphon:sandalphon-api'}),
    (':uriel:uriel-app', {':uriel:uriel-api', ':jophiel:jophiel-client', ':sandalphon:sandalphon-client', ':judgels-commons:judgels-messaging'}),
    (':uriel:uriel-dist', set()),
    (':uriel', {':uriel:uriel-app', ':uriel:uriel-api', ':uriel:uriel-dist'}),

    (':jerahmeel:jerahmeel-api', {':sandalphon:sandalphon-api'}),
    (':jerahmeel:jerahmeel-app', {':jerahmeel:jerahmeel-api', ':jophiel:jophiel-client', ':sandalphon:sandalphon-client', ':judgels-commons:judgels-messaging'}),
    (':jerahmeel:jerahmeel-dist', set()),
    (':jerahmeel', {':jerahmeel:jerahmeel-app', ':jerahmeel:jerahmeel-api', ':jerahmeel:jerahmeel-dist'}),

    (':michael:michael-app', set()),
    (':michael', {':michael:michael-app', ':jophiel', ':uriel', ':jerahmeel'}),

    (':raphael:package.json', set()),
    (':raphael', {':raphael:package.json'})
])

SERVICES = [
    ':michael',
    ':jophiel',
    ':uriel',
    ':sandalphon',
    ':jerahmeel',
    ':gabriel',
    ':raphael'
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
            if 'judgels-backends' + module.replace(':', '/') in file:
                changed_modules.add(module)
                break
            if 'judgels-frontends' + module.replace(':', '/') in file:
                changed_modules.add(module)
                break
    return changed_modules

def check(head_sha, base_sha, force_ci):
    changed_modules = get_changed_modules(head_sha, base_sha, force_ci)

    for service in SERVICES:
        if MODULES[service].intersection(changed_modules):
            print('echo ::set-output name={}::1'.format(service[1:]))
            if service == ':raphael':
                print('echo ::set-output name=yarn::1')
            else:
                print('echo ::set-output name=gradle::1')

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
