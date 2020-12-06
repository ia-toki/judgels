import { mount } from 'enzyme';
import * as React from 'react';
import { IntlProvider } from 'react-intl';
import { Provider } from 'react-redux';
import { MemoryRouter, Route } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import ChapterSubmissionPage from './ChapterSubmissionPage';
import { OutputOnlyOverrides } from '../../../../../../../../../modules/api/gabriel/language';
import webPrefsReducer, { PutStatementLanguage } from '../../../../../../../../../modules/webPrefs/webPrefsReducer';
import courseReducer, { PutCourse } from '../../../../../../modules/courseReducer';
import courseChapterReducer, { PutCourseChapter } from '../../../../modules/courseChapterReducer';
import * as chapterSubmissionActions from '../../modules/chapterSubmissionActions';

jest.mock('../../modules/chapterSubmissionActions');

describe('ChapterSubmissionPage', () => {
  let wrapper;

  beforeEach(async () => {
    chapterSubmissionActions.getSubmissionWithSource.mockReturnValue(() =>
      Promise.resolve({
        data: {
          submission: {
            id: 10,
            gradingEngine: OutputOnlyOverrides.KEY,
          },
          source: {},
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
        value: { chapterJid: 'chapterJid', alias: 'chapter-1' },
        courseSlug: 'courseSlug',
        name: 'Chapter 1',
      })
    );
    store.dispatch(PutStatementLanguage('en'));

    wrapper = mount(
      <IntlProvider locale={navigator.language}>
        <Provider store={store}>
          <MemoryRouter initialEntries={['/courses/courseSlug/chapter/chapter-1/submissions/10']}>
            <Route
              path="/courses/courseSlug/chapter/chapter-1/submissions/:submissionId"
              component={ChapterSubmissionPage}
            />
          </MemoryRouter>
        </Provider>
      </IntlProvider>
    );

    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();
  });

  test('page', () => {
    expect(wrapper.text()).toContain('Submission #10');
  });
});
