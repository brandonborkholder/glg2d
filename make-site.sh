#!/bin/sh

VER=$1

if [ -z "$VER" ]
then
  echo Must specify version as first argument
  exit 1
fi

cp -r maven2 m2repo

git checkout master
mvn clean

# Generate javadocs
mvn javadoc:javadoc -Dshow=public

# Deploy to local repo
mvn package javadoc:jar source:jar
mvn deploy:deploy-file -Dfile=target/glg2d-$VER.jar \
  -Dsources=target/glg2d-$VER-sources.jar \
  -Djavadoc=target/glg2d-$VER-javadoc.jar \
  -DpomFile=pom.xml -Durl=file:./m2repo

# Check out the site
git checkout gh-pages

# Install javadocs
git rm -rf apidocs
mv target/site/apidocs .
git add apidocs

# Copy back the repo
cp -r m2repo maven2
rm -rf m2repo
git add maven2

