import { mount } from 'enzyme';
import { createMemoryHistory } from 'history';
import * as React from 'react';
import { Provider } from 'react-redux';
import { Route } from 'react-router';
import { connectRouter, ConnectedRouter, routerMiddleware } from 'connected-react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import webPrefsReducer from '../../../../../../../../modules/webPrefs/webPrefsReducer';
import { ContestProblemStatus } from '../../../../../../../../modules/api/uriel/contestProblem';
import { contest, contestJid, problemJid, problemAlias } from '../../../../../../../../fixtures/state';
import ContestProblemPage from './ContestProblemPage';
import contestReducer, { PutContest } from '../../../../../modules/contestReducer';
import { ContestStyle } from '../../../../../../../../modules/api/uriel/contest';
import { ItemType } from '../../../../../../../../modules/api/sandalphon/problemBundle';
import * as breadcrumbsActions from '../../../../../../../../modules/breadcrumbs/breadcrumbsActions';
import * as contestProblemActions from '../../../modules/contestProblemActions';
import * as contestSubmissionActions from '../../../../submissions/Bundle/modules/contestSubmissionActions';

jest.mock('../../../../../../../../modules/breadcrumbs/breadcrumbsActions');
jest.mock('../../../modules/contestProblemActions');
jest.mock('../../../../submissions/Bundle/modules/contestSubmissionActions');

describe('BundleContestProblemPage', () => {
  let wrapper;
  let history;

  beforeEach(() => {
    contestProblemActions.getBundleProblemWorksheet.mockReturnValue(() =>
      Promise.resolve({
        defaultLanguage: 'fakelang',
        languages: ['fakelang'],
        problem: {
          problemJid,
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
          reasonNotAllowedToSubmit: 'no reason',
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

    history = createMemoryHistory({ initialEntries: [`/contests/${contestJid}/problems/${problemAlias}`] });

    const store = createStore(
      combineReducers({
        form: formReducer,
        webPrefs: webPrefsReducer,
        uriel: combineReducers({ contest: contestReducer }),
        router: connectRouter(history),
      }),
      applyMiddleware(thunk, routerMiddleware(history))
    );
    store.dispatch(PutContest({ ...contest, style: ContestStyle.Bundle }));

    wrapper = mount(
      <Provider store={store}>
        <ConnectedRouter history={history}>
          <Route path="/contests/:contestSlug/problems/:problemAlias" component={ContestProblemPage} />
        </ConnectedRouter>
      </Provider>
    );
  });

  test('navigation', async () => {
    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();
    expect(breadcrumbsActions.pushBreadcrumb).toHaveBeenCalledWith(
      `/contests/${contestJid}/problems/${problemAlias}`,
      'Problem C'
    );

    history.push('/contests/xyz/');
    await new Promise(resolve => setImmediate(resolve));
    expect(breadcrumbsActions.popBreadcrumb).toHaveBeenCalledWith(`/contests/${contestJid}/problems/${problemAlias}`);
  });

  test('submission form', async () => {
    await new Promise(resolve => setTimeout(resolve, 1000));
    wrapper.update();

    const inp = wrapper.find('.problem-multiple-choice-item-choice input').first();
    inp.simulate('change');
    expect(contestSubmissionActions.createItemSubmission).toHaveBeenCalledWith(
      contestJid,
      problemJid,
      'fakeitemjid',
      'a'
    );
  });
});
