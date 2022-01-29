import {sleep, check, fail} from 'k6';
import http from 'k6/http';
import {textSummary} from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';


// export let options = {
//     vus: 1,
//     duration: '1s',
//
// }

export function setup() {
    console.info("------------------------------------------------------");
    console.info("------------------------------------------------------");
    console.info(new Date().toString());
    console.info(new Date().toString());
    console.info(new Date().toString());
    console.info(new Date().toString());
    console.info(new Date().toString());
    console.info("------------------------------------------------------");
    console.info("------------------------------------------------------");
    console.info("------------------------------------------------------");
    console.info("------------------------------------------------------");
    const req_addDelivery = makeNewDelivery();
    req_addDelivery['riderId'] = null;
    const addDelivery = http.post(URI, JSON.stringify(req_addDelivery), params);
    if (addDelivery.status !== 200) {
        throw new Error("somethigns wrong!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }
}

export let options = {
    stages: [
        {duration: '15m', target: 1},
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
/*
*/

const params = {
    headers: {
        'Content-Type': 'application/json',
    },
};

const uri = "localhost:8888";
const URI = uri + "/api/delivery"

export default () => {
    const res = http.get(uri + "/actuator/threaddump");
    console.log(res.body);
    sleep(30);
};


function makeNewDelivery(time) {
    return {
        'orderId': 'orderId-' + time,
        'agencyId': 'agencyId-' + time,
        'shopId': 'shopId-' + time,
        'customerId': 'customerId-' + time,
        'address': 'address-' + time,
        'phoneNumber': 'phoneNumber-' + time,
        'comment': 'comment-' + time,
        'deliveryStatus': 'NEW',
        'orderTime': new Date().toISOString(),
        'pickupTime': new Date().toISOString(),
        'finishTime': new Date().toISOString()
    }
}

function makeDelivery(id, status) {
    return {
        'id': id,
        'orderId': 'orderId-' + id,
        'agencyId': 'agencyId-' + id,
        'shopId': 'shopId-' + id,
        'customerId': 'customerId-' + id,
        'address': 'address-' + id,
        'phoneNumber': 'phoneNumber-' + id,
        'comment': 'comment-' + id,
        'deliveryStatus': status,
        'orderTime': new Date().toISOString(),
        'pickupTime': new Date().toISOString(),
        'finishTime': new Date().toISOString()
    }
}

function generateRandomNumberBetween(min, max) {
    return (Math.random() * (max - min) + min).toFixed(3);
};

