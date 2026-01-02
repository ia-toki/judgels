import { act, render, screen } from '@testing-library/react';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import { OutputOnlyOverrides } from '../../../../../../../../../../../../modules/api/gabriel/language';
import webPrefsReducer, {
  PutStatementLanguage,
} from '../../../../../../../../../../../../modules/webPrefs/webPrefsReducer';
import { TestRouter } from '../../../../../../../../../../../../test/RouterWrapper';
import courseReducer, { PutCourse } from '../../../../../../../../../modules/courseReducer';
import courseChapterReducer, { PutCourseChapter } from '../../../../../../../modules/courseChapterReducer';
import ChapterProblemSubmissionPage from './ChapterProblemSubmissionPage';

import * as chapterProblemSubmissionActions from '../../modules/chapterProblemSubmissionActions';

vi.mock('../../modules/chapterProblemSubmissionActions');

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
          <TestRouter
            initialEntries={['/courses/courseSlug/chapters/chapter-1/problems/A/submissions/10']}
            path="/courses/$courseSlug/chapters/$chapterAlias/problems/$problemAlias/submissions/$submissionId"
          >
            <ChapterProblemSubmissionPage />
          </TestRouter>
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
