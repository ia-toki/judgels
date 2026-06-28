import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import SettingsPage from './SettingsPage';

describe('SettingsPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const settings = {
    app: { name: 'Judgels', slogan: 'Programming Contest System', announcement: 'Hi' },
    home: { banner: 'Welcome' },
    session: { disableLogout: false, maxConcurrentSessionsPerUser: 5 },
  };

  const renderComponent = async () => {
    nockApi().get('/settings').reply(200, settings);

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <SettingsPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('details', async () => {
    await renderComponent();

    await screen.findByText('App settings');
    expect(screen.getByText('Home settings')).toBeInTheDocument();
    expect(screen.getByText('Session settings')).toBeInTheDocument();

    expect(screen.getByText('Judgels')).toBeInTheDocument();
    expect(screen.getByText('Programming Contest System')).toBeInTheDocument();
    expect(screen.getByText('Welcome')).toBeInTheDocument();
    expect(screen.getByText('5')).toBeInTheDocument();
  });

  test('app form', async () => {
    await renderComponent();

    const user = userEvent.setup();

    const editButtons = await screen.findAllByRole('button', { name: /edit/i });
    await user.click(editButtons[0]);

    const name = screen.getByRole('textbox', { name: /name/i });
    expect(name).toHaveValue('Judgels');
    await user.clear(name);
    await user.type(name, 'New Judge');

    nockApi()
      .put('/settings', { app: { name: 'New Judge', slogan: 'Programming Contest System', announcement: 'Hi' } })
      .reply(200);

    await user.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('home form', async () => {
    await renderComponent();

    const user = userEvent.setup();

    const editButtons = await screen.findAllByRole('button', { name: /edit/i });
    await user.click(editButtons[1]);

    const banner = screen.getByRole('textbox', { name: /banner/i });
    expect(banner).toHaveValue('Welcome');
    await user.clear(banner);
    await user.type(banner, 'New banner');

    nockApi()
      .put('/settings', { home: { banner: 'New banner' } })
      .reply(200);

    await user.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('session form', async () => {
    await renderComponent();

    const user = userEvent.setup();

    const editButtons = await screen.findAllByRole('button', { name: /edit/i });
    await user.click(editButtons[2]);

    const maxSessions = screen.getByLabelText(/max concurrent sessions per user/i);
    await user.clear(maxSessions);
    await user.type(maxSessions, '10');

    await user.click(screen.getByLabelText(/disable logout/i));

    nockApi()
      .put('/settings', { session: { disableLogout: true, maxConcurrentSessionsPerUser: 10 } })
      .reply(200);

    await user.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
