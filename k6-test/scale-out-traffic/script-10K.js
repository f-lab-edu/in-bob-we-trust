import {sleep, check, fail} from 'k6';
import http from 'k6/http';

export let options = {
    stages: [
        {duration: '5m', target: 11000},
        {duration: '15m', target: 11000},
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

const uri = "http://localhost.ap-northeast-2.compute.amazonaws.com:8888";
const URI = uri + "/api/delivery"

export default () => {
    // 신규주문
    const req_addDelivery = makeNewDelivery(Date.now().toString());
    req_addDelivery['riderId'] = null;

    const addDelivery = http.post(URI, JSON.stringify(req_addDelivery), params);
    check(addDelivery, {
        'addDelivery is OK 200': () => {
            if (addDelivery.status !== 200) {
                console.info('addDelivery result >>> ' + addDelivery.body);
                fail();
            }
            return addDelivery.status === 200;
        }
    });

    sleep(1);

    const SAVED_DELIVERY = JSON.parse(addDelivery.body);
    const DELIVRY_ID = SAVED_DELIVERY.id;

    // 주문접수
    const req_acceptDelivery = makeDelivery(DELIVRY_ID, 'ACCEPTED');
    req_acceptDelivery['riderId'] = null;
    req_acceptDelivery['orderTime'] = new Date().toISOString();
    sleep(0.01);
    req_acceptDelivery['pickupTime'] = new Date().toISOString();

    const acceptDelivery = http.put(URI + "/accept", JSON.stringify(req_acceptDelivery), params);
    check(acceptDelivery, {
        'acceptDelivery is OK 200': () => {
            if (acceptDelivery.status !== 200) {
                console.info('acceptDelivery result >>> ' + acceptDelivery.body);
            }
            return acceptDelivery.status === 200;
        }
    });
    sleep(1);


    // 라이더 배정
    const req_setDeliveryRider = makeDelivery(DELIVRY_ID, 'ACCEPTED');

    const setDeliveryRider = http.put(URI + "/rider", JSON.stringify(req_setDeliveryRider), params);
    check(setDeliveryRider, {
        'setDeliveryRider is OK 200': () => {
            if (setDeliveryRider.status !== 200) {
                console.info('setDeliveryRider result >>> ' + setDeliveryRider.body);
            }
            return setDeliveryRider.status === 200;
        }
    });
    sleep(1);

    // 픽업완료
    const req_setPickedUp = makeDelivery(DELIVRY_ID, 'PICKED_UP');
    req_setPickedUp['deliveryStatus'] = 'PICKED_UP';

    const setPickedUp = http.put(URI + "/pickup", JSON.stringify(req_setPickedUp), params);
    check(setPickedUp, {
        'setPickedUp is OK 200': () => {
            if (setPickedUp.status !== 200) {
                console.info('setPickedUp result >>> ' + setPickedUp.body);
            }
            return setPickedUp.status === 200;
        }
    });
    sleep(1);


    // 배달완료
    const req_setComplete = makeDelivery(DELIVRY_ID, 'COMPLETE');

    const setComplete = http.put(URI + "/complete", JSON.stringify(req_setComplete), params);
    check(setComplete, {
        'setComplete is OK 200': () => {
            if (setComplete.status !== 200) {
                console.info('setComplete result >>> ' + setComplete.body);
            }
            return setComplete.status === 200;
        }
    });
    sleep(1);
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
