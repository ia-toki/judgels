import { act, render, screen } from '@testing-library/react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router-dom';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import { ContestStyle } from '../../../../../../modules/api/uriel/contest';
import { ContestScoreboardType } from '../../../../../../modules/api/uriel/contestScoreboard';
import sessionReducer, { PutUser } from '../../../../../../modules/session/sessionReducer';
import contestReducer, { PutContest } from '../../../modules/contestReducer';
import ContestScoreboardPage from './ContestScoreboardPage';

import * as contestScoreboardActions from '../modules/contestScoreboardActions';

vi.mock('../modules/contestScoreboardActions');

describe('ContestScoreboardPage', () => {
  let scoreboard;

  const renderComponent = async () => {
    contestScoreboardActions.getScoreboard.mockReturnValue(() => Promise.resolve(scoreboard));

    const store = createStore(
      combineReducers({ session: sessionReducer, uriel: combineReducers({ contest: contestReducer }) }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutUser({ jid: 'userJid' }));
    store.dispatch(PutContest({ jid: 'contestJid', style: ContestStyle.ICPC }));

    await act(async () =>
      render(
        <Provider store={store}>
          <MemoryRouter>
            <ContestScoreboardPage />
          </MemoryRouter>
        </Provider>
      )
    );
  };

  describe('when there is no scoreboard', () => {
    beforeEach(async () => {
      await renderComponent();
    });

    it('shows placeholder text and no scoreboard', () => {
      expect(screen.getByText(/no scoreboard/i)).toBeInTheDocument();
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

    it('shows the scoreboard without frozen notice', () => {
      expect(screen.queryByText(/FROZEN/)).not.toBeInTheDocument();
      expect(screen.getByRole('table')).toBeInTheDocument();
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

    it('shows the scoreboard with frozen notice', () => {
      expect(screen.getByText(/FROZEN/)).toBeInTheDocument();
      expect(screen.getByRole('table')).toBeInTheDocument();
    });
  });
});
