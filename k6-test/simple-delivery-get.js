import { sleep, check } from 'k6';
import http from 'k6/http';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

const uri = "http://localhost:8888/api/delivery";

export let options = {
  vus: 100,
  startTime: '10s', // the ramping API test starts a little later
  startRate: 10,
  timeUnit: '1s', // we start at 50 iterations per second
  stages: [
    { duration: '30s', target: 100 },
    { duration: '30s', target: 300 },
    { duration: '30s', target: 500 },
    { duration: '30s', target: 100 }, // below normal load
  ],
  thresholds: {
    // http errors should be less than 1%
    http_req_failed: ['rate<=0.05'],
    // 95% of requests should be less than 500ms
    http_req_duration: ['p(90)<=1000', 'p(95)<=1250', 'p(100)<=1500'],
    // 99% of checkes should pass
    checks: ['rate>=0.95']
  }
};

export default () => {
  const res = http.get(uri);

  check(res, {
    'Body length is greater than 1': () => {
      const array = JSON.parse(res.body);
      return array.length > 0;
    }
  });
  sleep(0.1);
};
