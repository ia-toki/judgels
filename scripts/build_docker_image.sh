set +ex

DIR="$(cd "$( dirname "${BASH_SOURCE[0]}")" && pwd)/../jophiel-dist"
TAG=$1

./gradlew clean distTar
tar -xf $DIR/build/distributions/jophiel-* --strip-components=1 -C $DIR/build/distributions
rm $DIR/build/distributions/*.tgz
mv $DIR/build/distributions/var/conf/jophiel.yml.example $DIR/build/distributions/var/conf/jophiel.yml
docker build -t judgels/jophiel:$TAG $DIR
