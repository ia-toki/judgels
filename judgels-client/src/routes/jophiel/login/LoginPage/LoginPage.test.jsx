import { act, render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import createMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import { TestRouter } from '../../../../test/RouterWrapper';
import LoginPage from './LoginPage';

import * as loginActions from '../modules/loginActions';

vi.mock('../modules/loginActions');

describe('LoginPage', () => {
  beforeEach(async () => {
    loginActions.logIn.mockReturnValue(() => Promise.resolve());

    const store = createMockStore([thunk])({});

    await act(async () =>
      render(
        <Provider store={store}>
          <TestRouter>
            <LoginPage />
          </TestRouter>
        </Provider>
      )
    );
  });

  test('form', async () => {
    const user = userEvent.setup();

    const usernameOrEmail = screen.getByRole('textbox', { name: /username or email/i });
    await user.type(usernameOrEmail, 'user');

    const password = document.querySelector('input[name="password"]');
    await user.type(password, 'pass');

    const submitButton = screen.getByRole('button', { name: /log in/i });
    await user.click(submitButton);

    expect(loginActions.logIn).toHaveBeenCalled();
  });
});
