import nock from 'nock';

import { APP_CONFIG } from '../conf';

export function nockApi() {
  return nock(APP_CONFIG.apiUrl).defaultReplyHeaders({
    'access-control-allow-origin': '*',
    'access-control-allow-headers': 'authorization',
  });
}
