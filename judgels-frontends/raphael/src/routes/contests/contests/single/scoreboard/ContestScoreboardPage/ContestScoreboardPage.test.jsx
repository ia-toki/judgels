import { mount } from 'enzyme';
import * as React from 'react';
import { IntlProvider } from 'react-intl';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import thunk from 'redux-thunk';

import { contest, user } from '../../../../../../fixtures/state';
import sessionReducer, { PutUser } from '../../../../../../modules/session/sessionReducer';
import { ContestScoreboardType } from '../../../../../../modules/api/uriel/contestScoreboard';
import ContestScoreboardPage from './ContestScoreboardPage';
import { IcpcScoreboardTable } from '../IcpcScoreboardTable/IcpcScoreboardTable';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import contestReducer, { PutContest } from '../../../modules/contestReducer';
import * as contestScoreboardActions from '../modules/contestScoreboardActions';

jest.mock('../modules/contestScoreboardActions');

describe('ContestScoreboardPage', () => {
  let wrapper;

  const render = () => {
    const store = createStore(
      combineReducers({ session: sessionReducer, uriel: combineReducers({ contest: contestReducer }) }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutUser(user));
    store.dispatch(PutContest(contest));

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
      const response = {
        data: {
          type: ContestScoreboardType.Official,
          scoreboard: {
            state: {
              contestantJids: [],
              problemJids: [],
              problemAliases: [],
            },
            content: {
              entries: [],
            },
          },
          totalEntries: 0,
          updatedTime: 0,
        },
        profilesMap: {},
        config: { canViewOfficialAndFrozen: false, canViewClosedProblems: false, canRefresh: true, pageSize: 0 },
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
      const response = {
        data: {
          type: ContestScoreboardType.Frozen,
          scoreboard: {
            state: {
              contestantJids: [],
              problemJids: [],
              problemAliases: [],
            },
            content: {
              entries: [],
            },
          },
          totalEntries: 0,
          updatedTime: 0,
        },
        profilesMap: {},
        config: { canViewOfficialAndFrozen: false, canViewClosedProblems: false, canRefresh: true, pageSize: 0 },
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
