#!/bin/bash

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
git rm -r apidocs
mv target/site/apidocs .
git add apidocs

# Copy back the repo
git rm -r maven2
rm -rf maven2
cp -r m2repo maven2
git add maven2
rm -rf m2repo

cd maven2

CUR=`pwd`

for d in `find . -type d`
do
  cd $d

  echo Creating index for `pwd`

  NAME=`pwd`
  NAME=`basename $NAME`

  cat > index.html << END
<!doctype html>
<html>
  <head>
    <title>GLG2D File Listing of $NAME</title>
    <style type="text/css">
    body {
      margin-top: 1.0em;
      background-color: #eeeeee;
      font-family: "Helvetica,Arial,FreeSans";
      color: #222222;
    }

    #container {
      margin: 0 auto;
      width: 700px;
    }
    </style>
  </head>
  <body>
    <div id="container">
      <h2><a href="http://brandonborkholder.github.com/glg2d/">GLG2D</a> Listing of $NAME</h2>
      <div class="link"><a href="..">..</a></div>
END

  for f in `ls`
  do
    if [ $f != "index.html" ]
    then
      echo "      <div class=\"link\"><a href=\"$f\">$f</a></div>" >> index.html
    fi
  done

cat >> index.html << END
    </div>
  </body>
</html>
END

  cd $CUR

done
