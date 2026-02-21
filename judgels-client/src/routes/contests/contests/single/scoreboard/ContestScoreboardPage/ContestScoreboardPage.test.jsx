import { act, render, screen } from '@testing-library/react';
import nock from 'nock';

import { ContestStyle } from '../../../../../../modules/api/uriel/contest';
import { ContestScoreboardType } from '../../../../../../modules/api/uriel/contestScoreboard';
import { setSession } from '../../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import ContestScoreboardPage from './ContestScoreboardPage';

describe('ContestScoreboardPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  afterEach(() => {
    nock.cleanAll();
  });

  let scoreboard;

  const renderComponent = async () => {
    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
      style: ContestStyle.ICPC,
    });

    nockUriel()
      .get('/contests/contestJid/scoreboard')
      .query({ frozen: false, showClosedProblems: false, page: 1 })
      .reply(200, scoreboard);

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
      scoreboard = undefined;
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
