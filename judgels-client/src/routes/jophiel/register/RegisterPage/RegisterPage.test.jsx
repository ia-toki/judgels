import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJophiel } from '../../../../utils/nock';
import RegisterPage from './RegisterPage';

describe('RegisterPage', () => {
  beforeEach(async () => {
    nockJophiel().get('/users/registration/web/config').reply(200, { useRecaptcha: false });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <RegisterPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  });

  test('form', async () => {
    const user = userEvent.setup();

    const username = await screen.findByRole('textbox', { name: /username/i });
    await user.type(username, 'user');

    const name = screen.getByRole('textbox', { name: /^name/i });
    await user.type(name, 'name');

    const email = screen.getByRole('textbox', { name: /email/i });
    await user.type(email, 'email@domain.com');

    const password = document.querySelector('input[name="password"]');
    await user.type(password, 'pass');

    const confirmPassword = document.querySelector('input[name="confirmPassword"]');
    await user.type(confirmPassword, 'pass');

    nockJophiel().get('/user-search/username-exists/user').reply(200, false);
    nockJophiel().get('/user-search/email-exists/email@domain.com').reply(200, false);
    nockJophiel()
      .post('/user-account/register', {
        username: 'user',
        name: 'name',
        email: 'email@domain.com',
        password: 'pass',
      })
      .reply(200);

    const submitButton = screen.getByRole('button', { name: /register/i });
    await user.click(submitButton);

    await waitFor(() => expect(nock.isDone()).toBe(true));
    expect(screen.queryByRole('textbox')).not.toBeInTheDocument();
    expect(document.querySelector('[data-key="instruction"]')).toHaveTextContent('email@domain.com');
  });
});
