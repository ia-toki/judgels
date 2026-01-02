import { act, render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import { TestRouter } from '../../../../test/RouterWrapper';
import ResetPasswordPage from './ResetPasswordPage';

import * as resetPasswordActions from '../modules/resetPasswordActions';

vi.mock('../modules/resetPasswordActions');

describe('ResetPasswordPage', () => {
  beforeEach(async () => {
    resetPasswordActions.resetPassword.mockReturnValue(() => Promise.resolve());

    const store = configureMockStore([thunk])({});

    await act(async () =>
      render(
        <Provider store={store}>
          <TestRouter initialEntries={['/reset-password/code123']} path="/reset-password/$emailCode">
            <ResetPasswordPage />
          </TestRouter>
        </Provider>
      )
    );
  });

  test('form', async () => {
    const user = userEvent.setup();

    const password = document.querySelector('input[name="password"]');
    await user.type(password, 'pass');

    const confirmPassword = document.querySelector('input[name="confirmPassword"]');
    await user.type(confirmPassword, 'pass');

    const submitButton = screen.getByRole('button', { name: /reset password/i });
    await user.click(submitButton);

    expect(resetPasswordActions.resetPassword).toHaveBeenCalledWith('code123', 'pass');
  });
});
