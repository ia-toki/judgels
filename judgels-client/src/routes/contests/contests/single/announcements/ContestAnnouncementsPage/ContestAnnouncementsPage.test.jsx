import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import contestReducer, { PutContest } from '../../../modules/contestReducer';
import ContestAnnouncementsPage from './ContestAnnouncementsPage';

import * as contestAnnouncementActions from '../modules/contestAnnouncementActions';

jest.mock('../modules/contestAnnouncementActions');

describe('ContestAnnouncementsPage', () => {
  let wrapper;
  let announcements;
  let canSupervise;
  let canManage;

  const render = async () => {
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

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ContestAnnouncementsPage />
        </MemoryRouter>
      </Provider>
    );

    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();
  };

  describe('action buttons', () => {
    beforeEach(() => {
      announcements = [];
    });

    describe('when not canManage', () => {
      beforeEach(async () => {
        canManage = false;
        await render();
      });

      it('shows no buttons', () => {
        expect(wrapper.find('button')).toHaveLength(0);
      });
    });

    describe('when canManage', () => {
      beforeEach(async () => {
        canManage = true;
        await render();
      });

      it('shows action buttons', () => {
        expect(wrapper.find('button').map(b => b.text())).toEqual(['New announcement']);
      });
    });
  });

  describe('content', () => {
    describe('when there are no announcements', () => {
      beforeEach(async () => {
        announcements = [];
        await render();
      });

      it('shows placeholder text and no announcements', () => {
        expect(wrapper.text()).toContain('No announcements.');
        expect(wrapper.find('div.contest-announcement-card')).toHaveLength(0);
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
          await render();
        });

        it('shows the announcements', () => {
          const cards = wrapper.find('div.contest-announcement-card');
          expect(
            cards.map(card => [
              card.find('h5').text(),
              card.find('p').text().replace(/\s+/g, ' '),
              card.find('.html-text').text(),
            ])
          ).toEqual([
            ['Title 1', 'published 1 day ago ', 'Content 1'],
            ['Title 2', 'published 1 day ago ', 'Content 2'],
          ]);
        });
      });

      describe('when canSupervise', () => {
        beforeEach(async () => {
          canSupervise = true;
          canManage = false;
          await render();
        });

        it('shows the announcements', () => {
          const cards = wrapper.find('div.contest-announcement-card');
          expect(
            cards.map(card => [
              card.find('h5').text(),
              card.find('p').text().replace(/\s+/g, ' '),
              card.find('.html-text').text(),
            ])
          ).toEqual([
            ['Title 1', 'published 1 day ago by username1 ', 'Content 1'],
            ['Title 2', 'published 1 day ago by username2 ', 'Content 2'],
          ]);
        });
      });

      describe('when canManage', () => {
        beforeEach(async () => {
          canSupervise = true;
          canManage = true;
          await render();
        });

        it('shows the announcements', () => {
          const cards = wrapper.find('div.contest-announcement-card');
          expect(
            cards.map(card => [
              card.find('h5').text(),
              card.find('p').text().replace(/\s+/g, ' '),
              card.find('.html-text').text(),
            ])
          ).toEqual([
            ['Title 1', 'published 1 day ago by username1 Edit', 'Content 1'],
            ['Title 2', 'published 1 day ago by username2 Edit', 'Content 2'],
          ]);
        });
      });
    });
  });
});
