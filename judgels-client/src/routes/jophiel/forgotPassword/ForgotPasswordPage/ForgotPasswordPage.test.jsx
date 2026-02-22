import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJophiel } from '../../../../utils/nock';
import ForgotPasswordPage from './ForgotPasswordPage';

describe('ForgotPasswordPage', () => {
  afterEach(() => {
    nock.cleanAll();
  });

  beforeEach(async () => {
    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <ForgotPasswordPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  });

  test('form', async () => {
    const user = userEvent.setup();

    const email = screen.getByRole('textbox');
    await user.type(email, 'email@domain.com');

    nockJophiel().post('/user-account/request-reset-password/email@domain.com').reply(200);

    const submitButton = screen.getByRole('button', { name: /request to reset password/i });
    await user.click(submitButton);

    await waitFor(() => {
      expect(nock.isDone()).toBe(true);
      expect(document.querySelector('[data-key="instruction"]')).toBeInTheDocument();
    });
  });
});
