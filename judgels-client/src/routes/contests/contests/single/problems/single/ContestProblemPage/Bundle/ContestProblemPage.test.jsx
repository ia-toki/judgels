import { act, render } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import { ItemType } from '../../../../../../../../modules/api/sandalphon/problemBundle';
import { ContestStyle } from '../../../../../../../../modules/api/uriel/contest';
import { ContestProblemStatus } from '../../../../../../../../modules/api/uriel/contestProblem';
import webPrefsReducer from '../../../../../../../../modules/webPrefs/webPrefsReducer';
import { TestRouter } from '../../../../../../../../test/RouterWrapper';
import contestReducer, { PutContest } from '../../../../../modules/contestReducer';
import ContestProblemPage from './ContestProblemPage';

import * as breadcrumbsActions from '../../../../../../../../modules/breadcrumbs/breadcrumbsActions';
import * as contestSubmissionActions from '../../../../submissions/Bundle/modules/contestSubmissionActions';
import * as contestProblemActions from '../../../modules/contestProblemActions';

vi.mock('../../../../../../../../modules/breadcrumbs/breadcrumbsActions');
vi.mock('../../../modules/contestProblemActions');
vi.mock('../../../../submissions/Bundle/modules/contestSubmissionActions');

describe('BundleContestProblemPage', () => {
  beforeEach(async () => {
    contestProblemActions.getBundleProblemWorksheet.mockReturnValue(() =>
      Promise.resolve({
        defaultLanguage: 'fakelang',
        languages: ['fakelang'],
        problem: {
          problemJid: 'problemJid',
          alias: 'C',
          status: ContestProblemStatus.Open,
          submissionsLimit: 0,
        },
        totalSubmissions: 0,
        worksheet: {
          statement: {
            name: 'Fake Name',
            text: 'Lorem ipsum dos color sit amet',
          },
          items: [
            {
              jid: 'fakeitemjid',
              type: ItemType.MultipleChoice,
              meta: 'somemeta',
              config: {
                statement: 'somestatement',
                choices: [
                  {
                    alias: 'a',
                    content: 'answer a',
                  },
                ],
              },
            },
          ],
        },
      })
    );

    contestSubmissionActions.createItemSubmission.mockReturnValue(() => Promise.resolve({}));
    contestSubmissionActions.getLatestSubmissions.mockReturnValue(() => Promise.resolve({}));
    breadcrumbsActions.pushBreadcrumb.mockReturnValue({ type: 'push' });
    breadcrumbsActions.popBreadcrumb.mockReturnValue({ type: 'pop' });

    const store = createStore(
      combineReducers({
        webPrefs: webPrefsReducer,
        uriel: combineReducers({ contest: contestReducer }),
      }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutContest({ jid: 'contestJid', style: ContestStyle.Bundle }));

    await act(async () =>
      render(
        <Provider store={store}>
          <TestRouter
            initialEntries={['/contests/contestJid/problems/C']}
            path="/contests/$contestSlug/problems/$problemAlias"
          >
            <ContestProblemPage />
          </TestRouter>
        </Provider>
      )
    );
  });

  test('navigation', async () => {
    expect(breadcrumbsActions.pushBreadcrumb).toHaveBeenCalledWith(`/contests/contestJid/problems/C`, 'Problem C');
  });

  test('form', async () => {
    const user = userEvent.setup();

    const input = document.querySelector('.problem-multiple-choice-item-choice input');
    await user.click(input);

    expect(contestSubmissionActions.createItemSubmission).toHaveBeenCalledWith(
      'contestJid',
      'problemJid',
      'fakeitemjid',
      'a'
    );
  });
});
