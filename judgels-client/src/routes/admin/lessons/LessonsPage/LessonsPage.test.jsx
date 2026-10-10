import { act, render, screen, waitFor, within } from '@testing-library/react';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import LessonsPage from './LessonsPage';

describe('LessonsPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({
    role = { training: 'ADMIN' },
    initialEntry = '/admin/lessons',
    lessonsQuery = true,
    lessons = [
      { jid: 'JIDLESS1', id: 1, slug: 'lesson-1', authorJid: 'JIDUSER1' },
      { jid: 'JIDLESS2', id: 2, slug: 'lesson-2', authorJid: 'JIDUSER2' },
    ],
    profilesMap = {
      JIDUSER1: { username: 'user1' },
      JIDUSER2: { username: 'user2' },
    },
  } = {}) => {
    nockApi().get('/v2/user-web/config').reply(200, { role });

    nockApi()
      .get('/v4/lessons')
      .query(lessonsQuery)
      .reply(200, {
        data: { page: lessons, totalCount: lessons.length },
        profilesMap,
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={[initialEntry]}>
            <LessonsPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('renders placeholder when there are no lessons', async () => {
    await renderComponent({ lessons: [], profilesMap: {} });
    expect(await screen.findByText(/no lessons/i)).toBeInTheDocument();
  });

  test('renders the lessons table', async () => {
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
    ).toEqual([[], ['1', 'lesson-1', 'user1'], ['2', 'lesson-2', 'user2']]);
  });

  test('links each lesson to its page', async () => {
    await renderComponent();

    const link = await screen.findByRole('link', { name: 'lesson-1' });
    expect(link).toHaveAttribute('href', '/admin/lessons/JIDLESS1');
  });

  test('passes the search term to the query', async () => {
    await renderComponent({
      initialEntry: '/admin/lessons?term=tree&page=2',
      lessonsQuery: { term: 'tree', page: '2' },
    });

    expect(await screen.findByText('lesson-1')).toBeInTheDocument();
    expect(screen.getByRole('textbox')).toHaveValue('tree');
  });

  test('shows the create button to training admins only', async () => {
    await renderComponent();
    expect(await screen.findByRole('button', { name: /new lesson/i })).toBeInTheDocument();
  });

  test('hides the create button from other users', async () => {
    await renderComponent({ role: {} });

    expect(await screen.findByText('lesson-1')).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: /new lesson/i })).not.toBeInTheDocument();
  });
});
