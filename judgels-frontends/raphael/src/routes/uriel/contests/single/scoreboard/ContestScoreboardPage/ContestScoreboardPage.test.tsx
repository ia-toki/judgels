import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { IntlProvider } from 'react-intl';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import thunk from 'redux-thunk';

import { contest, user } from 'fixtures/state';
import { PutUser, sessionReducer } from 'modules/session/sessionReducer';
import { ContestScoreboardResponse } from 'modules/api/uriel/contestScoreboard';
import { ContestScoreboardType } from 'modules/api/uriel/contestScoreboard';

import { createContestScoreboardPage } from './ContestScoreboardPage';
import { IcpcScoreboardTable } from '../IcpcScoreboardTable/IcpcScoreboardTable';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { contestReducer, PutContest } from '../../../modules/contestReducer';

describe('ContestScoreboardPage', () => {
  let wrapper: ReactWrapper<any, any>;
  let contestScoreboardActions: jest.Mocked<any>;

  const render = () => {
    const store = createStore(
      combineReducers({ session: sessionReducer, uriel: combineReducers({ contest: contestReducer }) }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutUser.create(user));
    store.dispatch(PutContest.create(contest));

    const ContestScoreboardPage = createContestScoreboardPage(contestScoreboardActions);

    wrapper = mount(
      <IntlProvider locale={navigator.language}>
        <Provider store={store}>
          <MemoryRouter>
            <ContestScoreboardPage />
          </MemoryRouter>
        </Provider>
      </IntlProvider>
    );
  };

  beforeEach(() => {
    contestScoreboardActions = {
      getScoreboard: jest.fn(),
    };
  });

  describe('when there is no scoreboard', () => {
    beforeEach(() => {
      contestScoreboardActions.getScoreboard.mockReturnValue(() => Promise.resolve(null));
      render();
    });

    it('shows placeholder text and no scoreboard', async () => {
      await new Promise(resolve => setImmediate(resolve));
      wrapper.update();

      expect(wrapper.text()).toContain('No scoreboard.');
      expect(wrapper.find(IcpcScoreboardTable)).toHaveLength(0);
    });
  });

  describe('when there is official scoreboard', () => {
    beforeEach(() => {
      const response: ContestScoreboardResponse = {
        data: {
          type: ContestScoreboardType.Official,
          scoreboard: {
            state: {
              contestantJids: [],
              problemJids: [],
              problemAliases: [],
              points: [],
            },
            content: {
              entries: [],
            },
          },
          totalEntries: 0,
          updatedTime: 0,
        },
        profilesMap: {},
        config: { canViewOfficialAndFrozen: false, canViewClosedProblems: false, pageSize: 0 },
      };
      contestScoreboardActions.getScoreboard.mockReturnValue(() => Promise.resolve(response));

      render();
    });

    it('shows the scoreboard without frozen notice', async () => {
      await new Promise(resolve => setImmediate(resolve));
      wrapper.update();

      expect(wrapper.text()).not.toContain('FROZEN');
      expect(wrapper.find(IcpcScoreboardTable)).toHaveLength(1);
    });
  });

  describe('when there is frozen scoreboard', () => {
    beforeEach(() => {
      const response: ContestScoreboardResponse = {
        data: {
          type: ContestScoreboardType.Frozen,
          scoreboard: {
            state: {
              contestantJids: [],
              problemJids: [],
              problemAliases: [],
              points: [],
            },
            content: {
              entries: [],
            },
          },
          totalEntries: 0,
          updatedTime: 0,
        },
        profilesMap: {},
        config: { canViewOfficialAndFrozen: false, canViewClosedProblems: false, pageSize: 0 },
      };
      contestScoreboardActions.getScoreboard.mockReturnValue(() => Promise.resolve(response));

      render();
    });

    it('shows the scoreboard with frozen notice', async () => {
      await new Promise(resolve => setImmediate(resolve));
      wrapper.update();

      expect(wrapper.text()).toContain('FROZEN');
      expect(wrapper.find(IcpcScoreboardTable)).toHaveLength(1);
    });
  });
});
