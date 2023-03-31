import { check } from 'k6';
import { randomString } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';
import http from 'k6/http';

export const options = {
    vus: 5,
    duration: '1m0s',
};

export function setup() {
    console.log('Running tests against', baseUrl);
}

export default function () {
    const user = {
        username: "user-" + randomString(10),
        password: randomString(10),
        name: "User " + randomString(10),
    };
    const userJson = JSON.stringify(user);

    const signUpRes = http.post(url('/sign-up'), userJson, jsonParams());
    check(signUpRes, {
        'sign-up has status 201': (r) => r.status === 201,
        'sign-up has token': (r) => r.body.includes('token'),
    });

    for (let i = 0; i < 5; i++) {
        const signInRes = http.post(url('/sign-in'), userJson, jsonParams());
        check(signInRes, {
            'sign-in has status 200': (r) => r.status === 200,
            'sign-in has token': (r) => r.body.includes('token'),
        });

        const jsonResp = signInRes.json();
        const meRes = http.get(url('/me'), jsonParams({
            'Authorization': `${jsonResp['type']} ${jsonResp['token']}`,
        }));
        check(meRes, {
            'me has status 200': (r) => r.status === 200,
            'me has name': (r) => r.body.includes('name'),
        });
    }
}

const baseUrl = __ENV.BASE_URL || 'http://localhost:8080';
const url = (endpoint) => `${baseUrl}${endpoint}`;

const jsonParams = (headers = {}) => {
    const headersMap = {
        'Content-Type': 'application/json',
    };
    Object.assign(headersMap, headers);
    return {
        headers: headersMap,
    };
};
