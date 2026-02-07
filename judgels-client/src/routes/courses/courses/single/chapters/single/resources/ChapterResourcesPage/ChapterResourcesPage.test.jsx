import { act, render, screen } from '@testing-library/react';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import webPrefsReducer, { PutStatementLanguage } from '../../../../../../../../modules/webPrefs/webPrefsReducer';
import { QueryClientProviderWrapper } from '../../../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../../../test/RouterWrapper';
import { nockJerahmeel } from '../../../../../../../../utils/nock';
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

    nockJerahmeel()
      .get('/courses/slug/courseSlug')
      .reply(200, { jid: 'courseJid', slug: 'courseSlug', name: 'Course' });
    nockJerahmeel().get('/courses/courseJid/chapters/chapter-1').reply(200, { jid: 'chapterJid', name: 'Chapter 1' });

    const store = createStore(
      combineReducers({
        webPrefs: webPrefsReducer,
      }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutStatementLanguage('en'));

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <Provider store={store}>
            <TestRouter
              initialEntries={['/courses/courseSlug/chapter/chapter-1']}
              path="/courses/$courseSlug/chapter/$chapterAlias"
            >
              <ChapterResourcesPage />
            </TestRouter>
          </Provider>
        </QueryClientProviderWrapper>
      )
    );
  };

  describe('when there are no resources', () => {
    beforeEach(async () => {
      lessons = [];
      problems = [];
      await renderComponent();
    });

    it('shows placeholder text and no resources', async () => {
      await screen.findByText(/no resources/i);
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

    it('shows the resources', async () => {
      await screen.findByText('X. Lesson X');
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
