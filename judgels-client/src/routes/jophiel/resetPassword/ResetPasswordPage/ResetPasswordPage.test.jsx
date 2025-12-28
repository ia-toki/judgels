import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import { MemoryRouter, Route, Routes } from 'react-router';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import ResetPasswordPage from './ResetPasswordPage';

import * as resetPasswordActions from '../modules/resetPasswordActions';

vi.mock('../modules/resetPasswordActions');

describe('ResetPasswordPage', () => {
  beforeEach(() => {
    resetPasswordActions.resetPassword.mockReturnValue(() => Promise.resolve());

    const store = configureMockStore([thunk])({});

    render(
      <Provider store={store}>
        <MemoryRouter initialEntries={['/reset-password/code123']}>
          <Routes>
            <Route path="/reset-password/:emailCode" element={<ResetPasswordPage />} />
          </Routes>
        </MemoryRouter>
      </Provider>
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
