import nock from 'nock';

import { APP_CONFIG } from '../conf';

export function nockJophiel() {
  return nock(APP_CONFIG.apiUrls.jophiel).defaultReplyHeaders({
    'access-control-allow-origin': '*',
    'access-control-allow-headers': 'authorization',
  });
}

export function nockUriel() {
  return nock(APP_CONFIG.apiUrls.uriel).defaultReplyHeaders({ 'access-control-allow-origin': '*' });
}
