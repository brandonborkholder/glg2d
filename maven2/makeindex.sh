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
