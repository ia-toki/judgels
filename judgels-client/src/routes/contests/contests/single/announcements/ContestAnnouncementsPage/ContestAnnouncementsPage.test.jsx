import { act, render, screen, waitFor, within } from '@testing-library/react';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import sessionReducer, { PutUser } from '../../../../../../modules/session/sessionReducer';
import { WebPrefsProvider } from '../../../../../../modules/webPrefs';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import ContestAnnouncementsPage from './ContestAnnouncementsPage';

import * as contestAnnouncementActions from '../modules/contestAnnouncementActions';

vi.mock('../modules/contestAnnouncementActions');

describe('ContestAnnouncementsPage', () => {
  let announcements;
  let canSupervise;
  let canManage;

  const renderComponent = async () => {
    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });

    contestAnnouncementActions.getAnnouncements.mockReturnValue(() =>
      Promise.resolve({
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
      })
    );

    const store = createStore(combineReducers({ session: sessionReducer }), applyMiddleware(thunk));
    store.dispatch(PutUser({ jid: 'userJid' }));

    await act(async () =>
      render(
        <WebPrefsProvider>
          <QueryClientProviderWrapper>
            <Provider store={store}>
              <TestRouter
                initialEntries={['/contests/contest-slug/announcements']}
                path="/contests/$contestSlug/announcements"
              >
                <ContestAnnouncementsPage />
              </TestRouter>
            </Provider>
          </QueryClientProviderWrapper>
        </WebPrefsProvider>
      )
    );
  };

  describe('action buttons', () => {
    beforeEach(() => {
      announcements = [];
    });

    describe('when not canManage', () => {
      beforeEach(async () => {
        canManage = false;
        await renderComponent();
      });

      it('shows no buttons', async () => {
        await screen.findByRole('heading', { name: 'Announcements' });
        expect(screen.queryByRole('button', { name: /new announcement/i })).not.toBeInTheDocument();
      });
    });

    describe('when canManage', () => {
      beforeEach(async () => {
        canManage = true;
        await renderComponent();
      });

      it('shows action buttons', async () => {
        expect(await screen.findByRole('button', { name: /new announcement/i })).toBeInTheDocument();
      });
    });
  });

  describe('content', () => {
    describe('when there are no announcements', () => {
      beforeEach(async () => {
        announcements = [];
        await renderComponent();
      });

      it('shows placeholder text and no announcements', async () => {
        expect(await screen.findByText('No announcements.')).toBeInTheDocument();
        expect(document.querySelectorAll('div.contest-announcement-card')).toHaveLength(0);
      });
    });

    describe('when there are announcements', () => {
      beforeEach(() => {
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
        ];
      });

      describe('when not canSupervise', () => {
        beforeEach(async () => {
          canSupervise = false;
          canManage = false;
          await renderComponent();
        });

        it('shows the announcements', async () => {
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
      });

      describe('when canSupervise', () => {
        beforeEach(async () => {
          canSupervise = true;
          canManage = false;
          await renderComponent();
        });

        it('shows the announcements', async () => {
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
      });

      describe('when canManage', () => {
        beforeEach(async () => {
          canSupervise = true;
          canManage = true;
          await renderComponent();
        });

        it('shows the announcements', async () => {
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
    });
  });
});
