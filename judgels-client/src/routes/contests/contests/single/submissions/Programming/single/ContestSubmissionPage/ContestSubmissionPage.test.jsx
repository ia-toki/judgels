import { act, render, screen } from '@testing-library/react';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import { OutputOnlyOverrides } from '../../../../../../../../modules/api/gabriel/language';
import webPrefsReducer, { PutStatementLanguage } from '../../../../../../../../modules/webPrefs/webPrefsReducer';
import { TestRouter } from '../../../../../../../../test/RouterWrapper';
import contestReducer, { PutContest } from '../../../../../modules/contestReducer';
import ContestSubmissionPage from './ContestSubmissionPage';

import * as contestSubmissionActions from '../../modules/contestSubmissionActions';

vi.mock('../../modules/contestSubmissionActions');

describe('ContestSubmissionPage', () => {
  beforeEach(async () => {
    contestSubmissionActions.getSubmissionWithSource.mockReturnValue(() =>
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
        uriel: combineReducers({ contest: contestReducer }),
      }),
      applyMiddleware(thunk)
    );
    store.dispatch(
      PutContest({
        jid: 'contestJid',
      })
    );
    store.dispatch(PutStatementLanguage('en'));

    await act(async () =>
      render(
        <Provider store={store}>
          <TestRouter
            initialEntries={['/contests/contestSlug/submissions/10']}
            path="/contests/$contestSlug/submissions/$submissionId"
          >
            <ContestSubmissionPage />
          </TestRouter>
        </Provider>
      )
    );
  });

  test('page', () => {
    expect(screen.getByText(/Submission #10/)).toBeInTheDocument();
  });
});
