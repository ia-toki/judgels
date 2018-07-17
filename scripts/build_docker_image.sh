set +ex

DIR="$(cd "$( dirname "${BASH_SOURCE[0]}")" && pwd)/.."
TAG=$1

GENERATE_SOURCEMAP=false yarn build
docker build -t judgels/raphael:$TAG $DIR
