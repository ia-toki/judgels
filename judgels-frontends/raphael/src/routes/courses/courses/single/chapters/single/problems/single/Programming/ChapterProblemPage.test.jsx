import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { MemoryRouter, Route } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import ChapterProblemPage from './ChapterProblemPage';
import webPrefsReducer, { PutStatementLanguage } from '../../../../../../../../../modules/webPrefs/webPrefsReducer';
import courseReducer, { PutCourse } from '../../../../../../modules/courseReducer';
import courseChapterReducer, { PutCourseChapter } from '../../../../modules/courseChapterReducer';

describe('ChapterProblemProgrammingPage', () => {
  let wrapper;

  beforeEach(async () => {
    const worksheet = {
      problem: {
        problemJid: 'problemJid1',
        alias: 'A',
        type: 'PROGRAMMING',
      },
      defaultLanguage: 'id',
      languages: ['en', 'id'],
      worksheet: {
        statement: {
          title: 'Problem',
          text: 'This is problem description',
        },
        limits: {},
        submissionConfig: {
          sourceKeys: ['source'],
          gradingEngine: 'Batch',
          gradingLanguageRestriction: {
            allowedLanguageNames: [],
            isAllowedAll: true,
          },
        },
      },
    };

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
        <MemoryRouter>
          <ChapterProblemPage worksheet={worksheet} />
        </MemoryRouter>
      </Provider>
    );

    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();
  });

  test('page', () => {
    expect(wrapper.text()).toContain('A. Problem');
  });
});
