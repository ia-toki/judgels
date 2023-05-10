import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { MemoryRouter, Route } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import ChapterLessonsPage from './ChapterLessonsPage';
import webPrefsReducer, { PutStatementLanguage } from '../../../../../../../../modules/webPrefs/webPrefsReducer';
import courseReducer, { PutCourse } from '../../../../../modules/courseReducer';
import courseChapterReducer, { PutCourseChapter } from '../../../modules/courseChapterReducer';
import * as chapterLessonActions from '../modules/chapterLessonActions';

jest.mock('../modules/chapterLessonActions');

describe('ChapterLessonsPage', () => {
  let wrapper;
  let lessons;
  let store;

  const render = async () => {
    chapterLessonActions.getLessons.mockReturnValue(() =>
      Promise.resolve({
        data: lessons,
        lessonsMap: {
          lessonJid1: {
            slug: 'lesson-a',
            titlesByLanguage: { en: 'Lesson A' },
          },
          lessonJid2: {
            slug: 'lesson-b',
            titlesByLanguage: { en: 'Lesson B' },
          },
        },
      })
    );
    chapterLessonActions.redirectToLesson.mockReturnValue(() => Promise.resolve());

    store = createStore(
      combineReducers({
        webPrefs: webPrefsReducer,
        jerahmeel: combineReducers({ course: courseReducer, courseChapter: courseChapterReducer }),
      }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutCourse({ jid: 'courseJid', slug: 'courseSlug' }));
    store.dispatch(
      PutCourseChapter({
        jd: 'chapterJid',
        name: 'Chapter 1',
        lessonAliases: lessons.map(lesson => lesson.alias),
        alias: 'chapter-1',
        courseSlug: 'courseSlug',
      })
    );
    store.dispatch(PutStatementLanguage('en'));

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter initialEntries={['/courses/courseSlug/chapter/chapter-1/lessons']}>
          <Route path="/courses/courseSlug/chapter/chapter-1/lessons" component={ChapterLessonsPage} />
        </MemoryRouter>
      </Provider>
    );

    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();
  };

  describe('when there are no lessons', () => {
    beforeEach(async () => {
      lessons = [];
      await render();
    });

    it('shows placeholder text and no lessons', () => {
      expect(wrapper.text()).toContain('No lessons.');
      expect(wrapper.find('div.chapter-lesson-card')).toHaveLength(0);
    });
  });

  describe('when there is one lesson', () => {
    beforeEach(async () => {
      lessons = [{ lessonJid: 'lessonJid1', alias: 'A' }];
      await render();
    });

    it('redirects to the only lesson', async () => {
      await new Promise(resolve => setImmediate(resolve));
      wrapper.update();

      expect(chapterLessonActions.redirectToLesson).toHaveBeenCalledWith(
        '/courses/courseSlug/chapter/chapter-1/lessons',
        'A'
      );
    });
  });

  describe('when there are lessons', () => {
    beforeEach(async () => {
      lessons = [
        { lessonJid: 'lessonJid1', alias: 'A' },
        { lessonJid: 'lessonJid2', alias: 'B' },
      ];
      await render();
    });

    it('shows the lessons', () => {
      const cards = wrapper.find('div.chapter-lesson-card');
      expect(cards.map(card => [card.text(), card.find('a').props().href])).toEqual([
        ['A. Lesson A', '/courses/courseSlug/chapters/chapter-1/lessons/A'],
        ['B. Lesson B', '/courses/courseSlug/chapters/chapter-1/lessons/B'],
      ]);
    });
  });
});
