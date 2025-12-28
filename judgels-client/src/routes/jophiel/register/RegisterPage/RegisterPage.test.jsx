import { act, render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import RegisterPage from './RegisterPage';

import * as registerActions from '../modules/registerActions';

vi.mock('../modules/registerActions');

describe('RegisterPage', () => {
  beforeEach(async () => {
    registerActions.getWebConfig.mockReturnValue(() => Promise.resolve({ useRecaptcha: false }));
    registerActions.registerUser.mockReturnValue(() => Promise.resolve());

    const store = createStore(() => {}, applyMiddleware(thunk));

    await act(async () =>
      render(
        <Provider store={store}>
          <MemoryRouter>
            <RegisterPage />
          </MemoryRouter>
        </Provider>
      )
    );
  });

  test('form', async () => {
    const user = userEvent.setup();

    const username = screen.getByRole('textbox', { name: /username/i });
    await user.type(username, 'user');

    const name = screen.getByRole('textbox', { name: /^name/i });
    await user.type(name, 'name');

    const email = screen.getByRole('textbox', { name: /email/i });
    await user.type(email, 'email@domain.com');

    const password = document.querySelector('input[name="password"]');
    await user.type(password, 'pass');

    const confirmPassword = document.querySelector('input[name="confirmPassword"]');
    await user.type(confirmPassword, 'pass');

    const submitButton = screen.getByRole('button', { name: /register/i });
    await user.click(submitButton);

    expect(registerActions.registerUser).toHaveBeenCalledWith({
      username: 'user',
      name: 'name',
      email: 'email@domain.com',
      password: 'pass',
    });

    expect(screen.queryByRole('textbox')).not.toBeInTheDocument();
    expect(document.querySelector('[data-key="instruction"]')).toHaveTextContent('email@domain.com');
  });
});
