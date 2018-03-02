set +ex

DIR="$(cd "$( dirname "${BASH_SOURCE[0]}")" && pwd)/../uriel-dist"
TAG=$1

./gradlew clean distTar
tar -xf $DIR/build/distributions/uriel-* --strip-components=1 -C $DIR/build/distributions
rm $DIR/build/distributions/*.tgz
mv $DIR/build/distributions/var/conf/uriel.yml.example $DIR/build/distributions/var/conf/uriel.yml
docker build -t judgels/uriel:$TAG $DIR
