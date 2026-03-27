import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJophiel } from '../../../../utils/nock';
import ResetPasswordPage from './ResetPasswordPage';

describe('ResetPasswordPage', () => {
  const renderComponent = async () => {
    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/reset-password/code123']} path="/reset-password/$emailCode">
            <ResetPasswordPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('form', async () => {
    await renderComponent();

    const user = userEvent.setup();

    const password = screen.getByLabelText(/^new password$/i);
    await user.type(password, 'pass');

    const confirmPassword = screen.getByLabelText(/^confirm new password$/i);
    await user.type(confirmPassword, 'pass');

    nockJophiel().post('/user-account/reset-password', { emailCode: 'code123', newPassword: 'pass' }).reply(200);

    const submitButton = screen.getByRole('button', { name: /reset password/i });
    await user.click(submitButton);

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
