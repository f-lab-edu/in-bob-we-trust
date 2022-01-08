docker stop $(docker ps -a -q) && docker rm $(docker ps -a -q)  || true


docker network create \
    -d bridge inbobwetrust || true


docker image build \
  -t inbobwetrust/delivery-info-service:${GITHUB_SHA::7} \
  ./delivery-info-service

docker run -d \
  -p 8888:8888 --network=inbobwetrust --log-driver local  -v /var/run/docker.sock:/var/run/docker.sock -e COMMAND_LINE_ARGS_BEFORE='-Dspring.data.mongodb.primary.database=inbobwetrust -Dspring.data.mongodb.primary.uri=mongodb://localhost:27017 -Dspring.data.mongodb.secondary.database=inbobwetrust -Dspring.data.mongodb.secondary.uri=mongodb://localhost:27018'   -e COMMAND_LINE_ARGS_AFTER='--spring.profiles.active=loadtest'   beanskobe/delivㅣery-info-service


docker run -d --network=inbobwetrust -d -p 27017:27017 mongo


docker run -d --network=inbobwetrust -d -p 27018:27017 mongo


docker run -d --network=inbobwetrust -p 8090:8080  -v /var/run/docker.sock:/var/run/docker.sock -v ~/dev/github.com/in-bob-we-trust/k6-test/wiremock/__files:/home/wiremock/__files  -v ~/dev/github.com/in-bob-we-trust/k6-test/wiremock/mappings:/home/wiremock/mappings wiremock/wiremock


sleep 20

curl http://localhost:8090/__admin/mappings

curl http://localhost:8888/api/delivery
