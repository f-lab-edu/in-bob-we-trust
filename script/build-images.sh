docker image rm beanskobe/delivery-info-service:latest
docker image rm beanskobe/delivery-relay-service:latest

./gradlew delivery-info-service:build -x test -x integrationTest && \
docker image build -t beanskobe/delivery-info-service:latest ./delivery-info-service &

./gradlew delivery-relay-service:build -x test -x integrationTest && \
docker image build -t beanskobe/delivery-relay-service:latest ./delivery-relay-service

