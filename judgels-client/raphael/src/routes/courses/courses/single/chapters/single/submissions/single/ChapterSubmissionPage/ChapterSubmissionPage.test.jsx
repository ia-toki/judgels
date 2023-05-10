import { mount } from 'enzyme';
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
  let source = {};

  const render = async () => {
    chapterSubmissionActions.getSubmissionWithSource.mockReturnValue(() =>
      Promise.resolve({
        data: {
          submission: {
            id: 10,
            jid: 'submissionJid',
            gradingEngine: OutputOnlyOverrides.KEY,
          },
          source,
        },
      })
    );
    chapterSubmissionActions.getSubmissionSourceImage.mockReturnValue(() => Promise.resolve('image url'));

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
        <MemoryRouter initialEntries={['/courses/courseSlug/chapter/chapter-1/submissions/10']}>
          <Route
            path="/courses/courseSlug/chapter/chapter-1/submissions/:submissionId"
            component={ChapterSubmissionPage}
          />
        </MemoryRouter>
      </Provider>
    );

    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();
  };

  beforeEach(async () => {
    await render();
  });

  test('page', () => {
    expect(wrapper.text()).toContain('Submission #10');
  });

  describe('when there is no source', () => {
    beforeEach(async () => {
      source = null;
      await render();
    });

    test('get source image url', () => {
      expect(chapterSubmissionActions.getSubmissionSourceImage).toHaveBeenCalledWith('submissionJid');
    });
  });
});
