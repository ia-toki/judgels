import { act, render, screen } from '@testing-library/react';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import { OutputOnlyOverrides } from '../../../../../../../../../../../../modules/api/gabriel/language';
import webPrefsReducer, {
  PutStatementLanguage,
} from '../../../../../../../../../../../../modules/webPrefs/webPrefsReducer';
import { QueryClientProviderWrapper } from '../../../../../../../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../../../../../../../test/RouterWrapper';
import { nockJerahmeel } from '../../../../../../../../../../../../utils/nock';
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

    nockJerahmeel().get('/courses/slug/courseSlug').reply(200, { jid: 'courseJid', slug: 'courseSlug' });

    const store = createStore(
      combineReducers({
        webPrefs: webPrefsReducer,
        jerahmeel: combineReducers({ courseChapter: courseChapterReducer }),
      }),
      applyMiddleware(thunk)
    );
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
        <QueryClientProviderWrapper>
          <Provider store={store}>
            <TestRouter
              initialEntries={['/courses/courseSlug/chapters/chapter-1/problems/A/submissions/10']}
              path="/courses/$courseSlug/chapters/$chapterAlias/problems/$problemAlias/submissions/$submissionId"
            >
              <ChapterProblemSubmissionPage />
            </TestRouter>
          </Provider>
        </QueryClientProviderWrapper>
      )
    );
  };

  beforeEach(async () => {
    await renderComponent();
  });

  test('page', async () => {
    await screen.findByText('Submission #10');
  });
});
