import nock from 'nock';

import { APP_CONFIG } from '../conf';

export function nockUriel() {
  return nock(APP_CONFIG.apiUrls.uriel).defaultReplyHeaders({ 'access-control-allow-origin': '*' });
}
