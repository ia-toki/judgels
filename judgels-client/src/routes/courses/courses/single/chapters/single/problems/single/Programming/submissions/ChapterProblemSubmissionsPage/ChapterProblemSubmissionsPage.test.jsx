import { act, render, screen, waitFor, within } from '@testing-library/react';
import nock from 'nock';

import { setSession } from '../../../../../../../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../../../../../../test/RouterWrapper';
import { nockJerahmeel } from '../../../../../../../../../../../utils/nock';
import { ChapterProblemContext } from '../../../ChapterProblemContext';
import ChapterProblemSubmissionsPage from './ChapterProblemSubmissionsPage';

describe('ChapterProblemSubmissionsPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid', username: 'username' });
  });

  afterEach(() => {
    nock.cleanAll();
  });

  let submissions;
  let canManage;

  const renderComponent = async () => {
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

  describe('action buttons', () => {
    beforeEach(() => {
      submissions = [];
    });

    describe('when not canManage', () => {
      beforeEach(async () => {
        canManage = false;
        await renderComponent();
      });

      it('shows no buttons', async () => {
        await screen.findByText(/no submissions/i);
        expect(document.querySelectorAll('.action-buttons button')).toHaveLength(0);
      });
    });

    describe('when canManage', () => {
      beforeEach(async () => {
        canManage = true;
        await renderComponent();
      });

      it('shows action buttons', async () => {
        expect(await screen.findByRole('button', { name: /regrade all pages/i })).toBeInTheDocument();
      });
    });
  });

  describe('content', () => {
    describe('when there are no submissions', () => {
      beforeEach(async () => {
        submissions = [];
        canManage = false;
        await renderComponent();
      });

      it('shows placeholder text and no submissions', async () => {
        expect(await screen.findByText(/no submissions/i)).toBeInTheDocument();
        expect(screen.queryByRole('row')).not.toBeInTheDocument();
      });
    });

    describe('when there are submissions', () => {
      beforeEach(() => {
        submissions = [
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
      });

      describe('when not canManage', () => {
        beforeEach(async () => {
          canManage = false;
          await renderComponent();
        });

        it('shows the submissions', async () => {
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
      });

      describe('when canManage', () => {
        beforeEach(async () => {
          canManage = true;
          await renderComponent();
        });

        it('shows the submissions', async () => {
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
    });
  });
});
