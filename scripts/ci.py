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
    (':judgels-commons:judgels-service-core', {':judgels-commons:judgels-service-api'}),

    (':jophiel:jophiel-api', {':judgels-commons:judgels-persistence-api', ':judgels-commons:judgels-service-api'}),
    (':jophiel:jophiel-app', {':jophiel:jophiel-api', ':judgels-commons:judgels-fs', ':judgels-commons:judgels-persistence-core', ':judgels-commons:judgels-persistence-testing', ':judgels-commons:judgels-recaptcha', ':judgels-commons:judgels-service-core'}),
    (':jophiel:jophiel-dist', set()),
    (':jophiel', {':jophiel:jophiel-app', ':jophiel:jophiel-api', ':jophiel:jophiel-dist'}),

    (':sealtiel:sealtiel-api', {':judgels-commons:judgels-service-api'}),
    (':sealtiel:sealtiel-app', {':sealtiel:sealtiel-api', ':judgels-commons:judgels-service-core'}),
    (':sealtiel:sealtiel-dist', set()),
    (':sealtiel', {':sealtiel:sealtiel-app', ':sealtiel:sealtiel-api', ':sealtiel:sealtiel-dist'}),

    (':uriel:uriel-api', {':judgels-commons:judgels-persistence-api', ':judgels-commons:judgels-service-api'}),
    (':uriel:uriel-app', {':uriel:uriel-api', ':jophiel:jophiel-api', ':judgels-commons:judgels-fs', ':judgels-commons:judgels-persistence-core', ':judgels-commons:judgels-persistence-testing', ':judgels-commons:judgels-service-core', ':sealtiel:sealtiel-api'}),
    (':uriel:uriel-dist', set()),
    (':uriel', {':uriel:uriel-app', ':uriel:uriel-api', ':uriel:uriel-dist'}),

    (':raphael:package.json', set()),
    (':raphael', {':raphael:package.json'})
])

PROJECTS = [
    ':judgels-commons:judgels-fs',
    ':judgels-commons:judgels-persistence-core',
    ':judgels-commons:judgels-recaptcha',
    ':judgels-commons:judgels-service-core',
    ':jophiel',
    ':sealtiel',
    ':uriel',
    ':raphael'
]

SERVICES = [
    ':jophiel',
    ':sealtiel',
    ':uriel',
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


def get_tag_env():
    tag = run('git describe --exact-match --tags HEAD').strip()
    if not tag:
        return ''
    return 'JUDGELS_VERSION={} '.format(tag)


def check(branch_to_compare):
    changed_modules = get_changed_modules(branch_to_compare)

    for project in PROJECTS:
        if MODULES[project].intersection(changed_modules):
            if project == ':raphael':
                if ':raphael:package.json' in changed_modules:
                    print('yarn --cwd=`pwd`/judgels-frontends/raphael install')
                print('yarn --cwd=`pwd`/judgels-frontends/raphael test')
            else:
                print('./judgels-backends/gradlew --console=plain -p judgels-backends{} check'.format(project.replace(':', '/')))


def deploy(branch_to_compare):
    tag_env = get_tag_env()
    changed_modules = MODULES.keys() if tag_env else get_changed_modules(branch_to_compare)

    print('set -ex')
    for service in SERVICES:
        if MODULES[service].intersection(changed_modules):
            print('{}./scripts/deploy_{}.sh'.format(tag_env, service.replace(':', '')))


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
