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
    lessons = [
      { jid: 'JIDLESS1', id: 1, slug: 'lesson-1', authorJid: 'JIDUSER1' },
      { jid: 'JIDLESS2', id: 2, slug: 'lesson-2', authorJid: 'JIDUSER2' },
    ],
    profilesMap = {
      JIDUSER1: { username: 'user1' },
      JIDUSER2: { username: 'user2' },
    },
  } = {}) => {
    nockApi()
      .get('/v4/lessons')
      .query(true)
      .reply(200, {
        data: { page: lessons, totalCount: lessons.length },
        profilesMap,
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/lessons']}>
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
});
