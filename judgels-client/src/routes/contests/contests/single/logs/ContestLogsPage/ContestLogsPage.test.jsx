import { act, render, screen } from '@testing-library/react';

import { setSession } from '../../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import ContestLogsPage from './ContestLogsPage';

describe('ContestLogsPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  let logs;

  const renderComponent = async () => {
    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });

    nockUriel()
      .get('/contests/contestJid/logs')
      .reply(200, {
        data: {
          page: logs,
          totalCount: logs.length,
        },
        config: {
          userJids: [],
          problemJids: [],
        },
        profilesMap: {
          userJid1: { username: 'username1' },
          userJid2: { username: 'username2' },
        },
        problemAliasesMap: {
          problemJid1: 'A',
          problemJid2: 'B',
        },
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/contests/contest-slug/logs']} path="/contests/$contestSlug/logs">
            <ContestLogsPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  describe('when there are no logs', () => {
    beforeEach(async () => {
      logs = [];
      await renderComponent();
    });

    it('shows placeholder text and no logs', async () => {
      expect(await screen.findByText(/no logs/i)).toBeInTheDocument();
      expect(screen.queryByRole('row')).not.toBeInTheDocument();
    });
  });

  describe('when there are logs', () => {
    beforeEach(async () => {
      logs = [
        {
          contestJid: 'contestJid',
          userJid: 'userJid1',
          event: 'OPEN_PROBLEM',
          problemJid: 'problemJid1',
          time: new Date(new Date().setDate(new Date().getDate() - 1)).getTime(),
        },
        {
          contestJid: 'contestJid',
          userJid: 'userJid2',
          event: 'OPEN_CLARIFICATIONS',
          time: new Date(new Date().setDate(new Date().getDate() - 1)).getTime(),
        },
      ];
      await renderComponent();
    });

    it('shows the logs', async () => {
      await screen.findAllByRole('row');
      const rows = screen.getAllByRole('row').slice(1);
      expect(rows.map(row => [...row.querySelectorAll('td')].map(cell => cell.textContent))).toEqual([
        ['username1', 'OPEN_PROBLEM', 'A', '1 day ago '],
        ['username2', 'OPEN_CLARIFICATIONS', '', '1 day ago '],
      ]);
    });
  });
});
