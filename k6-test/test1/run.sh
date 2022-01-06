DURATION=10s
SERVER=process-cpu-usage-test
TEST=simple-delivery-get

k6 run --duration $DURATION --out csv=./results/$SERVER-summary.csv --console-output=./results/$SERVER-log.log $SERVER.js &
k6 run --duration $DURATION --out csv=./results/$TEST-summary.csv --console-output=./results/$TEST-log.log $TEST.js &

wait

echo '----------------------'
