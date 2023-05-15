import nock from 'nock';

import { APP_CONFIG } from '../conf';

export function nockJophiel() {
  return nock(APP_CONFIG.apiUrl).defaultReplyHeaders({
    'access-control-allow-origin': '*',
    'access-control-allow-headers': 'authorization',
  });
}

export function nockUriel() {
  return nock(APP_CONFIG.apiUrl).defaultReplyHeaders({ 'access-control-allow-origin': '*' });
}

export function nockJerahmeel() {
  return nock(APP_CONFIG.apiUrl).defaultReplyHeaders({ 'access-control-allow-origin': '*' });
}
