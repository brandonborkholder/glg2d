#!/bin/sh

CUR=`pwd`

for d in `find . -type d`
do
  cd $d

  NAME=`pwd`
  NAME=`basename $NAME`

  cat > index.html << END
<!doctype html>
<html>
  <head>
    <title>Listing of $NAME</title>
    <style type="text/css">
    body {
      margin-left: auto;
      margin-right: auto;
      width: 800px;
    }

    #listing {
      border: 1px solid black;
    }
    </style>
  </head>
  <body>
    <div id="listing">
      <h2>Listing of $NAME</h2>
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
