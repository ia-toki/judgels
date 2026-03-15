import { act, render, screen } from '@testing-library/react';

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

  const mockScoreboard = {
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

  const renderComponent = async ({ scoreboard: sb } = {}) => {
    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
      style: ContestStyle.ICPC,
    });

    nockUriel()
      .get('/contests/contestJid/scoreboard')
      .query({ frozen: false, showClosedProblems: false })
      .reply(200, sb);

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

  test('renders placeholder when there is no scoreboard', async () => {
    await renderComponent();
    expect(await screen.findByText(/no scoreboard/i)).toBeInTheDocument();
  });

  test('renders scoreboard without frozen notice for official scoreboard', async () => {
    await renderComponent({ scoreboard: mockScoreboard });
    expect(await screen.findByRole('table')).toBeInTheDocument();
    expect(screen.queryByText(/FROZEN/)).not.toBeInTheDocument();
  });

  test('renders scoreboard with frozen notice for frozen scoreboard', async () => {
    await renderComponent({
      scoreboard: {
        ...mockScoreboard,
        data: {
          ...mockScoreboard.data,
          type: ContestScoreboardType.Frozen,
        },
      },
    });
    expect(await screen.findByRole('table')).toBeInTheDocument();
    expect(screen.getByText(/FROZEN/)).toBeInTheDocument();
  });
});
