import { act, render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import { ContestProblemStatus } from '../../../../../../../../modules/api/uriel/contestProblem';
import { WebPrefsProvider } from '../../../../../../../../modules/webPrefs';
import { QueryClientProviderWrapper } from '../../../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../../../utils/nock';
import ContestProblemPage from './ContestProblemPage';

import * as contestSubmissionActions from '../../../../submissions/Programming/modules/contestSubmissionActions';
import * as contestProblemActions from '../../../modules/contestProblemActions';

vi.mock('../../../modules/contestProblemActions');
vi.mock('../../../../submissions/Programming/modules/contestSubmissionActions');

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

    contestSubmissionActions.createSubmission.mockReturnValue(() => Promise.resolve({}));

    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });

    const store = createStore(combineReducers({}), applyMiddleware(thunk));

    await act(async () => {
      render(
        <WebPrefsProvider>
          <QueryClientProviderWrapper>
            <Provider store={store}>
              <TestRouter
                initialEntries={['/contests/contest-slug/problems/C']}
                path="/contests/$contestSlug/problems/$problemAlias"
              >
                <ContestProblemPage />
              </TestRouter>
            </Provider>
          </QueryClientProviderWrapper>
        </WebPrefsProvider>
      );
    });
  });

  test('form', async () => {
    await screen.findByText('Lorem ipsum');

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

    expect(contestSubmissionActions.createSubmission).toHaveBeenCalledWith('contestJid', 'contest-slug', 'problemJid', {
      gradingLanguage: 'Cpp11',
      sourceFiles: {
        encoder: expect.objectContaining({ name: 'encoder.cpp' }),
        decoder: expect.objectContaining({ name: 'decoder.cpp' }),
      },
    });
  });
});
