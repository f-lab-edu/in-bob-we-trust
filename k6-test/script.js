import {sleep, check, fail} from 'k6';
import http from 'k6/http';
import {textSummary} from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

export let options = {
    vus: 1,
    iterations: 1,
    //
    // vus: 100,
    // startTime: '10s', // the ramping API test starts a little later
    // startRate: 10,
    // timeUnit: '1s', // we start at 50 iterations per second
    // stages: [
    //     {duration: '30s', target: 200},
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


const params = {
    headers: {
        'Content-Type': 'application/json',
    },
};

export default () => {

    const minWaitTime = 0.01;
    const maxWaitTime = 1.00;

    const uri = "http://ec2-3-37-15-222.ap-northeast-2.compute.amazonaws.com:8888";
    const URI = uri + "/api/delivery"

    const ORDER_ID = new Date().toISOString();

    // 신규주문
    const req_addDelivery = makeDelivery(ORDER_ID, 'NEW');
    const addDelivery = http.post(URI, JSON.stringify(req_addDelivery), params);
    check(addDelivery, {
        'addDelivery is OK 200': () => {
            console.info('addDelivery result >>> ' + addDelivery.body);
            return addDelivery.status === 200;
        }
    });

    sleep(2)
    sleep(generateRandomNumberBetween(minWaitTime, maxWaitTime));

    // 주문접수
    const req_acceptDelivery = makeDelivery(ORDER_ID, 'ACCEPTED');
    req_acceptDelivery['orderTime'] = new Date().toISOString();
    sleep(0.1)
    req_acceptDelivery['pickupTime'] = new Date().toISOString();

    const acceptDelivery = http.put(URI + "/accept", JSON.stringify(req_acceptDelivery), params);
    check(acceptDelivery, {
        'acceptDelivery is OK 200': () => {
            console.info('acceptDelivery result >>> ' + acceptDelivery.body);
            return acceptDelivery.status === 200;
        }
    });

    sleep(2)
    sleep(1)
    const z = http.get(URI + "/"+req_acceptDelivery.id);
    console.info("skerrrrrrl       " + z.body);
    //
    //
    // sleep(generateRandomNumberBetween(minWaitTime, maxWaitTime));
    //
    // sleep(1)
    //
    // // 라이더 배정
    // const req_setDeliveryRider = makeDelivery(ORDER_ID, 'ACCEPTED');
    // req_setDeliveryRider['riderId'] = null;
    //
    // const setDeliveryRider = http.put(URI + "/rider", JSON.stringify(req_setDeliveryRider), params);
    // check(setDeliveryRider, {
    //     'setDeliveryRider is OK 200': () => {
    //         console.info('setDeliveryRider result >>> ' + setDeliveryRider.body);
    //         return setDeliveryRider.status === 200;
    //     }
    // });
    //
    // const curr = http.get(URI + "/" + req_setDeliveryRider["id"]);
    // console.info("currrr   " + curr.body);
    // sleep(1)
    //
    //
    // // 픽업완료
    // const req_setPickedUp = makeDelivery(ORDER_ID, 'PICKED_UP');
    // req_setPickedUp['deliveryStatus'] = 'PICKED_UP';
    //
    // const setPickedUp = http.put(URI + "/pickup", JSON.stringify(req_setPickedUp), params);
    // check(setPickedUp, {
    //     'setPickedUp is OK 200': () => {
    //         console.info('setPickedUp result >>> ' + setPickedUp.body);
    //         return setPickedUp.status === 200;
    //     }
    // });
    //
    // sleep(generateRandomNumberBetween(minWaitTime, maxWaitTime));
    //
    //
    // // 배달완료
    // const req_setComplete = makeDelivery(ORDER_ID, 'COMPLETE');
    //
    // const setComplete = http.put(URI + "complete", JSON.stringify(req_setComplete), params);
    // check(setComplete, {
    //     'setComplete is OK 200': () => {
    //         console.info('setComplete result >>> ' + setComplete.body);
    //         return setComplete.status === 200;
    //     }
    // });
    //
    // sleep(generateRandomNumberBetween(minWaitTime, maxWaitTime));
};

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

