import { act, render, screen, waitFor, within } from '@testing-library/react';

import { setSession } from '../../../../../../modules/session';
import { WebPrefsProvider } from '../../../../../../modules/webPrefs';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import ContestAnnouncementsPage from './ContestAnnouncementsPage';

describe('ContestAnnouncementsPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({
    announcements = [
      {
        jid: 'jid1',
        userJid: 'userJid1',
        title: 'Title 1',
        content: 'Content 1',
        updatedTime: new Date(new Date().setDate(new Date().getDate() - 1)).getTime(),
      },
      {
        jid: 'jid2',
        userJid: 'userJid2',
        title: 'Title 2',
        content: 'Content 2',
        updatedTime: new Date(new Date().setDate(new Date().getDate() - 1)).getTime(),
      },
    ],
    canSupervise,
    canManage,
  } = {}) => {
    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });

    nockUriel()
      .get('/contests/contestJid/announcements')
      .reply(200, {
        data: {
          page: announcements,
        },
        config: {
          canSupervise,
          canManage,
        },
        profilesMap: {
          userJid1: { username: 'username1' },
          userJid2: { username: 'username2' },
        },
      });

    await act(async () =>
      render(
        <WebPrefsProvider>
          <QueryClientProviderWrapper>
            <TestRouter
              initialEntries={['/contests/contest-slug/announcements']}
              path="/contests/$contestSlug/announcements"
            >
              <ContestAnnouncementsPage />
            </TestRouter>
          </QueryClientProviderWrapper>
        </WebPrefsProvider>
      )
    );
  };

  test('renders no action buttons when not canManage', async () => {
    await renderComponent({ canManage: false });
    await screen.findByRole('heading', { name: 'Announcements' });
    expect(screen.queryByRole('button', { name: /new announcement/i })).not.toBeInTheDocument();
  });

  test('renders action buttons when canManage', async () => {
    await renderComponent({ canManage: true });
    expect(await screen.findByRole('button', { name: /new announcement/i })).toBeInTheDocument();
  });

  test('renders placeholder when there are no announcements', async () => {
    await renderComponent({ announcements: [] });
    expect(await screen.findByText('No announcements.')).toBeInTheDocument();
    expect(document.querySelectorAll('div.contest-announcement-card')).toHaveLength(0);
  });

  test('renders announcements when not canSupervise', async () => {
    await renderComponent({ canSupervise: false });
    await waitFor(() => {
      expect(document.querySelectorAll('div.contest-announcement-card').length).toBeGreaterThan(0);
    });
    const announcements = document.querySelectorAll('div.contest-announcement-card');
    expect(announcements).toHaveLength(2);

    expect(within(announcements[0]).getByRole('heading')).toHaveTextContent('Title 1');
    expect(within(announcements[0]).getByText('Content 1')).toBeInTheDocument();
    expect(announcements[0].querySelector('small')).toHaveTextContent(/published 1 day ago$/);

    expect(within(announcements[1]).getByRole('heading')).toHaveTextContent('Title 2');
    expect(within(announcements[1]).getByText('Content 2')).toBeInTheDocument();
    expect(announcements[1].querySelector('small')).toHaveTextContent(/published 1 day ago$/);
  });

  test('renders announcements when canSupervise', async () => {
    await renderComponent({ canSupervise: true });
    await waitFor(() => {
      expect(document.querySelectorAll('div.contest-announcement-card').length).toBeGreaterThan(0);
    });
    const announcements = document.querySelectorAll('div.contest-announcement-card');
    expect(announcements).toHaveLength(2);

    expect(within(announcements[0]).getByRole('heading')).toHaveTextContent('Title 1');
    expect(within(announcements[0]).getByText('Content 1')).toBeInTheDocument();
    expect(announcements[0].querySelector('small')).toHaveTextContent(/published 1 day ago by username1$/);

    expect(within(announcements[1]).getByRole('heading')).toHaveTextContent('Title 2');
    expect(within(announcements[1]).getByText('Content 2')).toBeInTheDocument();
    expect(announcements[1].querySelector('small')).toHaveTextContent(/published 1 day ago by username2$/);
  });

  test('renders announcements when canManage', async () => {
    await renderComponent({ canSupervise: true, canManage: true });
    await waitFor(() => {
      expect(document.querySelectorAll('div.contest-announcement-card').length).toBeGreaterThan(0);
    });
    const announcements = document.querySelectorAll('div.contest-announcement-card');
    expect(announcements).toHaveLength(2);

    expect(within(announcements[0]).getByRole('heading')).toHaveTextContent('Title 1');
    expect(within(announcements[0]).getByText('Content 1')).toBeInTheDocument();
    expect(announcements[0].querySelector('small')).toHaveTextContent(/published 1 day ago by username1 Edit$/);

    expect(within(announcements[1]).getByRole('heading')).toHaveTextContent('Title 2');
    expect(within(announcements[1]).getByText('Content 2')).toBeInTheDocument();
    expect(announcements[1].querySelector('small')).toHaveTextContent(/published 1 day ago by username2 Edit$/);
  });
});
