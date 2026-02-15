import { act, render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';

import { TestRouter } from '../../../../test/RouterWrapper';
import ResetPasswordPage from './ResetPasswordPage';

import * as resetPasswordActions from '../modules/resetPasswordActions';

vi.mock('../modules/resetPasswordActions');

describe('ResetPasswordPage', () => {
  beforeEach(async () => {
    resetPasswordActions.resetPassword.mockReturnValue(Promise.resolve());

    await act(async () =>
      render(
        <TestRouter initialEntries={['/reset-password/code123']} path="/reset-password/$emailCode">
          <ResetPasswordPage />
        </TestRouter>
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
