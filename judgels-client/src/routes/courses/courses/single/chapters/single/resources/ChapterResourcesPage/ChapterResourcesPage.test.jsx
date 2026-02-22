import { act, render, screen } from '@testing-library/react';
import nock from 'nock';

import { setSession } from '../../../../../../../../modules/session';
import { WebPrefsProvider } from '../../../../../../../../modules/webPrefs';
import { QueryClientProviderWrapper } from '../../../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../../../test/RouterWrapper';
import { nockJerahmeel } from '../../../../../../../../utils/nock';
import ChapterResourcesPage from './ChapterResourcesPage';

describe('ChapterResourcesPage', () => {
  let lessons;
  let problems;

  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  afterEach(() => {
    nock.cleanAll();
  });

  const renderComponent = async () => {
    nockJerahmeel()
      .get('/courses/slug/courseSlug')
      .reply(200, { jid: 'courseJid', slug: 'courseSlug', name: 'Course' });
    nockJerahmeel().get('/courses/courseJid/chapters/chapter-1').reply(200, { jid: 'chapterJid', name: 'Chapter 1' });

    nockJerahmeel()
      .get('/chapters/chapterJid/lessons')
      .reply(200, {
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
      });

    nockJerahmeel()
      .get('/chapters/chapterJid/problems')
      .reply(200, {
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
      });

    await act(async () =>
      render(
        <WebPrefsProvider initialPrefs={{ statementLanguage: 'en' }}>
          <QueryClientProviderWrapper>
            <TestRouter
              initialEntries={['/courses/courseSlug/chapter/chapter-1']}
              path="/courses/$courseSlug/chapter/$chapterAlias"
            >
              <ChapterResourcesPage />
            </TestRouter>
          </QueryClientProviderWrapper>
        </WebPrefsProvider>
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
        [`A. Problem Asolved\xa0\xa0`, '/courses/courseSlug/chapters/chapter-1/problems/A'],
        ['B. Problem B', '/courses/courseSlug/chapters/chapter-1/problems/B'],
      ]);
    });
  });
});
