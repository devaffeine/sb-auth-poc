import { apiUrl, randomUser, signUpUser, signInUser, userProfile } from './common.js';

export const options = {
    vus: 5,
    duration: '1m0s',
};

export function setup() {
    console.log(`Running smoke tests on ${apiUrl('')}...`);
}

export default function () {
    const user = randomUser();
    signUpUser(user);
    for (let i = 0; i < 5; i++) {
        const token = signInUser(user);
        userProfile(token);
    }
}
