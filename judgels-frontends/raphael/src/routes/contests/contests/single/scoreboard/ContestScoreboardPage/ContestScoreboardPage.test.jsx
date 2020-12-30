import { mount } from 'enzyme';
import { IntlProvider } from 'react-intl';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import thunk from 'redux-thunk';

import sessionReducer, { PutUser } from '../../../../../../modules/session/sessionReducer';
import { ContestScoreboardType } from '../../../../../../modules/api/uriel/contestScoreboard';
import ContestScoreboardPage from './ContestScoreboardPage';
import { IcpcScoreboardTable } from '../IcpcScoreboardTable/IcpcScoreboardTable';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import contestReducer, { PutContest } from '../../../modules/contestReducer';
import * as contestScoreboardActions from '../modules/contestScoreboardActions';
import { ContestStyle } from '../../../../../../modules/api/uriel/contest';

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
      <IntlProvider locale={navigator.language}>
        <Provider store={store}>
          <MemoryRouter>
            <ContestScoreboardPage />
          </MemoryRouter>
        </Provider>
      </IntlProvider>
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
