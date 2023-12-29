import { ConnectedRouter, connectRouter, routerMiddleware } from 'connected-react-router';
import { mount } from 'enzyme';
import { createMemoryHistory } from 'history';
import { Provider } from 'react-redux';
import { Route } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import { ItemType } from '../../../../../../../../modules/api/sandalphon/problemBundle';
import { ContestStyle } from '../../../../../../../../modules/api/uriel/contest';
import { ContestProblemStatus } from '../../../../../../../../modules/api/uriel/contestProblem';
import webPrefsReducer from '../../../../../../../../modules/webPrefs/webPrefsReducer';
import contestReducer, { PutContest } from '../../../../../modules/contestReducer';
import ContestProblemPage from './ContestProblemPage';

import * as breadcrumbsActions from '../../../../../../../../modules/breadcrumbs/breadcrumbsActions';
import * as contestSubmissionActions from '../../../../submissions/Bundle/modules/contestSubmissionActions';
import * as contestProblemActions from '../../../modules/contestProblemActions';

jest.mock('../../../../../../../../modules/breadcrumbs/breadcrumbsActions');
jest.mock('../../../modules/contestProblemActions');
jest.mock('../../../../submissions/Bundle/modules/contestSubmissionActions');

describe('BundleContestProblemPage', () => {
  let wrapper;
  let history;

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

    history = createMemoryHistory({ initialEntries: [`/contests/contestJid/problems/C`] });

    const store = createStore(
      combineReducers({
        webPrefs: webPrefsReducer,
        uriel: combineReducers({ contest: contestReducer }),
        router: connectRouter(history),
      }),
      applyMiddleware(thunk, routerMiddleware(history))
    );
    store.dispatch(PutContest({ jid: 'contestJid', style: ContestStyle.Bundle }));

    wrapper = mount(
      <Provider store={store}>
        <ConnectedRouter history={history}>
          <Route path="/contests/:contestSlug/problems/:problemAlias" component={ContestProblemPage} />
        </ConnectedRouter>
      </Provider>
    );

    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();
  });

  test('navigation', async () => {
    expect(breadcrumbsActions.pushBreadcrumb).toHaveBeenCalledWith(`/contests/contestJid/problems/C`, 'Problem C');

    history.push('/contests/xyz/');
    await new Promise(resolve => setImmediate(resolve));
    expect(breadcrumbsActions.popBreadcrumb).toHaveBeenCalledWith(`/contests/contestJid/problems/C`);
  });

  test('form', () => {
    const inp = wrapper.find('.problem-multiple-choice-item-choice input').first();
    inp.simulate('change');
    expect(contestSubmissionActions.createItemSubmission).toHaveBeenCalledWith(
      'contestJid',
      'problemJid',
      'fakeitemjid',
      'a'
    );
  });
});
