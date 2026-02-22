import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJophiel } from '../../../../utils/nock';
import LoginPage from './LoginPage';

describe('LoginPage', () => {
  afterEach(() => {
    nock.cleanAll();
  });

  beforeEach(async () => {
    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <LoginPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  });

  test('form', async () => {
    const user = userEvent.setup();

    const usernameOrEmail = screen.getByRole('textbox', { name: /username or email/i });
    await user.type(usernameOrEmail, 'user');

    const password = document.querySelector('input[name="password"]');
    await user.type(password, 'pass');

    nockJophiel()
      .post('/session/login', { usernameOrEmail: 'user', password: 'pass' })
      .reply(200, { token: 'token123' });
    nockJophiel().get('/users/me').reply(200, { jid: 'userJid', username: 'user' });

    const submitButton = screen.getByRole('button', { name: /log in/i });
    await user.click(submitButton);

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
