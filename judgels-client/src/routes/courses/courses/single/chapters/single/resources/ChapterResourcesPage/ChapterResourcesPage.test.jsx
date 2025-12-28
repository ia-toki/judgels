import { act, render, screen } from '@testing-library/react';
import { Provider } from 'react-redux';
import { MemoryRouter, Route, Routes } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import webPrefsReducer, { PutStatementLanguage } from '../../../../../../../../modules/webPrefs/webPrefsReducer';
import courseReducer, { PutCourse } from '../../../../../modules/courseReducer';
import courseChapterReducer, { PutCourseChapter } from '../../../modules/courseChapterReducer';
import ChapterResourcesPage from './ChapterResourcesPage';

import * as chapterResourceActions from '../modules/chapterResourceActions';

vi.mock('../modules/chapterResourceActions');

describe('ChapterResourcesPage', () => {
  let lessons;
  let problems;

  const renderComponent = async () => {
    chapterResourceActions.getResources.mockReturnValue(() =>
      Promise.resolve([
        {
          data: lessons,
          lessonsMap: {
            lessonJid1: {
              slug: 'lesson-x',
              titlesByLanguage: { en: 'Lesson X' },
              defaultLanguage: 'en',
            },
            lessonJid2: {
              slug: 'lesson-y',
              titlesByLanguage: { en: 'Lesson Y' },
              defaultLanguage: 'en',
            },
          },
        },
        {
          data: problems,
          problemsMap: {
            problemJid1: {
              slug: 'problem-a',
              titlesByLanguage: { en: 'Problem A' },
              defaultLanguage: 'en',
            },
            problemJid2: {
              slug: 'problem-b',
              titlesByLanguage: { en: 'Problem B' },
              defaultLanguage: 'en',
            },
          },
          problemSetProblemPathsMap: {},
          problemProgressesMap: {
            problemJid1: { verdict: 'AC', score: 100 },
          },
        },
      ])
    );

    const store = createStore(
      combineReducers({
        webPrefs: webPrefsReducer,
        jerahmeel: combineReducers({ course: courseReducer, courseChapter: courseChapterReducer }),
      }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutCourse({ jid: 'courseJid', slug: 'courseSlug' }));
    store.dispatch(
      PutCourseChapter({
        jid: 'chapterJid',
        name: 'Chapter 1',
        alias: 'chapter-1',
        courseSlug: 'courseSlug',
      })
    );
    store.dispatch(PutStatementLanguage('en'));

    await act(async () =>
      render(
        <Provider store={store}>
          <MemoryRouter initialEntries={['/courses/courseSlug/chapter/chapter-1']}>
            <Routes>
              <Route path="/courses/:courseSlug/chapter/:chapterAlias" element={<ChapterResourcesPage />} />
            </Routes>
          </MemoryRouter>
        </Provider>
      )
    );
  };

  describe('when there are no resources', () => {
    beforeEach(async () => {
      lessons = [];
      problems = [];
      await renderComponent();
    });

    it('shows placeholder text and no resources', () => {
      expect(screen.getByText(/no resources/i)).toBeInTheDocument();
      expect(document.querySelectorAll('a.content-card-link')).toHaveLength(0);
    });
  });

  describe('when there are resources', () => {
    beforeEach(async () => {
      lessons = [
        { lessonJid: 'lessonJid1', alias: 'X' },
        { lessonJid: 'lessonJid2', alias: 'Y' },
      ];
      problems = [
        { problemJid: 'problemJid1', alias: 'A' },
        { problemJid: 'problemJid2', alias: 'B' },
      ];
      await renderComponent();
    });

    it('shows the resources', () => {
      const cards = document.querySelectorAll('a.content-card-link');
      expect([...cards].map(card => [card.textContent, card.pathname])).toEqual([
        ['X. Lesson X', '/courses/courseSlug/chapters/chapter-1/lessons/X'],
        ['Y. Lesson Y', '/courses/courseSlug/chapters/chapter-1/lessons/Y'],
        ['A. Problem Asolved  ', '/courses/courseSlug/chapters/chapter-1/problems/A'],
        ['B. Problem B', '/courses/courseSlug/chapters/chapter-1/problems/B'],
      ]);
    });
  });
});
