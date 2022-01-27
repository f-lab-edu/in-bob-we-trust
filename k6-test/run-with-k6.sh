rm -f /users/joohyuk/dev/github.com/k6/samples/script.js
cp /Users/joohyuk/dev/github.com/temp/in-bob-we-trust/k6-test/script.js /users/joohyuk/dev/github.com/k6/samples/script.js

cd /users/joohyuk/dev/github.com/k6 || exit

docker-compose run \
    -v ./samples:/scripts \
    k6 run /scripts/script.js
