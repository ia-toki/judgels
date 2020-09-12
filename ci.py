import json
import os
import subprocess
import sys

from collections import OrderedDict


FORCE_CI = '[force ci]'

MODULES = OrderedDict([
    (':judgels-commons:judgels-fs', set()),
    (':judgels-commons:judgels-persistence-api', set()),
    (':judgels-commons:judgels-persistence-core', {':judgels-commons:judgels-persistence-testing'}),
    (':judgels-commons:judgels-persistence-testing', {':judgels-commons:judgels-persistence-core'}),
    (':judgels-commons:judgels-recaptcha', set()),
    (':judgels-commons:judgels-service-api', set()),
    (':judgels-commons:judgels-service-core', {':judgels-commons:judgels-service-api', ':judgels-commons:judgels-service-jaxrs'}),
    (':judgels-commons:judgels-service-jaxrs', set()),
    (':judgels-commons:judgels-service-persistence', {':judgels-commons:judgels-persistence-core', ':judgels-commons:judgels-service-core'}),

    (':jophiel:jophiel-api', {':judgels-commons:judgels-persistence-api', ':judgels-commons:judgels-service-api'}),
    (':jophiel:jophiel-app', {':jophiel:jophiel-api', ':judgels-commons:judgels-fs', ':judgels-commons:judgels-persistence-core', ':judgels-commons:judgels-persistence-testing', ':judgels-commons:judgels-recaptcha', ':judgels-commons:judgels-service-persistence'}),
    (':jophiel:jophiel-client', {':jophiel:jophiel-api'}),
    (':jophiel:jophiel-dist', set()),
    (':jophiel', {':jophiel:jophiel-app', ':jophiel:jophiel-client', ':jophiel:jophiel-api', ':jophiel:jophiel-dist'}),

    (':sealtiel:sealtiel-api', {':judgels-commons:judgels-service-api'}),
    (':sealtiel:sealtiel-app', {':sealtiel:sealtiel-api', ':judgels-commons:judgels-service-core'}),
    (':sealtiel:sealtiel-dist', set()),
    (':sealtiel', {':sealtiel:sealtiel-app', ':sealtiel:sealtiel-api', ':sealtiel:sealtiel-dist'}),

    (':gabriel:gabriel-api', set()),
    (':gabriel:gabriel-engine-api', {':gabriel:gabriel-api'}),
    (':gabriel:gabriel-engines', {':gabriel:gabriel-engine-api', ':judgels-commons:judgels-fs'}),
    (':gabriel:gabriel-app', {':gabriel:gabriel-engines', ':sealtiel:sealtiel-api', ':judgels-commons:judgels-service-core'}),
    (':gabriel:gabriel-dist', set()),
    (':gabriel', {':gabriel:gabriel-app', ':gabriel:gabriel-dist', ':gabriel:gabriel-engines', ':gabriel:gabriel-engine-api', ':gabriel:gabriel-api'}),


    (':judgels-play:commons', set()),
    (':judgels-play:play-commons', {':judgels-play:commons', ':judgels-commons:judgels-service-persistence'}),
    (':judgels-play:jophiel-commons', {':judgels-play:commons', ':judgels-commons:judgels-persistence-core'}),

    (':sandalphon:sandalphon-api', {':jophiel:jophiel-api', ':gabriel:gabriel-api'}),
    (':sandalphon:sandalphon-client', {':sandalphon:sandalphon-api', ':judgels-commons:judgels-fs', ':judgels-commons:judgels-persistence-core', ':judgels-commons:judgels-persistence-testing', ':judgels-commons:judgels-service-persistence', ':sealtiel:sealtiel-api'}),
    (':sandalphon:sandalphon-app', {':sandalphon:sandalphon-client', ':jophiel:jophiel-client'}),
    (':sandalphon', {':sandalphon:sandalphon-app', ':sandalphon:sandalphon-api', ':sandalphon:sandalphon-client', ':judgels-play:sandalphon-commons', ':judgels-play:jophiel-commons', ':judgels-play:sandalphon-blackbox-adapters'}),

    (':judgels-play:sandalphon-commons', {':judgels-play:play-commons', ':judgels-commons:judgels-service-persistence', ':sandalphon:sandalphon-api', ':sandalphon:sandalphon-client', ':gabriel:gabriel-engines', ':sealtiel:sealtiel-api'}),
    (':judgels-play:sandalphon-blackbox-adapters', {':judgels-play:sandalphon-commons'}),

    (':uriel:uriel-api', {':sandalphon:sandalphon-api'}),
    (':uriel:uriel-app', {':uriel:uriel-api', ':jophiel:jophiel-client', ':sandalphon:sandalphon-client', ':sealtiel:sealtiel-api'}),
    (':uriel:uriel-dist', set()),
    (':uriel', {':uriel:uriel-app', ':uriel:uriel-api', ':uriel:uriel-dist'}),

    (':jerahmeel:jerahmeel-api', {':sandalphon:sandalphon-api'}),
    (':jerahmeel:jerahmeel-app', {':jerahmeel:jerahmeel-api', ':jophiel:jophiel-client', ':sandalphon:sandalphon-client', ':sealtiel:sealtiel-api'}),
    (':jerahmeel:jerahmeel-dist', set()),
    (':jerahmeel', {':jerahmeel:jerahmeel-app', ':jerahmeel:jerahmeel-api', ':jerahmeel:jerahmeel-dist'}),

    (':raphael:package.json', set()),
    (':raphael', {':raphael:package.json'})
])

SERVICES = [
    ':jophiel',
    ':uriel',
    ':sealtiel',
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


def die(message):
    print('[ERROR] {}'.format(message))
    sys.exit(1)


def run(command):
    p = subprocess.Popen(['bash', '-c', command], cwd='.', stdout=subprocess.PIPE)
    return p.communicate()[0].decode('utf-8')


def get_changed_modules(branch_to_compare):
    if branch_to_compare == FORCE_CI:
        return MODULES.keys()

    changed_files = run('git diff --name-only {}'.format(branch_to_compare)).split('\n')

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

def check(branch_to_compare):
    changed_modules = get_changed_modules(branch_to_compare)

    for service in SERVICES:
        if MODULES[service].intersection(changed_modules):
            print('true')

def deploy(branch_to_compare):
    tag_env = get_tag_env()
    changed_modules = MODULES.keys() if tag_env else get_changed_modules(branch_to_compare)

    print('set -ex')
    for service in SERVICES:
        if MODULES[service].intersection(changed_modules):
            print('{}./deployment/scripts/deploy_{}.sh && \\'.format(tag_env, service.replace(':', '')))
    print('true')


flatten_dependencies()

print('WOY')

with open(os.environ['GITHUB_EVENT_PATH']) as event_path:
    data = json.load(event_path)
    print(data)
