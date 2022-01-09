import { sleep, check, fail } from 'k6';
import http from 'k6/http';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';
import { Trend } from 'k6/metrics';


export let options = {
  vus: 1,
  duration: '1s',
  thresholds: {
    // 100% of checkes should pass
    checks: ['rate==1'],
    // !!!IMPORTANT 100% of process_cpu_usage should be less than 0.8 (80%)
    process_cpu_usage: ['p(100)<=0.95']
  }
};

const customTrend = new Trend('process_cpu_usage');
const time = '2m';


export default () => {

  const res = http.get(`http://localhost:9090/api/v1/query?query=process_cpu_usage{job="delivery-info-service"}[${time}]&step=1`);

  const body = JSON.parse(res.body);
  const values = body.data.result[0].values;

  if (values.length < 50) { // cpu.usage 데이터가 50개보다 적으면 비정상적인것으로 판단하기 (1분동안 테스트를 실행하기 때문에)
    const errorMessage = `defined expected timeInSeconds is ${timeInSeconds} ..... but actual is ${values.length}`;
    console.error(errorMessage);
    fail(errorMessage);
  }

  values.forEach(val => {
    console.info(`process.cpu.usage     : ${val[1]}`);
    customTrend.add(Number(val[1]));
  });

  sleep(0.1);
};
