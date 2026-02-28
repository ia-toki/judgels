import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../test/RouterWrapper';
import { nockJophiel } from '../../../../../utils/nock';
import ResetPasswordPage from './ResetPasswordPage';

describe('ResetPasswordPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid', email: 'user@domain.com' });
  });

  beforeEach(async () => {
    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <ResetPasswordPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  });

  test('form', async () => {
    const user = userEvent.setup();

    nockJophiel().post('/user-account/request-reset-password/user@domain.com').reply(200);

    const submitButton = screen.getByRole('button', { name: /request to reset password/i });
    await user.click(submitButton);

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
