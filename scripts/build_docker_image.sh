set +ex

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/.."
TAG=$1

GENERATE_SOURCEMAP=false yarn build
mv $DIR/build/var/conf/raphael.js.example $DIR/build/var/conf/raphael.js
docker build -t judgels/raphael:$TAG $DIR
