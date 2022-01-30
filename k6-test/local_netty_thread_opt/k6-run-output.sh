THREAD_CONFIG=
k6 run \
  --console-output=./local-thread-dump/local-netty-threaddump-$THREAD_CONFIG.log \
  script3-thread-local-once.js