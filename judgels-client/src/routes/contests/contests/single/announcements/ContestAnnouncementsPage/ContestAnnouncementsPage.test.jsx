import { act, render, screen, within } from '@testing-library/react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import contestReducer, { PutContest } from '../../../modules/contestReducer';
import ContestAnnouncementsPage from './ContestAnnouncementsPage';

import * as contestAnnouncementActions from '../modules/contestAnnouncementActions';

vi.mock('../modules/contestAnnouncementActions');

describe('ContestAnnouncementsPage', () => {
  let announcements;
  let canSupervise;
  let canManage;

  const renderComponent = async () => {
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

    const store = createStore(
      combineReducers({ uriel: combineReducers({ contest: contestReducer }) }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutContest({ jid: 'contestJid' }));

    await act(async () =>
      render(
        <Provider store={store}>
          <MemoryRouter>
            <ContestAnnouncementsPage />
          </MemoryRouter>
        </Provider>
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

      it('shows no buttons', () => {
        expect(screen.queryByRole('button', { name: /new announcement/i })).not.toBeInTheDocument();
      });
    });

    describe('when canManage', () => {
      beforeEach(async () => {
        canManage = true;
        await renderComponent();
      });

      it('shows action buttons', () => {
        expect(screen.getByRole('button', { name: /new announcement/i })).toBeInTheDocument();
      });
    });
  });

  describe('content', () => {
    describe('when there are no announcements', () => {
      beforeEach(async () => {
        announcements = [];
        await renderComponent();
      });

      it('shows placeholder text and no announcements', () => {
        expect(screen.getByText(/no announcements/i)).toBeInTheDocument();
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

        it('shows the announcements', () => {
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

        it('shows the announcements', () => {
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

        it('shows the announcements', () => {
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
