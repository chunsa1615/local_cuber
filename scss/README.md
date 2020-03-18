
# SCSS


## sass 설치

    http://www.sass-lang.com/install


## scss -> css (minify)

    ### Bootstrap : http://v4-alpha.getbootstrap.com/getting-started/options/

        scss bootstrap-scc/bootstrap.scss ../src/main/resources/static/lib/bootstrap-scc/dist/css/bootstrap.css

        wget --post-data="input=`cat ../src/main/resources/static/lib/bootstrap-scc/dist/css/bootstrap.css`" --output-document=../src/main/resources/static/lib/bootstrap-scc/dist/css/bootstrap.min.css https://cssminifier.com/raw

