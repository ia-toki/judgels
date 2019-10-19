import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { IntlProvider } from 'react-intl';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import { contest } from '../../../../../../fixtures/state';
import { ContestManager, ContestManagersResponse } from '../../../../../../modules/api/uriel/contestManager';

import { createContestManagersPage } from './ContestManagersPage';
import { contestReducer, PutContest } from '../../../modules/contestReducer';

describe('ContestManagersPage', () => {
  let wrapper: ReactWrapper<any, any>;
  let contestManagerActions: jest.Mocked<any>;

  const response: ContestManagersResponse = {
    data: { page: [], totalCount: 0 },
    profilesMap: {
      userJid1: { username: 'user1' },
      userJid2: { username: 'user2' },
    },
    config: { canManage: true },
  };

  const render = () => {
    const store: any = createStore(
      combineReducers({ uriel: combineReducers({ contest: contestReducer }), form: formReducer }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutContest.create(contest));

    const ContestManagersPage = createContestManagersPage(contestManagerActions);

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
    contestManagerActions = {
      getManagers: jest.fn().mockReturnValue(() => Promise.resolve(response)),
    };
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
      const managers: ContestManager[] = [
        {
          userJid: 'userJid1',
        } as ContestManager,
        {
          userJid: 'userJid2',
        } as ContestManager,
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
