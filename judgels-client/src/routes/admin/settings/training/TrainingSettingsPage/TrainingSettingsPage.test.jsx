import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../test/RouterWrapper';
import { nockApi } from '../../../../../utils/nock';
import TrainingSettingsPage from './TrainingSettingsPage';

describe('TrainingSettingsPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const settings = {
    app: { name: 'Judgels', slogan: 'Programming Contest System', announcement: 'Hi' },
    home: { banner: 'Welcome' },
    session: { disableLogout: false, maxConcurrentSessionsPerUser: 5 },
    trainingAccountRegistration: {
      enabled: false,
      recaptchaEnabled: false,
      activationEmailTemplate: { subject: 'Activate', body: 'Code: {{emailCode}}' },
    },
    trainingAccountPasswordReset: {
      enabled: false,
      requestEmailTemplate: { subject: 'Reset?', body: 'Reset code: {{emailCode}}' },
      resetEmailTemplate: { subject: 'Reset', body: 'Done' },
    },
    trainingGoogleAnalytics: { trackingId: 'G-OLD' },
    mailerConfigured: true,
    recaptchaConfigured: true,
  };

  const renderComponent = async (overrides = {}) => {
    nockApi()
      .get('/v2/settings')
      .reply(200, { ...settings, ...overrides });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <TrainingSettingsPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('details', async () => {
    await renderComponent();

    await screen.findByText('Account registration');
    expect(screen.getByText('Account password reset')).toBeInTheDocument();
    expect(screen.getByText('Activate')).toBeInTheDocument();
    expect(screen.getByText('Reset?')).toBeInTheDocument();
    expect(screen.getByText('Google Analytics')).toBeInTheDocument();
    expect(screen.getByText('G-OLD')).toBeInTheDocument();
    expect(screen.queryByText('Session')).not.toBeInTheDocument();
  });

  test('account registration form', async () => {
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
        trainingAccountRegistration: {
          enabled: true,
          recaptchaEnabled: false,
          activationEmailTemplate: { subject: 'Welcome', body: 'Code: {{emailCode}}' },
        },
      })
      .reply(200);

    await user.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('account password reset form', async () => {
    await renderComponent();

    const user = userEvent.setup();

    const editButtons = await screen.findAllByRole('button', { name: /edit/i });
    await user.click(editButtons[1]);

    await user.click(screen.getByLabelText(/^enabled$/i));

    nockApi()
      .put('/v2/settings', {
        trainingAccountPasswordReset: {
          enabled: true,
          requestEmailTemplate: { subject: 'Reset?', body: 'Reset code: {{emailCode}}' },
          resetEmailTemplate: { subject: 'Reset', body: 'Done' },
        },
      })
      .reply(200);

    await user.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('google analytics form', async () => {
    await renderComponent();

    const user = userEvent.setup();

    const editButtons = await screen.findAllByRole('button', { name: /edit/i });
    await user.click(editButtons[2]);

    const trackingId = screen.getByRole('textbox', { name: /tracking id/i });
    expect(trackingId).toHaveValue('G-OLD');
    await user.clear(trackingId);
    await user.type(trackingId, 'G-NEW');

    nockApi()
      .put('/v2/settings', { trainingGoogleAnalytics: { trackingId: 'G-NEW' } })
      .reply(200);

    await user.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('disables the toggles whose config is missing', async () => {
    await renderComponent({ mailerConfigured: false, recaptchaConfigured: false });

    const user = userEvent.setup();

    const editButtons = await screen.findAllByRole('button', { name: /edit/i });
    await user.click(editButtons[0]);

    expect(screen.getByLabelText(/^enabled$/i)).toBeDisabled();
    expect(screen.getByLabelText(/^recaptcha enabled$/i)).toBeDisabled();
    expect(screen.getByText('Needs the mailer config in judgels-server.yml.')).toBeInTheDocument();
    expect(screen.getByText('Needs the recaptcha config in judgels-server.yml.')).toBeInTheDocument();
  });
});
