import { act, render, screen } from '@testing-library/react';
import { vi } from 'vitest';

import { ContestStyle } from '../../../../../../modules/api/uriel/contest';
import { ContestScoreboardType } from '../../../../../../modules/api/uriel/contestScoreboard';
import { setSession } from '../../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import ContestScoreboardPage from './ContestScoreboardPage';

import * as contestScoreboardActions from '../modules/contestScoreboardActions';

vi.mock('../modules/contestScoreboardActions');

describe('ContestScoreboardPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  let scoreboard;

  const renderComponent = async () => {
    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
      style: ContestStyle.ICPC,
    });

    contestScoreboardActions.getScoreboard.mockReturnValue(Promise.resolve(scoreboard));

    await act(async () => {
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/contests/contest-slug/scoreboard']} path="/contests/$contestSlug/scoreboard">
            <ContestScoreboardPage />
          </TestRouter>
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
