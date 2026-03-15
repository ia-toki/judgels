import { act, render, screen, within } from '@testing-library/react';

import { setSession } from '../../../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../../utils/nock';
import ContestSubmissionsPage from './ContestSubmissionsPage';

describe('ContestSubmissionsPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({
    submissions = [
      {
        id: 20,
        jid: 'jid1',
        userJid: 'userJid1',
        problemJid: 'problemJid1',
        containerJid: 'contestJid',
        gradingEngine: 'Batch',
        gradingLanguage: 'Cpp17',
        time: new Date(new Date().setDate(new Date().getDate() - 1)).getTime(),
      },
      {
        id: 10,
        jid: 'jid2',
        userJid: 'userJid2',
        problemJid: 'problemJid2',
        containerJid: 'contestJid',
        gradingEngine: 'Batch',
        gradingLanguage: 'Cpp17',
        time: new Date(new Date().setDate(new Date().getDate() - 2)).getTime(),
        latestGrading: {
          verdict: { code: 'WA' },
          score: 70,
        },
      },
    ],
    canSupervise = false,
    canManage = false,
  } = {}) => {
    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
      style: 'ICPC',
    });

    nockUriel()
      .get('/contests/submissions/programming')
      .query({ contestJid: 'contestJid' })
      .reply(200, {
        data: { page: submissions, totalCount: submissions.length },
        config: {
          canSupervise,
          canManage,
          userJids: [],
          problemJids: [],
        },
        profilesMap: {
          userJid1: { username: 'user1' },
          userJid2: { username: 'user2' },
        },
        problemAliasesMap: {
          problemJid1: 'A',
          problemJid2: 'B',
        },
      });

    await act(async () => {
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/contests/contest-slug/submissions']} path="/contests/$contestSlug/submissions">
            <ContestSubmissionsPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      );
    });
  };

  test('renders placeholder text when there are no submissions', async () => {
    await renderComponent({ submissions: [] });

    expect(await screen.findByText(/no submissions/i)).toBeInTheDocument();
    const rows = screen.queryAllByRole('row');
    expect(rows).toHaveLength(0);
  });

  test('renders submissions when not canSupervise', async () => {
    await renderComponent();

    await screen.findAllByRole('row');

    const rows = screen.getAllByRole('row').slice(1);
    const data = rows.map(row => {
      const cells = within(row).queryAllByRole('cell');
      return cells.map(cell => cell.textContent.trim());
    });

    expect(data).toEqual([
      ['20', 'A', 'C++17', '', '1 day ago', 'search'],
      ['10', 'B', 'C++17', 'Wrong Answer70', '2 days ago', 'search'],
    ]);
  });

  test('renders submissions with username when canSupervise', async () => {
    await renderComponent({ canSupervise: true });

    await screen.findAllByRole('row');

    const rows = screen.getAllByRole('row').slice(1);
    const data = rows.map(row => {
      const cells = within(row).queryAllByRole('cell');
      return cells.map(cell => cell.textContent.trim());
    });

    expect(data).toEqual([
      ['20', 'user1', 'A', 'C++17', '', '1 day ago', 'search'],
      ['10', 'user2', 'B', 'C++17', 'Wrong Answer70', '2 days ago', 'search'],
    ]);
  });

  test('renders submissions with regrade button when canManage', async () => {
    await renderComponent({ canSupervise: true, canManage: true });

    await screen.findAllByRole('row');

    const rows = screen.getAllByRole('row').slice(1);
    const data = rows.map(row => {
      const cells = within(row).queryAllByRole('cell');
      return cells.map(cell => cell.textContent.replace(/\s+/g, ' ').trim());
    });

    expect(data).toEqual([
      ['20 refresh', 'user1', 'A', 'C++17', '', '1 day ago', 'search'],
      ['10 refresh', 'user2', 'B', 'C++17', 'Wrong Answer70', '2 days ago', 'search'],
    ]);
  });
});
