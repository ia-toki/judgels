set +ex

DIR="$(cd "$( dirname "${BASH_SOURCE[0]}")" && pwd)/../sealtiel-dist"
TAG=$1

./gradlew clean distTar
tar -xf $DIR/build/distributions/sealtiel-* -C $DIR/build/distributions
rm $DIR/build/distributions/*.tgz
docker build -t judgels/sealtiel:$TAG $DIR
