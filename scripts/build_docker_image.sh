set +ex

DIR="$(cd "$( dirname "${BASH_SOURCE[0]}")" && pwd)/../jophiel-dist"
TAG=$1

./gradlew clean distTar
tar -xf $DIR/build/distributions/jophiel-* -C $DIR/build/distributions
rm $DIR/build/distributions/*.tgz
docker build -t judgels/jophiel:$TAG $DIR
