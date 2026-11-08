import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import AccountSettingsPage from './AccountSettingsPage';

describe('AccountSettingsPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const settings = {
    app: { name: 'Judgels', slogan: 'Programming Contest System', announcement: 'Hi' },
    home: { banner: 'Welcome' },
    session: { disableLogout: false, maxConcurrentSessionsPerUser: 5 },
    userRegistration: {
      enabled: false,
      recaptchaEnabled: false,
      activationEmailTemplate: { subject: 'Activate', body: 'Code: {{emailCode}}' },
    },
    userResetPassword: {
      enabled: false,
      requestEmailTemplate: { subject: 'Reset?', body: 'Reset code: {{emailCode}}' },
      resetEmailTemplate: { subject: 'Reset', body: 'Done' },
    },
  };

  const renderComponent = async () => {
    nockApi().get('/v2/settings').reply(200, settings);

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <AccountSettingsPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('details', async () => {
    await renderComponent();

    await screen.findByText('User registration');
    expect(screen.getByText('User password reset')).toBeInTheDocument();
    expect(screen.getByText('Activate')).toBeInTheDocument();
    expect(screen.getByText('Reset?')).toBeInTheDocument();
    expect(screen.queryByText('Session')).not.toBeInTheDocument();
  });

  test('user registration form', async () => {
    await renderComponent();

    const user = userEvent.setup();

    const editButtons = await screen.findAllByRole('button', { name: /edit/i });
    await user.click(editButtons[0]);

    await user.click(screen.getByLabelText(/^enabled$/i));

    const subject = screen.getByLabelText(/activation email subject/i);
    await user.clear(subject);
    await user.type(subject, 'Welcome');

    nockApi()
      .put('/v2/settings', {
        userRegistration: {
          enabled: true,
          recaptchaEnabled: false,
          activationEmailTemplate: { subject: 'Welcome', body: 'Code: {{emailCode}}' },
        },
      })
      .reply(200);

    await user.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('user password reset form', async () => {
    await renderComponent();

    const user = userEvent.setup();

    const editButtons = await screen.findAllByRole('button', { name: /edit/i });
    await user.click(editButtons[1]);

    await user.click(screen.getByLabelText(/^enabled$/i));

    nockApi()
      .put('/v2/settings', {
        userResetPassword: {
          enabled: true,
          requestEmailTemplate: { subject: 'Reset?', body: 'Reset code: {{emailCode}}' },
          resetEmailTemplate: { subject: 'Reset', body: 'Done' },
        },
      })
      .reply(200);

    await user.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
