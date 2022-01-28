sudo docker run -d --network=host openzipkin/zipkin > /dev/null 2>&1 & disown


java -jar zipkin.jar > /dev/null 2>&1 & disown