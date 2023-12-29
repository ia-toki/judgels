import { ConnectedRouter, connectRouter, routerMiddleware } from 'connected-react-router';
import { mount } from 'enzyme';
import { createMemoryHistory } from 'history';
import { act } from 'preact/test-utils';
import { Provider } from 'react-redux';
import { Route } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import { ContestProblemStatus } from '../../../../../../../../modules/api/uriel/contestProblem';
import webPrefsReducer from '../../../../../../../../modules/webPrefs/webPrefsReducer';
import contestReducer, { PutContest } from '../../../../../modules/contestReducer';
import ContestProblemPage from './ContestProblemPage';

import * as breadcrumbsActions from '../../../../../../../../modules/breadcrumbs/breadcrumbsActions';
import * as webPrefsActions from '../../../../../../../../modules/webPrefs/webPrefsActions';
import * as contestSubmissionActions from '../../../../submissions/Programming/modules/contestSubmissionActions';
import * as contestProblemActions from '../../../modules/contestProblemActions';

jest.mock('../../../modules/contestProblemActions');
jest.mock('../../../../submissions/Programming/modules/contestSubmissionActions');
jest.mock('../../../../../../../../modules/webPrefs/webPrefsActions');
jest.mock('../../../../../../../../modules/breadcrumbs/breadcrumbsActions');

describe('ProgrammingContestProblemPage', () => {
  let wrapper;
  let history;

  beforeEach(async () => {
    contestProblemActions.getProgrammingProblemWorksheet.mockReturnValue(() =>
      Promise.resolve({
        problem: {
          problemJid: 'problemJid',
          alias: 'C',
          status: ContestProblemStatus.Open,
          submissionsLimit: 0,
        },
        totalSubmissions: 2,
        worksheet: {
          statement: {
            name: 'Problem',
            text: 'Lorem ipsum',
          },
          limits: {
            timeLimit: 2000,
            memoryLimit: 65536,
          },
          submissionConfig: {
            sourceKeys: { encoder: 'Encoder', decoder: 'Decoder' },
            gradingEngine: 'Batch',
            gradingLanguageRestriction: { allowedLanguageNames: ['Cpp11', 'Pascal'] },
          },
        },
      })
    );

    webPrefsActions.updateGradingLanguage.mockReturnValue(() => Promise.resolve({}));
    contestSubmissionActions.createSubmission.mockReturnValue(() => Promise.resolve({}));
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
    store.dispatch(PutContest({ jid: 'contestJid', slug: 'contest-a' }));

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

    history.push('/contests/ioi/');
    await new Promise(resolve => setImmediate(resolve));
    expect(breadcrumbsActions.popBreadcrumb).toHaveBeenCalledWith(`/contests/contestJid/problems/C`);
  });

  test('form', async () => {
    act(() => {
      const encoder = wrapper.find('input[name="sourceFiles.encoder"]');
      encoder.prop('onChange')({ target: { files: [{ name: 'encoder.cpp', size: 1000 }] }, preventDefault: () => {} });

      const decoder = wrapper.find('input[name="sourceFiles.decoder"]');
      decoder.prop('onChange')({ target: { files: [{ name: 'decoder.cpp', size: 2000 }] }, preventDefault: () => {} });
    });

    // TODO(fushar): make this work
    // See https://github.com/FezVrasta/popper.js/issues/478

    // const gradingLanguageButton = wrapper.find('button[data-key="gradingLanguage"]');
    // gradingLanguageButton.simulate('click');

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(webPrefsActions.updateGradingLanguage).toHaveBeenCalledWith('Cpp11');
    expect(contestSubmissionActions.createSubmission).toHaveBeenCalledWith('contestJid', 'contest-a', 'problemJid', {
      gradingLanguage: 'Cpp11',
      sourceFiles: {
        encoder: { name: 'encoder.cpp', size: 1000 },
        decoder: { name: 'decoder.cpp', size: 2000 },
      },
    });
  });
});
