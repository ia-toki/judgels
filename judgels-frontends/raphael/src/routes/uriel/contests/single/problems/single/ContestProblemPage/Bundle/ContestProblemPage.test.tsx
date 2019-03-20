import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { Route } from 'react-router';
import { ConnectedRouter } from 'react-router-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import { webPrefsReducer } from 'modules/webPrefs/webPrefsReducer';
import { ContestProblemStatus } from 'modules/api/uriel/contestProblem';
import { contest, contestJid, problemJid, problemAlias } from 'fixtures/state';

import { createContestProblemPage } from './ContestProblemPage';
import { contestReducer, PutContest } from '../../../../../modules/contestReducer';
import createMemoryHistory from 'history/createMemoryHistory';
import { MemoryHistory } from 'history';
import { ContestStyle } from 'modules/api/uriel/contest';
import { ItemType } from 'modules/api/sandalphon/problemBundle';

describe('BundleContestProblemPage', () => {
  let contestProblemActions: jest.Mocked<any>;
  let contestSubmissionActions: jest.Mocked<any>;
  let breadcrumbsActions: jest.Mocked<any>;
  let wrapper: ReactWrapper<any, any>;
  let history: MemoryHistory;

  beforeEach(() => {
    contestProblemActions = {
      getBundleProblemWorksheet: jest.fn().mockReturnValue(() =>
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
      ),
    };

    contestSubmissionActions = {
      createItemSubmission: jest.fn(),
      getLatestSubmissions: jest.fn().mockReturnValue(() => Promise.resolve({})),
    };

    breadcrumbsActions = {
      pushBreadcrumb: jest.fn().mockReturnValue({ type: 'push' }),
      popBreadcrumb: jest.fn().mockReturnValue({ type: 'pop' }),
    };

    const store = createStore(
      combineReducers({
        form: formReducer,
        webPrefs: webPrefsReducer,
        uriel: combineReducers({ contest: contestReducer }),
      }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutContest.create({ ...contest, style: ContestStyle.Bundle }));

    const ContestProblemPage = createContestProblemPage(
      contestProblemActions,
      contestSubmissionActions,
      breadcrumbsActions
    );

    history = createMemoryHistory({ initialEntries: [`/contests/${contestJid}/problems/${problemAlias}`] });
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
