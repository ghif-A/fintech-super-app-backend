import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 20 },
    { duration: '1m30s', target: 10 },
    { duration: '20s', target: 0 },
  ],
};

const BASE_URL = 'http://localhost:8000';

function getAuthToken() {
  const username = `user${__VU}@example.com`;
  const password = 'password123';

  // Ensure user exists
  http.post(
    `${BASE_URL}/auth/signup`,
    JSON.stringify({ username, password, roles: ['user'] }),
    { headers: { 'Content-Type': 'application/json' } }
  );

  const loginRes = http.post(
    `${BASE_URL}/auth/signin`,
    JSON.stringify({ username, password }),
    { headers: { 'Content-Type': 'application/json' } }
  );

  return loginRes.json('accessToken');
}

export default function () {
  const token = getAuthToken();
  const headers = { Authorization: `Bearer ${token}` };

  // Create wallet
  const createWalletRes = http.post(
    `${BASE_URL}/wallets`,
    JSON.stringify({ userId: `user${__VU}` }),
    { headers }
  );
  check(createWalletRes, { 'wallet created': (r) => r.status === 200 });
  const walletId = createWalletRes.json('id');

  sleep(1);

  // Get balance
  const balanceRes = http.get(`${BASE_URL}/wallets/user/user${__VU}`, { headers });
  check(balanceRes, { 'get balance': (r) => r.status === 200 });

  sleep(1);

  // Deposit
  const depositRes = http.post(
    `${BASE_URL}/wallets/deposit`,
    JSON.stringify({ walletId, amount: 100 }),
    { headers }
  );
  check(depositRes, { 'deposit successful': (r) => r.status === 200 });
}
