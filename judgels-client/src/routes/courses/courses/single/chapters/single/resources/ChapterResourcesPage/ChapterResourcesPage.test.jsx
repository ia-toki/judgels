import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { MemoryRouter, Route } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import ChapterResourcesPage from './ChapterResourcesPage';
import webPrefsReducer, { PutStatementLanguage } from '../../../../../../../../modules/webPrefs/webPrefsReducer';
import courseReducer, { PutCourse } from '../../../../../modules/courseReducer';
import courseChapterReducer, { PutCourseChapter } from '../../../modules/courseChapterReducer';
import * as chapterResourceActions from '../modules/chapterResourceActions';

jest.mock('../modules/chapterResourceActions');

describe('ChapterResourcesPage', () => {
  let wrapper;
  let lessons;
  let problems;

  const render = async () => {
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

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter initialEntries={['/courses/courseSlug/chapter/chapter-1']}>
          <Route path="/courses/courseSlug/chapter/chapter-1" component={ChapterResourcesPage} />
        </MemoryRouter>
      </Provider>
    );

    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();
  };

  describe('when there are no resources', () => {
    beforeEach(async () => {
      lessons = [];
      problems = [];
      await render();
    });

    it('shows placeholder text and no resources', () => {
      expect(wrapper.text()).toContain('No resources.');
      expect(wrapper.find('a.content-card-link')).toHaveLength(0);
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
      await render();
    });

    it('shows the resources', () => {
      const cards = wrapper.find('a.content-card-link');
      expect(cards.map(card => [card.text(), card.find('a').props().href])).toEqual([
        ['bookX. Lesson X', '/courses/courseSlug/chapters/chapter-1/lessons/X'],
        ['bookY. Lesson Y', '/courses/courseSlug/chapters/chapter-1/lessons/Y'],
        ['panel-tableA. Problem AAC 100', '/courses/courseSlug/chapters/chapter-1/problems/A'],
        ['panel-tableB. Problem B', '/courses/courseSlug/chapters/chapter-1/problems/B'],
      ]);
    });
  });
});
