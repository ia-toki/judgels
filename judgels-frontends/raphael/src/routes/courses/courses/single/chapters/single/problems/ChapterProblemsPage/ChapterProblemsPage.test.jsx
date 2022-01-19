import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { MemoryRouter, Route } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import ChapterProblemsPage from './ChapterProblemsPage';
import webPrefsReducer, { PutStatementLanguage } from '../../../../../../../../modules/webPrefs/webPrefsReducer';
import courseReducer, { PutCourse } from '../../../../../modules/courseReducer';
import courseChapterReducer, { PutCourseChapter } from '../../../modules/courseChapterReducer';
import * as chapterProblemActions from '../modules/chapterProblemActions';

jest.mock('../modules/chapterProblemActions');

describe('ChapterProblemsPage', () => {
  let wrapper;
  let problems;

  const render = async () => {
    chapterProblemActions.getProblems.mockReturnValue(() =>
      Promise.resolve({
        data: problems,
        problemsMap: {
          problemJid1: {
            slug: 'problem-a',
            titlesByLanguage: { en: 'Problem A' },
          },
          problemJid2: {
            slug: 'problem-b',
            titlesByLanguage: { en: 'Problem B' },
          },
        },
        problemProgressesMap: {
          problemJid1: { verdict: 'AC', score: 100 },
        },
      })
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
        <MemoryRouter initialEntries={['/courses/courseSlug/chapter/chapter-1/problems']}>
          <Route path="/courses/courseSlug/chapter/chapter-1/problems" component={ChapterProblemsPage} />
        </MemoryRouter>
      </Provider>
    );

    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();
  };

  describe('when there are no problems', () => {
    beforeEach(async () => {
      problems = [];
      await render();
    });

    it('shows placeholder text and no problems', () => {
      expect(wrapper.text()).toContain('No problems.');
      expect(wrapper.find('div.chapter-problem-card')).toHaveLength(0);
    });
  });

  describe('when there are problems', () => {
    beforeEach(async () => {
      problems = [
        { problemJid: 'problemJid1', alias: 'A' },
        { problemJid: 'problemJid2', alias: 'B' },
      ];
      await render();
    });

    it('shows the problems', () => {
      const cards = wrapper.find('div.chapter-problem-card');
      expect(cards.map(card => [card.text(), card.find('a').props().href])).toEqual([
        ['A. Problem AAC 100', '/courses/courseSlug/chapters/chapter-1/problems/A'],
        ['B. Problem B', '/courses/courseSlug/chapters/chapter-1/problems/B'],
      ]);
    });
  });
});
