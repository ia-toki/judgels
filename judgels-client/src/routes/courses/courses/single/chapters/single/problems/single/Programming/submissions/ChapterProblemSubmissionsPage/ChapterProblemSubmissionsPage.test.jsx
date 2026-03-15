import { act, render, screen, waitFor, within } from '@testing-library/react';

import { setSession } from '../../../../../../../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../../../../../../test/RouterWrapper';
import { nockJerahmeel } from '../../../../../../../../../../../utils/nock';
import { ChapterProblemContext } from '../../../ChapterProblemContext';
import ChapterProblemSubmissionsPage from './ChapterProblemSubmissionsPage';

describe('ChapterProblemSubmissionsPage', () => {
  const mockSubmissions = [
    {
      id: 20,
      jid: 'submissionJid1',
      containerJid: 'chapterJid',
      userJid: 'userJid1',
      problemJid: 'problemJid1',
      gradingLanguage: 'Cpp17',
      time: new Date(new Date().setDate(new Date().getDate() - 1)).getTime(),
      latestGrading: {
        verdict: { code: 'AC' },
        score: 100,
      },
    },
    {
      id: 10,
      jid: 'submissionJid2',
      containerJid: 'chapterJid',
      userJid: 'userJid2',
      problemJid: 'problemJid1',
      gradingLanguage: 'Cpp17',
      time: new Date(new Date().setDate(new Date().getDate() - 2)).getTime(),
    },
  ];

  beforeEach(() => {
    setSession('token', { jid: 'userJid', username: 'username' });
  });

  const renderComponent = async ({ submissions = mockSubmissions, canManage = false } = {}) => {
    nockJerahmeel().get('/courses/slug/courseSlug').reply(200, { jid: 'courseJid', slug: 'courseSlug' });
    nockJerahmeel().get('/courses/courseJid/chapters/chapter-1').reply(200, { jid: 'chapterJid', name: 'Chapter 1' });

    nockJerahmeel()
      .get('/submissions/programming')
      .query(true)
      .reply(200, {
        data: {
          page: submissions,
          totalCount: submissions.length,
        },
        profilesMap: {
          userJid1: { username: 'username1' },
          userJid2: { username: 'username2' },
        },
        problemAliasesMap: {
          'chapterJid-problemJid1': 'A',
        },
        config: {
          canManage,
          userJids: ['userJid1', 'userJid2'],
          problemJids: ['problemJid1'],
        },
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter
            initialEntries={['/courses/courseSlug/chapter/chapter-1/problems/A/submissions']}
            path="/courses/$courseSlug/chapter/$chapterAlias/problems/$problemAlias/submissions"
          >
            <ChapterProblemContext.Provider value={{ worksheet: null, renderNavigation: () => null }}>
              <ChapterProblemSubmissionsPage />
            </ChapterProblemContext.Provider>
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('when not canManage, renders no buttons', async () => {
    await renderComponent({ submissions: [] });
    await screen.findByText(/no submissions/i);
    expect(document.querySelectorAll('.action-buttons button')).toHaveLength(0);
  });

  test('when canManage, renders action buttons', async () => {
    await renderComponent({ submissions: [], canManage: true });
    expect(await screen.findByRole('button', { name: /regrade all pages/i })).toBeInTheDocument();
  });

  test('renders placeholder when there are no submissions', async () => {
    await renderComponent({ submissions: [] });
    expect(await screen.findByText(/no submissions/i)).toBeInTheDocument();
    expect(screen.queryByRole('row')).not.toBeInTheDocument();
  });

  test('when not canManage, renders the submissions', async () => {
    await renderComponent();

    await waitFor(() => {
      expect(screen.getAllByRole('row').length).toBeGreaterThan(1);
    });

    const rows = screen.getAllByRole('row').slice(1);
    expect(rows).toHaveLength(2);

    expect(
      within(rows[0])
        .getAllByRole('cell')
        .map(td => td.textContent.trim())
    ).toEqual(['20', 'username1', 'C++17', 'Accepted', '1 day ago', 'search']);
    expect(
      within(rows[1])
        .getAllByRole('cell')
        .map(td => td.textContent.trim())
    ).toEqual(['10', 'username2', 'C++17', '', '2 days ago', 'search']);
  });

  test('when canManage, renders the submissions', async () => {
    await renderComponent({ canManage: true });

    await waitFor(() => {
      expect(screen.getAllByRole('row').length).toBeGreaterThan(1);
    });

    const rows = screen.getAllByRole('row').slice(1);
    expect(
      rows.map(row =>
        within(row)
          .getAllByRole('cell')
          .map(cell => cell.textContent.replace(/\s+/g, ' ').trim())
      )
    ).toEqual([
      ['20 refresh', 'username1', 'C++17', 'Accepted', '1 day ago', 'search'],
      ['10 refresh', 'username2', 'C++17', '', '2 days ago', 'search'],
    ]);
  });
});
