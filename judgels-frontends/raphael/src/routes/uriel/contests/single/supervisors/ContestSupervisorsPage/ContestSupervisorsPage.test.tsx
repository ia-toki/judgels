import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { IntlProvider } from 'react-intl';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import { contest } from 'fixtures/state';
import { ContestContestant, ContestContestantsResponse } from 'modules/api/uriel/contestContestant';

import { createContestSupervisorsPage } from './ContestSupervisorsPage';
import { contestReducer, PutContest } from '../../../modules/contestReducer';

describe('ContestContestantsPage', () => {
  let wrapper: ReactWrapper<any, any>;
  let contestContestantActions: jest.Mocked<any>;

  const response: ContestContestantsResponse = {
    data: { page: [], totalCount: 0 },
    profilesMap: {
      userJid1: { username: 'user1' },
      userJid2: { username: 'user2' },
    },
    config: {
      canManage: true,
    },
  };

  const render = () => {
    const store = createStore(
      combineReducers({ uriel: combineReducers({ contest: contestReducer }), form: formReducer }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutContest.create(contest));

    const ContestContestantsPage = createContestSupervisorsPage(contestContestantActions);

    wrapper = mount(
      <IntlProvider locale={navigator.language}>
        <Provider store={store}>
          <MemoryRouter>
            <ContestContestantsPage />
          </MemoryRouter>
        </Provider>
      </IntlProvider>
    );
  };

  beforeEach(() => {
    contestContestantActions = {
      getContestants: jest.fn().mockReturnValue(() => Promise.resolve(response)),
    };
  });

  describe('when there are no contestants', () => {
    beforeEach(() => {
      render();
    });

    it('shows placeholder text and no contestants', async () => {
      await new Promise(resolve => setImmediate(resolve));
      wrapper.update();

      expect(wrapper.text()).toContain('No contestants.');
      expect(wrapper.find('tr')).toHaveLength(0);
    });
  });

  describe('when there are contestants', () => {
    beforeEach(() => {
      const contestants: ContestContestant[] = [
        {
          userJid: 'userJid1',
        } as ContestContestant,
        {
          userJid: 'userJid2',
        } as ContestContestant,
      ];
      contestContestantActions.getContestants.mockReturnValue(() =>
        Promise.resolve({ ...response, data: { page: contestants, totalCount: 2 } })
      );

      render();
    });

    it('shows the contestants', async () => {
      await new Promise(resolve => setImmediate(resolve));
      wrapper.update();

      expect(wrapper.find('tr')).toHaveLength(3);
    });
  });
});
