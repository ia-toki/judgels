import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import { ContestStyle } from '../../../../../../modules/api/uriel/contest';
import { ContestScoreboardType } from '../../../../../../modules/api/uriel/contestScoreboard';
import sessionReducer, { PutUser } from '../../../../../../modules/session/sessionReducer';
import contestReducer, { PutContest } from '../../../modules/contestReducer';
import { IcpcScoreboardTable } from '../IcpcScoreboardTable/IcpcScoreboardTable';
import ContestScoreboardPage from './ContestScoreboardPage';

import * as contestScoreboardActions from '../modules/contestScoreboardActions';

jest.mock('../modules/contestScoreboardActions');

describe('ContestScoreboardPage', () => {
  let wrapper;
  let scoreboard;

  const render = async () => {
    contestScoreboardActions.getScoreboard.mockReturnValue(() => Promise.resolve(scoreboard));

    const store = createStore(
      combineReducers({ session: sessionReducer, uriel: combineReducers({ contest: contestReducer }) }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutUser({ jid: 'userJid' }));
    store.dispatch(PutContest({ jid: 'contestJid', style: ContestStyle.ICPC }));

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ContestScoreboardPage />
        </MemoryRouter>
      </Provider>
    );

    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();
  };

  describe('when there is no scoreboard', () => {
    beforeEach(async () => {
      await render();
    });

    it('shows placeholder text and no scoreboard', () => {
      expect(wrapper.text()).toContain('No scoreboard.');
      expect(wrapper.find(IcpcScoreboardTable)).toHaveLength(0);
    });
  });

  describe('when there is official scoreboard', () => {
    beforeEach(async () => {
      scoreboard = {
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

      await render();
    });

    it('shows the scoreboard without frozen notice', () => {
      expect(wrapper.text()).not.toContain('FROZEN');
      expect(wrapper.find(IcpcScoreboardTable)).toHaveLength(1);
    });
  });

  describe('when there is frozen scoreboard', () => {
    beforeEach(async () => {
      scoreboard = {
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

      await render();
    });

    it('shows the scoreboard with frozen notice', () => {
      expect(wrapper.text()).toContain('FROZEN');
      expect(wrapper.find(IcpcScoreboardTable)).toHaveLength(1);
    });
  });
});
