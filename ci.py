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
    (':gabriel:gabriel-engines', {':gabriel:gabriel-engine-api'}),
    (':gabriel', {':gabriel:gabriel-engines', ':gabriel:gabriel-engine-api', ':gabriel:gabriel-api'}),


    (':commons', set()),
    (':api', set()),
    (':play-commons', {':commons', ':judgels-commons:judgels-service-persistence'}),
    (':jophiel-commons', {':commons', ':judgels-commons:judgels-persistence-core'}),
    (':gabriel-commons', {':commons'}),
    (':gabriel-blackbox', {':gabriel-commons', ':gabriel:gabriel-engines'}),
    (':sandalphon-commons', {':play-commons', ':gabriel-commons', ':api', ':judgels-commons:judgels-service-persistence', ':sandalphon:sandalphon-api', ':gabriel:gabriel-engine-api', ':sealtiel:sealtiel-api'}),
    (':sandalphon-blackbox-adapters', {':sandalphon-commons', ':gabriel-blackbox'}),


    (':sandalphon:sandalphon-api', {':jophiel:jophiel-api', ':gabriel:gabriel-api'}),
    (':sandalphon:sandalphon-client', {':sandalphon:sandalphon-api', ':judgels-commons:judgels-fs', ':judgels-commons:judgels-persistence-core', ':judgels-commons:judgels-persistence-testing', ':judgels-commons:judgels-service-persistence', ':sealtiel:sealtiel-api'}),
    (':sandalphon:sandalphon-app', {':sandalphon:sandalphon-client', ':jophiel:jophiel-client'}),
    (':sandalphon', {':sandalphon:sandalphon-app', ':sandalphon:sandalphon-api', ':sandalphon:sandalphon-client', ':sandalphon-commons', ':jophiel-commons', ':sandalphon-blackbox-adapters'}),

    (':uriel:uriel-api', {':sandalphon:sandalphon-api'}),
    (':uriel:uriel-app', {':uriel:uriel-api', ':jophiel:jophiel-client', ':sandalphon:sandalphon-client', ':sealtiel:sealtiel-api'}),
    (':uriel:uriel-dist', set()),
    (':uriel', {':uriel:uriel-app', ':uriel:uriel-api', ':uriel:uriel-dist'}),

    (':jerahmeel', {':sandalphon-commons', ':jophiel-commons', ':sandalphon-blackbox-adapters'}),

    (':raphael:package.json', set()),
    (':raphael', {':raphael:package.json'})
])

PROJECTS = [
    ':judgels-commons:judgels-fs',
    ':judgels-commons:judgels-persistence-core',
    ':judgels-commons:judgels-recaptcha',
    ':judgels-commons:judgels-service-core',
    ':judgels-commons:judgels-service-persistence',
    ':jophiel',
    ':sandalphon',
    ':sealtiel',
    ':uriel',
    ':jerahmeel',
    ':gabriel',
    ':gabriel-blackbox',
    ':raphael'
]

SERVICES = [
    ':jophiel',
    ':uriel',
    ':sealtiel',
    ':sandalphon',
    ':jerahmeel',
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
            if 'judgels-legacy' + module.replace(':', '/') in file:
                changed_modules.add(module)
                break
            if 'judgels-backends' + module.replace(':', '/') in file:
                changed_modules.add(module)
                break
            if 'judgels-frontends' + module.replace(':', '/') in file:
                changed_modules.add(module)
                break
    return changed_modules


def get_tag_env():
    tag = run('git describe --exact-match --tags HEAD 2> /dev/null').strip()
    if not tag:
        return ''
    return 'JUDGELS_VERSION={} '.format(tag)


def check(branch_to_compare):
    changed_modules = get_changed_modules(branch_to_compare)

    for project in PROJECTS:
        if MODULES[project].intersection(changed_modules):
            if project == ':raphael':
                print('yarn --cwd=`pwd`/judgels-frontends/raphael install')
                print('yarn --cwd=`pwd`/judgels-frontends/raphael lint')
                print('yarn --cwd=`pwd`/judgels-frontends/raphael test')
            elif project in [':sandalphon', ':jerahmeel', ':gabriel-blackbox']:
                print('./judgels-legacy/gradlew --console=plain -p judgels-legacy{} check'.format(project.replace(':', '/'))) 
            elif project == ':gabriel':
                print('./judgels-backends/gradlew --console=plain -p judgels-backends{} check'.format(project.replace(':', '/')))
                print('./judgels-legacy/gradlew --console=plain -p judgels-legacy{} check'.format(project.replace(':', '/'))) 
            else:
                print('./judgels-backends/gradlew --console=plain -p judgels-backends{} check'.format(project.replace(':', '/')))


def deploy(branch_to_compare):
    tag_env = get_tag_env()
    changed_modules = MODULES.keys() if tag_env else get_changed_modules(branch_to_compare)

    print('set -ex')
    for service in SERVICES:
        if MODULES[service].intersection(changed_modules):
            print('{}./deployment/scripts/deploy_{}.sh'.format(tag_env, service.replace(':', '')))


flatten_dependencies()

if len(sys.argv) < 2:
    die('Usage: python3 ci.py <command>')

command, commit_range, commit_message = sys.argv[1], os.environ['TRAVIS_COMMIT_RANGE'], os.environ['TRAVIS_COMMIT_MESSAGE']

branch_to_compare = None
if FORCE_CI in commit_message or not commit_range:
    branch_to_compare = FORCE_CI
else:
    branch_to_compare = commit_range.split('...')[0]
    if not 'commit' in run('git cat-file -t {}'.format(branch_to_compare)):
        branch_to_compare = FORCE_CI

print('echo "Running continuous integration against {}"'.format(branch_to_compare))

print('set -ex')

if command == 'check':
    check(branch_to_compare)
elif command == 'deploy':
    deploy(branch_to_compare)
