rm -f /users/joohyuk/dev/github.com/k6/samples/script2.js
cp /Users/joohyuk/dev/github.com/temp/in-bob-we-trust/k6-test/script2.js /users/joohyuk/dev/github.com/k6/samples/script2.js

cd /users/joohyuk/dev/github.com/k6 || exit

docker-compose run \
    -v ./samples:/scripts \
    k6 run /scripts/script2.js