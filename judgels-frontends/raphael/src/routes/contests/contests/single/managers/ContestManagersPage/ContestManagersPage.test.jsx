import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import ContestManagersPage from './ContestManagersPage';
import contestReducer, { PutContest } from '../../../modules/contestReducer';
import * as contestManagerActions from '../modules/contestManagerActions';

jest.mock('../modules/contestManagerActions');

describe('ContestManagersPage', () => {
  let wrapper;
  let managers;
  let canManage;

  const render = async () => {
    contestManagerActions.getManagers.mockReturnValue(() =>
      Promise.resolve({
        data: {
          page: managers,
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
          <ContestManagersPage />
        </MemoryRouter>
      </Provider>
    );

    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();
  };

  describe('action buttons', () => {
    beforeEach(() => {
      managers = [];
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
        expect(wrapper.find('button').map(b => b.text())).toEqual(['plusAdd managers', 'trashRemove managers']);
      });
    });
  });

  describe('content', () => {
    describe('when there are no managers', () => {
      beforeEach(async () => {
        managers = [];
        await render();
      });

      it('shows placeholder text and no managers', () => {
        expect(wrapper.text()).toContain('No managers.');
        expect(wrapper.find('tr')).toHaveLength(0);
      });
    });

    describe('when there are managers', () => {
      beforeEach(async () => {
        managers = [
          {
            userJid: 'userJid1',
          },
          {
            userJid: 'userJid2',
          },
        ];
        await render();
      });

      it('shows the managers', () => {
        expect(wrapper.find('tr').map(tr => tr.find('td').map(td => td.text()))).toEqual([
          [],
          ['username1'],
          ['username2'],
        ]);
      });
    });
  });
});
