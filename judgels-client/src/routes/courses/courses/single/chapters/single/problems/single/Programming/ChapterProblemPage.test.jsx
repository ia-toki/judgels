import { render, screen } from '@testing-library/react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import webPrefsReducer, { PutStatementLanguage } from '../../../../../../../../../modules/webPrefs/webPrefsReducer';
import courseReducer, { PutCourse } from '../../../../../../modules/courseReducer';
import courseChapterReducer, { PutCourseChapter } from '../../../../modules/courseChapterReducer';
import chapterProblemReducer from '../modules/chapterProblemReducer';
import ChapterProblemPage from './ChapterProblemPage';

describe('ChapterProblemProgrammingPage', () => {
  beforeEach(async () => {
    const worksheet = {
      problem: {
        problemJid: 'problemJid1',
        alias: 'A',
        type: 'PROGRAMMING',
      },
      defaultLanguage: 'id',
      languages: ['en', 'id'],
      skeletons: [],
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
    const renderNavigation = () => null;

    const store = createStore(
      combineReducers({
        webPrefs: webPrefsReducer,
        jerahmeel: combineReducers({
          course: courseReducer,
          courseChapter: courseChapterReducer,
          chapterProblem: chapterProblemReducer,
        }),
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

    render(
      <Provider store={store}>
        <MemoryRouter>
          <ChapterProblemPage worksheet={worksheet} renderNavigation={renderNavigation} />
        </MemoryRouter>
      </Provider>
    );
  });

  test('page', () => {
    expect(screen.getByText('This is problem description')).toBeInTheDocument();
  });
});
