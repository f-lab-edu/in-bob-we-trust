curl -sSL https://zipkin.io/quickstart.sh | bash -s

java -jar zipkin.jar > /dev/null 2>&1 & disown
