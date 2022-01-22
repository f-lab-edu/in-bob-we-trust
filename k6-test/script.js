import {sleep, check, fail} from 'k6';
import http from 'k6/http';
import {textSummary} from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

export let options = {
    vus: 1,
    duration: '1s',
    // vus: 100,
    // startTime: '10s', // the ramping API test starts a little later
    // startRate: 10,
    // timeUnit: '1s', // we start at 50 iterations per second
    // stages: [
    //     {duration: '30s', target: 100},
    //     {duration: '30s', target: 300},
    //     {duration: '30s', target: 500},
    //     {duration: '30s', target: 100}, // below normal load
    // ],
    thresholds: {
        // http errors should be less than 1%
        http_req_failed: ['rate<=0.05'],
        // 95% of requests should be less than 500ms
        http_req_duration: ['p(90)<=1000', 'p(95)<=1250', 'p(100)<=1500'],
        // 95% of checkes should pass
        checks: ['rate>=0.95']
    }
};

const uri = "http://ec2-3-37-15-222.ap-northeast-2.compute.amazonaws.com:8888";

const params = {
    headers: {
        'Content-Type': 'application/json',
    },
};

export default () => {
    const delivery = makeDelivery(Date.now().toString());
    const payload = JSON.stringify(delivery);
    const URI = uri + "/api/delivery"
    const res = http.post(URI, payload, params);
    console.info("status is....", res.status);
    console.info("status is....", res);
    check(res, {
        'Status is OK 200': () => {
            return res.status === 200;
        }
    });
    sleep(1);
};

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
