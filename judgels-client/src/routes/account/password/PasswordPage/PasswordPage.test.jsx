import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import PasswordPage from './PasswordPage';

describe('PasswordPage', () => {
  const renderComponent = async () => {
    setSession('token', { jid: 'userJid', email: 'user@domain.com' });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <PasswordPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('form', async () => {
    await renderComponent();

    const user = userEvent.setup();

    nockApi().post('/v4/account/password/request-reset/user@domain.com').reply(200);

    const submitButton = screen.getByRole('button', { name: /request to reset password/i });
    await user.click(submitButton);

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
