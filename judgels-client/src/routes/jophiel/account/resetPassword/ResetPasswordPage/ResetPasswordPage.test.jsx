import { act, render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';

import { TestRouter } from '../../../../../test/RouterWrapper';
import ResetPasswordPage from './ResetPasswordPage';

import * as resetPasswordActions from '../modules/resetPasswordActions';

vi.mock('../modules/resetPasswordActions');

describe('ResetPasswordPage', () => {
  beforeEach(async () => {
    resetPasswordActions.requestToResetPassword.mockReturnValue(Promise.resolve());

    await act(async () =>
      render(
        <TestRouter>
          <ResetPasswordPage />
        </TestRouter>
      )
    );
  });

  test('form', async () => {
    const user = userEvent.setup();

    const submitButton = screen.getByRole('button', { name: /request to reset password/i });
    await user.click(submitButton);

    expect(resetPasswordActions.requestToResetPassword).toHaveBeenCalled();
  });
});
