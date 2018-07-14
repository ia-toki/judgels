import subprocess
import sys

from collections import OrderedDict


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
])

PROJECTS = [
    ':judgels-commons:judgels-fs',
    ':judgels-commons:judgels-persistence-core',
    ':judgels-commons:judgels-recaptcha',
    ':judgels-commons:judgels-service-core',
    ':jophiel',
    ':sealtiel',
    ':uriel'
]

SERVICES = [
    ':jophiel',
    ':sealtiel',
    ':uriel'
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


def run(command, working_dir):
    p = subprocess.Popen(['bash', '-c', command], cwd=working_dir, stdout=subprocess.PIPE)
    return p.communicate()[0].decode('utf-8')


def get_changed_modules():
    changed_files = run('git diff --name-only origin/master', '.')

    changed_modules = set()
    for module in MODULES.keys():
        if module.replace(':', '/') in changed_files:
            changed_modules.add(module)
    return changed_modules


def check():
    changed_modules = get_changed_modules()

    print('set -ex')
    for project in PROJECTS:
        if MODULES[project].intersection(changed_modules):
            print('./judgels-backends/gradlew --console=plain -p judgels-backends{} check'.format(project.replace(':', '/')))


def deploy():
    changed_modules = get_changed_modules()

    print('set -ex')
    for service in SERVICES:
        if MODULES[service].intersection(changed_modules):
            print('./scripts/deploy_{}.sh'.format(service.replace(':', '')))


flatten_dependencies()

if len(sys.argv) < 2:
    die('Usage: python3 ci.py <command>')

command = sys.argv[1]
if command == 'check':
    check()
elif command == 'deploy':
    deploy()
