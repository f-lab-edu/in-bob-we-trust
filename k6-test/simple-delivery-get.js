import { sleep, check, fail } from 'k6';
import http from 'k6/http';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

const uri = "http://host.docker.internal:8888/api/delivery";

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
    // 95% of checkes should pass
    checks: ['rate>=0.95']
  }
};

const params = {
  headers: {
    'Content-Type': 'application/json',
  },
};

export function setup() {
  const res = http.get(uri);
  if (JSON.parse(res.body).length >= 10) {
    return;
  }

  var failed = 0;
  for (let index = 1; index < 20; index++) {
    const payload = JSON.stringify(makeDelivery(index));
    const saved = http.post(uri, payload, params);
    if (saved.status != 200) {
      console.info('saved failed for id of     ' + index);
      fail++;
    } else {
      console.info('saved success for id of    ' + index);
    }
  }
  if (failed.length > 10) {
    throw '!!!! ABORTING !!!! failed saving more than half of POST /api/delivery ';
  }
}

function makeDelivery(id) {
  return {
    'orderId': 'orderId-' + id,
    'riderId': 'riderId-' + id,
    'agencyId': 'agencyId-' + id,
    'shopId': 'shopId-' + id,
    'customerId': 'customerId-' + id,
    'address': 'address-' + id,
    'phoneNumber': 'phoneNumber-' + id,
    'comment': 'comment-' + id,
    'deliveryStatus': 'DECLINED',
    'orderTime': '2022-01-11T09:48:58.279Z',
    'pickupTime': '2022-01-11T09:48:59.279Z',
    'finishTime': '2022-01-11T09:49:00.279Z'
  }
}


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
