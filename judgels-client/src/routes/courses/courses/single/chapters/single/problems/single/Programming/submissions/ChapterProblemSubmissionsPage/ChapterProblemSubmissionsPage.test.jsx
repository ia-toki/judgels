import { act, render, screen, within } from '@testing-library/react';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import sessionReducer, { PutUser } from '../../../../../../../../../../../modules/session/sessionReducer';
import { TestRouter } from '../../../../../../../../../../../test/RouterWrapper';
import courseReducer, { PutCourse } from '../../../../../../../../modules/courseReducer';
import courseChapterReducer, { PutCourseChapter } from '../../../../../../modules/courseChapterReducer';
import { ChapterProblemContext } from '../../../ChapterProblemContext';
import ChapterProblemSubmissionsPage from './ChapterProblemSubmissionsPage';

import * as chapterProblemSubmissionActions from '../modules/chapterProblemSubmissionActions';

vi.mock('../modules/chapterProblemSubmissionActions');

describe('ChapterProblemSubmissionsPage', () => {
  let submissions;
  let canManage;

  const renderComponent = async () => {
    chapterProblemSubmissionActions.getSubmissions.mockReturnValue(() =>
      Promise.resolve({
        data: {
          page: submissions,
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
      })
    );

    chapterProblemSubmissionActions.getSubmissionSourceImage.mockReturnValue(() => Promise.resolve('image.url'));

    const store = createStore(
      combineReducers({
        session: sessionReducer,
        jerahmeel: combineReducers({ course: courseReducer, courseChapter: courseChapterReducer }),
      }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutUser({ jid: 'userJid1', username: 'username' }));
    store.dispatch(PutCourse({ jid: 'courseJid', slug: 'courseSlug' }));
    store.dispatch(
      PutCourseChapter({
        jid: 'chapterJid',
        name: 'Chapter 1',
        alias: 'chapter-1',
        courseSlug: 'courseSlug',
      })
    );

    await act(async () =>
      render(
        <Provider store={store}>
          <TestRouter
            initialEntries={['/courses/courseSlug/chapter/chapter-1/problems/A/submissions']}
            path="/courses/$courseSlug/chapter/$chapterAlias/problems/$problemAlias/submissions"
          >
            <ChapterProblemContext.Provider value={{ worksheet: null, renderNavigation: () => null }}>
              <ChapterProblemSubmissionsPage />
            </ChapterProblemContext.Provider>
          </TestRouter>
        </Provider>
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

      it('shows no buttons', () => {
        expect(document.querySelectorAll('.action-buttons button')).toHaveLength(0);
      });
    });

    describe('when canManage', () => {
      beforeEach(async () => {
        canManage = true;
        await renderComponent();
      });

      it('shows action buttons', () => {
        const buttons = document.querySelectorAll('.action-buttons button');
        expect([...buttons].map(button => button.textContent)).toEqual(['Regrade all pages']);
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

      it('shows placeholder text and no submissions', () => {
        expect(screen.getByText(/no submissions/i)).toBeInTheDocument();
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

        it('shows the submissions', () => {
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

        it('shows the submissions', () => {
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
