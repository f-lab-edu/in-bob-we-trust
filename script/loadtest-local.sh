./gradlew :delivery-info-service:build --x test --x integrationTest

docker stop $(docker ps -a -q) && docker rm $(docker ps -a -q)  || true

docker image rm inbobwetrust/delivery-info-service:loadtest

docker image build -t inbobwetrust/delivery-info-service:loadtest ./delivery-info-service

docker-compose -f ./k6-test/docker-compose-actions.yml up -d

k6 run ./k6-test/simple-delivery-get.js && k6 run ./k6-test/process_cpu_usage.js


K6_PROMETHEUS_REMOTE_URL=http://localhost:9090/api/v1/write \
  ./k6-test/k6 run ./k6-test/simple-delivery-get.js  -o output-prometheus-remote && \
  k6 run ./k6-test/process_cpu_usage.js
