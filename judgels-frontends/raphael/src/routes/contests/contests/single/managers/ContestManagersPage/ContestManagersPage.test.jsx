import { mount } from 'enzyme';
import * as React from 'react';
import { IntlProvider } from 'react-intl';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import { contest } from '../../../../../../fixtures/state';
import ContestManagersPage from './ContestManagersPage';
import contestReducer, { PutContest } from '../../../modules/contestReducer';
import * as contestManagerActions from '../modules/contestManagerActions';

jest.mock('../modules/contestManagerActions');

describe('ContestManagersPage', () => {
  let wrapper;

  const response = {
    data: { page: [], totalCount: 0 },
    profilesMap: {
      userJid1: { username: 'user1' },
      userJid2: { username: 'user2' },
    },
    config: { canManage: true },
  };

  const render = () => {
    const store = createStore(
      combineReducers({ uriel: combineReducers({ contest: contestReducer }), form: formReducer }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutContest(contest));

    wrapper = mount(
      <IntlProvider locale={navigator.language}>
        <Provider store={store}>
          <MemoryRouter>
            <ContestManagersPage />
          </MemoryRouter>
        </Provider>
      </IntlProvider>
    );
  };

  beforeEach(() => {
    contestManagerActions.getManagers.mockReturnValue(() => Promise.resolve(response));
  });

  describe('when there are no managers', () => {
    beforeEach(() => {
      render();
    });

    it('shows placeholder text and no managers', async () => {
      await new Promise(resolve => setImmediate(resolve));
      wrapper.update();

      expect(wrapper.text()).toContain('No managers.');
      expect(wrapper.find('tr')).toHaveLength(0);
    });
  });

  describe('when there are managers', () => {
    beforeEach(() => {
      const managers = [
        {
          userJid: 'userJid1',
        },
        {
          userJid: 'userJid2',
        },
      ];
      contestManagerActions.getManagers.mockReturnValue(() =>
        Promise.resolve({ ...response, data: { page: managers, totalCount: 2 } })
      );

      render();
    });

    it('shows the managers', async () => {
      await new Promise(resolve => setImmediate(resolve));
      wrapper.update();

      expect(wrapper.find('tr')).toHaveLength(3);
    });
  });
});
