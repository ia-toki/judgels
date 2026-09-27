import { act, render, screen, waitFor, within } from '@testing-library/react';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import ProblemsPage from './ProblemsPage';

describe('ProblemsPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({
    problems = [
      { jid: 'JIDPROG1', id: 1, slug: 'problem-1', type: 'PROGRAMMING', authorJid: 'JIDUSER1' },
      { jid: 'JIDBUND2', id: 2, slug: 'problem-2', type: 'BUNDLE', authorJid: 'JIDUSER2' },
    ],
    profilesMap = {
      JIDUSER1: { username: 'user1' },
      JIDUSER2: { username: 'user2' },
    },
  } = {}) => {
    nockApi()
      .get('/v4/problems')
      .query(true)
      .reply(200, {
        data: { page: problems, totalCount: problems.length },
        profilesMap,
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/problems']}>
            <ProblemsPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('renders placeholder when there are no problems', async () => {
    await renderComponent({ problems: [], profilesMap: {} });
    expect(await screen.findByText(/no problems/i)).toBeInTheDocument();
  });

  test('renders the problems table', async () => {
    await renderComponent();

    await waitFor(() => {
      expect(screen.getAllByRole('row').length).toBeGreaterThan(1);
    });
    const rows = screen.getAllByRole('row');
    expect(
      rows.map(row =>
        within(row)
          .queryAllByRole('cell')
          .map(cell => cell.textContent)
      )
    ).toEqual([[], ['1', 'problem-1', 'Programming', 'user1'], ['2', 'problem-2', 'Bundle', 'user2']]);
  });
});
