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
    role = { problem: 'ADMIN' },
    initialEntry = '/admin/problems',
    problemsQuery = true,
    problems = [
      { jid: 'JIDPROG1', id: 1, slug: 'problem-1', type: 'PROGRAMMING', authorJid: 'JIDUSER1' },
      { jid: 'JIDBUND2', id: 2, slug: 'problem-2', type: 'BUNDLE', authorJid: 'JIDUSER2' },
    ],
    profilesMap = {
      JIDUSER1: { username: 'user1' },
      JIDUSER2: { username: 'user2' },
    },
  } = {}) => {
    nockApi().get('/v2/user-web/config').reply(200, { role });

    nockApi()
      .get('/v4/problems')
      .query(problemsQuery)
      .reply(200, {
        data: { page: problems, totalCount: problems.length },
        profilesMap,
      });

    nockApi()
      .get('/v4/problems/tags')
      .reply(200, {
        data: [
          {
            title: 'Visibility',
            options: [
              { label: 'private', value: 'visibility-private', count: 2 },
              { label: 'public', value: 'visibility-public', count: 0 },
            ],
          },
        ],
        topicTags: [],
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={[initialEntry]}>
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

  test('links each problem to its page', async () => {
    await renderComponent();

    const link = await screen.findByRole('link', { name: 'problem-1' });
    expect(link).toHaveAttribute('href', '/admin/problems/JIDPROG1');
  });

  test('passes the search term and tags to the query', async () => {
    await renderComponent({
      initialEntry: '/admin/problems?term=tree&tags=visibility-private&page=2',
      problemsQuery: { term: 'tree', tags: 'visibility-private', page: '2' },
    });

    expect(await screen.findByText('problem-1')).toBeInTheDocument();
    expect(screen.getByRole('textbox')).toHaveValue('tree');
    expect(await screen.findByRole('checkbox', { name: 'private (2)' })).toBeChecked();
  });

  test('shows the create button to problem admins only', async () => {
    await renderComponent();
    expect(await screen.findByRole('button', { name: /new problem/i })).toBeInTheDocument();
  });

  test('hides the create button from other users', async () => {
    await renderComponent({ role: {} });

    expect(await screen.findByText('problem-1')).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: /new problem/i })).not.toBeInTheDocument();
  });
});
