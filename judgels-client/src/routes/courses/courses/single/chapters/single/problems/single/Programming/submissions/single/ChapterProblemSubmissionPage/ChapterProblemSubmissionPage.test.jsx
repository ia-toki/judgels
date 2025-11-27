import { act, render, screen } from '@testing-library/react';
import { Provider } from 'react-redux';
import { MemoryRouter, Route } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import { OutputOnlyOverrides } from '../../../../../../../../../../../../modules/api/gabriel/language';
import webPrefsReducer, {
  PutStatementLanguage,
} from '../../../../../../../../../../../../modules/webPrefs/webPrefsReducer';
import courseReducer, { PutCourse } from '../../../../../../../../../modules/courseReducer';
import courseChapterReducer, { PutCourseChapter } from '../../../../../../../modules/courseChapterReducer';
import ChapterProblemSubmissionPage from './ChapterProblemSubmissionPage';

import * as chapterProblemSubmissionActions from '../../modules/chapterProblemSubmissionActions';

jest.mock('../../modules/chapterProblemSubmissionActions');

describe('ChapterProblemSubmissionPage', () => {
  let source = {};

  const renderComponent = async () => {
    chapterProblemSubmissionActions.getSubmissionWithSource.mockReturnValue(() =>
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
    chapterProblemSubmissionActions.getSubmissionSourceImage.mockReturnValue(() => Promise.resolve('image url'));

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
          <MemoryRouter initialEntries={['/courses/courseSlug/chapter/chapter-1/submissions/10']}>
            <Route
              path="/courses/courseSlug/chapter/chapter-1/submissions/:submissionId"
              component={ChapterProblemSubmissionPage}
            />
          </MemoryRouter>
        </Provider>
      )
    );
  };

  beforeEach(async () => {
    await renderComponent();
  });

  test('page', () => {
    expect(screen.getByText('Submission #10')).toBeInTheDocument();
  });
});
