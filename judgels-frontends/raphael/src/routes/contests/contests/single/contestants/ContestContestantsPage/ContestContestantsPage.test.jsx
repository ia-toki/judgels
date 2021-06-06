import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import ContestContestantsPage from './ContestContestantsPage';
import contestReducer, { PutContest } from '../../../modules/contestReducer';
import * as contestContestantActions from '../../modules/contestContestantActions';

jest.mock('../../modules/contestContestantActions');

describe('ContestContestantsPage', () => {
  let wrapper;
  let contestants;
  let canManage;

  const render = async () => {
    contestContestantActions.getContestants.mockReturnValue(() =>
      Promise.resolve({
        data: {
          page: contestants,
        },
        profilesMap: {
          userJid1: { username: 'username1' },
          userJid2: { username: 'username2' },
        },
        config: {
          canManage,
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
          <ContestContestantsPage />
        </MemoryRouter>
      </Provider>
    );

    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();
  };

  describe('action buttons', () => {
    beforeEach(() => {
      contestants = [];
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
        expect(wrapper.find('button').map(b => b.text())).toEqual(['plusAdd contestants', 'trashRemove contestants']);
      });
    });
  });

  describe('content', () => {
    describe('when there are no contestants', () => {
      beforeEach(async () => {
        contestants = [];
        await render();
      });

      it('shows placeholder text and no contestants', () => {
        expect(wrapper.text()).toContain('No contestants.');
        expect(wrapper.find('tr')).toHaveLength(0);
      });
    });

    describe('when there are contestants', () => {
      beforeEach(async () => {
        contestants = [
          {
            userJid: 'userJid1',
          },
          {
            userJid: 'userJid2',
          },
        ];
        await render();
      });

      it('shows the contestants', () => {
        expect(wrapper.find('tr').map(tr => tr.find('td').map(td => td.text()))).toEqual([
          [],
          ['1', 'username1'],
          ['2', 'username2'],
        ]);
      });
    });
  });
});
