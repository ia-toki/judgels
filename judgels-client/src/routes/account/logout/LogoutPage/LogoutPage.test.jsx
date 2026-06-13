import { act, render } from '@testing-library/react';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import LogoutPage from './LogoutPage';

describe('LogoutPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async () => {
    nockApi().post('/session/logout').reply(200);

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <LogoutPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('logs out immediately', async () => {
    await renderComponent();
    expect(nock.isDone()).toBe(true);
  });
});
