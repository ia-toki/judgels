import { act, render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import { ContestProblemStatus } from '../../../../../../../../modules/api/uriel/contestProblem';
import webPrefsReducer from '../../../../../../../../modules/webPrefs/webPrefsReducer';
import { TestRouter } from '../../../../../../../../test/RouterWrapper';
import contestReducer, { PutContest } from '../../../../../modules/contestReducer';
import ContestProblemPage from './ContestProblemPage';

import * as breadcrumbsActions from '../../../../../../../../modules/breadcrumbs/breadcrumbsActions';
import * as webPrefsActions from '../../../../../../../../modules/webPrefs/webPrefsActions';
import * as contestSubmissionActions from '../../../../submissions/Programming/modules/contestSubmissionActions';
import * as contestProblemActions from '../../../modules/contestProblemActions';

vi.mock('../../../modules/contestProblemActions');
vi.mock('../../../../submissions/Programming/modules/contestSubmissionActions');
vi.mock('../../../../../../../../modules/webPrefs/webPrefsActions');
vi.mock('../../../../../../../../modules/breadcrumbs/breadcrumbsActions');

describe('ProgrammingContestProblemPage', () => {
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

    const store = createStore(
      combineReducers({
        webPrefs: webPrefsReducer,
        uriel: combineReducers({ contest: contestReducer }),
      }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutContest({ jid: 'contestJid', slug: 'contest-a' }));

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

    const encoderInput = document.querySelector('input[name="sourceFiles.encoder"]');
    const encoderFile = new File(['encoder content'], 'encoder.cpp', { type: 'text/plain' });
    Object.defineProperty(encoderFile, 'size', { value: 1000 });
    await user.upload(encoderInput, encoderFile);

    const decoderInput = document.querySelector('input[name="sourceFiles.decoder"]');
    const decoderFile = new File(['decoder content'], 'decoder.cpp', { type: 'text/plain' });
    Object.defineProperty(decoderFile, 'size', { value: 2000 });
    await user.upload(decoderInput, decoderFile);

    // TODO(fushar): make this work
    // See https://github.com/FezVrasta/popper.js/issues/478

    // const gradingLanguageButton = screen.getByRole('button', { name: /grading language/i });
    // await user.click(gradingLanguageButton);

    const submitButton = screen.getByRole('button', { name: /submit/i });
    await user.click(submitButton);

    expect(webPrefsActions.updateGradingLanguage).toHaveBeenCalledWith('Cpp11');
    expect(contestSubmissionActions.createSubmission).toHaveBeenCalledWith('contestJid', 'contest-a', 'problemJid', {
      gradingLanguage: 'Cpp11',
      sourceFiles: {
        encoder: expect.objectContaining({ name: 'encoder.cpp' }),
        decoder: expect.objectContaining({ name: 'decoder.cpp' }),
      },
    });
  });
});
