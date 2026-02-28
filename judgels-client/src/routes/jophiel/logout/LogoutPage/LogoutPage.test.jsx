import { act, render } from '@testing-library/react';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJophiel } from '../../../../utils/nock';
import LogoutPage from './LogoutPage';

describe('LogoutPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  beforeEach(async () => {
    nockJophiel().post('/session/logout').reply(200);

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <LogoutPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  });

  it('logs out immediately', () => {
    expect(nock.isDone()).toBe(true);
  });
});
