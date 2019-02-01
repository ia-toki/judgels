import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { Route } from 'react-router';
import { ConnectedRouter } from 'react-router-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import { webPrefsReducer } from 'modules/webPrefs/webPrefsReducer';
import { preferredGradingLanguage } from 'modules/api/gabriel/language';
import { ContestProblemStatus } from 'modules/api/uriel/contestProblem';
import { contest, contestJid, problemJid } from 'fixtures/state';

import { createContestProblemPage } from './ContestProblemPage';
import { contestReducer, PutContest } from '../../../../modules/contestReducer';
import createMemoryHistory from 'history/createMemoryHistory';
import { MemoryHistory } from 'history';

describe('ContestProblemPage', () => {
  let contestProblemActions: jest.Mocked<any>;
  let contestSubmissionActions: jest.Mocked<any>;
  let breadcrumbsActions: jest.Mocked<any>;
  let wrapper: ReactWrapper<any, any>;
  let history: MemoryHistory;

  beforeEach(() => {
    contestProblemActions = {
      getProblemWorksheet: jest.fn().mockReturnValue(() =>
        Promise.resolve({
          problem: {
            problemJid,
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
              gradingLanguageRestriction: { allowedLanguageNames: [] },
            },
          },
        })
      ),
    };
    contestSubmissionActions = {
      createSubmission: jest.fn(),
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
    store.dispatch(PutContest.create(contest));

    const ContestProblemPage = createContestProblemPage(
      contestProblemActions,
      contestSubmissionActions,
      breadcrumbsActions
    );

    history = createMemoryHistory({ initialEntries: [`/contests/${contestJid}/problems/C`] });
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
    expect(breadcrumbsActions.pushBreadcrumb).toHaveBeenCalledWith(`/contests/${contestJid}/problems/C`, 'Problem C');

    history.push('/contests/ioi/');
    await new Promise(resolve => setImmediate(resolve));
    expect(breadcrumbsActions.popBreadcrumb).toHaveBeenCalledWith(`/contests/${contestJid}/problems/C`);
  });

  test('submission form', async () => {
    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();

    const encoder = wrapper.find('input[name="sourceFiles.encoder"]');
    encoder.simulate('change', { target: { files: [{ name: 'encoder.cpp', size: 1000 }] } });

    const decoder = wrapper.find('input[name="sourceFiles.decoder"]');
    decoder.simulate('change', { target: { files: [{ name: 'decoder.cpp', size: 2000 }] } });

    // TODO(fushar): make this work
    // See https://github.com/FezVrasta/popper.js/issues/478

    // const gradingLanguageButton = wrapper.find('button[data-key="gradingLanguage"]');
    // gradingLanguageButton.simulate('click');

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(contestSubmissionActions.createSubmission).toHaveBeenCalledWith(contestJid, 'contest-a', problemJid, {
      gradingLanguage: preferredGradingLanguage,
      sourceFiles: {
        encoder: { name: 'encoder.cpp', size: 1000 } as File,
        decoder: { name: 'decoder.cpp', size: 2000 } as File,
      },
    });
  });
});
