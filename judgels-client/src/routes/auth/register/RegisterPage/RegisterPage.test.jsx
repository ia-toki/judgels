import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import RegisterPage from './RegisterPage';

describe('RegisterPage', () => {
  const renderComponent = async () => {
    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <RegisterPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  const fillForm = async user => {
    const username = await screen.findByRole('textbox', { name: /username/i });
    await user.type(username, 'user');

    const name = screen.getByRole('textbox', { name: /^name/i });
    await user.type(name, 'name');

    const email = screen.getByRole('textbox', { name: /email/i });
    await user.type(email, 'email@domain.com');

    const password = screen.getByLabelText(/^password$/i);
    await user.type(password, 'pass');

    const confirmPassword = screen.getByLabelText(/confirm password/i);
    await user.type(confirmPassword, 'pass');
  };

  test('form', async () => {
    await renderComponent();

    const user = userEvent.setup();

    await fillForm(user);

    nockApi()
      .post('/v4/account/registration', {
        username: 'user',
        name: 'name',
        email: 'email@domain.com',
        password: 'pass',
      })
      .reply(200);

    const submitButton = screen.getByRole('button', { name: /register/i });
    await user.click(submitButton);

    await waitFor(() => expect(nock.isDone()).toBe(true));
    await waitFor(() => expect(screen.queryByRole('textbox')).not.toBeInTheDocument());
    expect(document.querySelector('[data-key="instruction"]')).toHaveTextContent('email@domain.com');
  });

  test('form with a taken username', async () => {
    await renderComponent();

    const user = userEvent.setup();
    await fillForm(user);

    nockApi()
      .post('/v4/account/registration')
      .reply(400, { code: 400, message: 'AccountRegistrationUsernameAlreadyExists' });

    const submitButton = screen.getByRole('button', { name: /register/i });
    await user.click(submitButton);

    expect(await screen.findByText('Username already exists')).toBeInTheDocument();
  });
});
