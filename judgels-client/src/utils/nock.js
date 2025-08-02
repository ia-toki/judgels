import nock from 'nock';

import { APP_CONFIG } from '../conf';

export function nockJophiel() {
  return nock('http://localhost:80/api/v2').defaultReplyHeaders({
    'access-control-allow-origin': '*',
    'access-control-allow-headers': 'authorization',
  });
}

export function nockUriel() {
  return nock('http://localhost:80/api/v2').defaultReplyHeaders({ 'access-control-allow-origin': '*' });
}

export function nockJerahmeel() {
  return nock('http://localhost:80/api/v2').defaultReplyHeaders({ 'access-control-allow-origin': '*' });
}
