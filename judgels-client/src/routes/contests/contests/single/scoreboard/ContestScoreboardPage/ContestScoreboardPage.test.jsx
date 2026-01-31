import { act, render, screen } from '@testing-library/react';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import { ContestStyle } from '../../../../../../modules/api/uriel/contest';
import { ContestScoreboardType } from '../../../../../../modules/api/uriel/contestScoreboard';
import sessionReducer, { PutUser } from '../../../../../../modules/session/sessionReducer';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import ContestScoreboardPage from './ContestScoreboardPage';

import * as contestScoreboardActions from '../modules/contestScoreboardActions';

vi.mock('../modules/contestScoreboardActions');

describe('ContestScoreboardPage', () => {
  let scoreboard;

  const renderComponent = async () => {
    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
      style: ContestStyle.ICPC,
    });

    contestScoreboardActions.getScoreboard.mockReturnValue(() => Promise.resolve(scoreboard));

    const store = createStore(combineReducers({ session: sessionReducer }), applyMiddleware(thunk));
    store.dispatch(PutUser({ jid: 'userJid' }));

    await act(async () => {
      render(
        <QueryClientProviderWrapper>
          <Provider store={store}>
            <TestRouter initialEntries={['/contests/contest-slug/scoreboard']} path="/contests/$contestSlug/scoreboard">
              <ContestScoreboardPage />
            </TestRouter>
          </Provider>
        </QueryClientProviderWrapper>
      );
    });
  };

  describe('when there is no scoreboard', () => {
    beforeEach(async () => {
      await renderComponent();
    });

    it('shows placeholder text and no scoreboard', async () => {
      expect(await screen.findByText(/no scoreboard/i)).toBeInTheDocument();
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

      await renderComponent();
    });

    it('shows the scoreboard without frozen notice', async () => {
      expect(await screen.findByRole('table')).toBeInTheDocument();
      expect(screen.queryByText(/FROZEN/)).not.toBeInTheDocument();
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

      await renderComponent();
    });

    it('shows the scoreboard with frozen notice', async () => {
      expect(await screen.findByRole('table')).toBeInTheDocument();
      expect(screen.getByText(/FROZEN/)).toBeInTheDocument();
    });
  });
});
