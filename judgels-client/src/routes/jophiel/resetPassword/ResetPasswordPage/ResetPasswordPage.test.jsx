import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJophiel } from '../../../../utils/nock';
import ResetPasswordPage from './ResetPasswordPage';

describe('ResetPasswordPage', () => {
  afterEach(() => {
    nock.cleanAll();
  });

  beforeEach(async () => {
    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/reset-password/code123']} path="/reset-password/$emailCode">
            <ResetPasswordPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  });

  test('form', async () => {
    const user = userEvent.setup();

    const password = document.querySelector('input[name="password"]');
    await user.type(password, 'pass');

    const confirmPassword = document.querySelector('input[name="confirmPassword"]');
    await user.type(confirmPassword, 'pass');

    nockJophiel().post('/user-account/reset-password', { emailCode: 'code123', newPassword: 'pass' }).reply(200);

    const submitButton = screen.getByRole('button', { name: /reset password/i });
    await user.click(submitButton);

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
