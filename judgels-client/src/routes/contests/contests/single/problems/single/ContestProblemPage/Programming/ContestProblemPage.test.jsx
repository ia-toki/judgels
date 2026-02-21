import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../../../../../modules/session';
import { WebPrefsProvider } from '../../../../../../../../modules/webPrefs';
import { QueryClientProviderWrapper } from '../../../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../../../utils/nock';
import ContestProblemPage from './ContestProblemPage';

describe('ProgrammingContestProblemPage', () => {
  beforeEach(async () => {
    setSession('token', { jid: 'userJid' });

    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });

    nockUriel()
      .get('/contests/contestJid/problems/C/programming/worksheet')
      .query({ language: 'id' })
      .reply(200, {
        defaultLanguage: 'en',
        languages: ['en'],
        problem: {
          problemJid: 'problemJid',
          alias: 'C',
          status: 'OPEN',
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
      });

    await act(async () => {
      render(
        <WebPrefsProvider>
          <QueryClientProviderWrapper>
            <TestRouter
              initialEntries={['/contests/contest-slug/problems/C']}
              path="/contests/$contestSlug/problems/$problemAlias"
            >
              <ContestProblemPage />
            </TestRouter>
          </QueryClientProviderWrapper>
        </WebPrefsProvider>
      );
    });
  });

  afterEach(() => {
    nock.cleanAll();
  });

  test('form', async () => {
    await screen.findByText('Lorem ipsum');

    const createSubmission = nockUriel()
      .post('/contests/submissions/programming', body => {
        return (
          body.includes('name="contestJid"\r\n\r\ncontestJid\r\n') &&
          body.includes('name="problemJid"\r\n\r\nproblemJid\r\n') &&
          body.includes('name="gradingLanguage"\r\n\r\nCpp11\r\n') &&
          body.includes('name="sourceFiles.encoder"') &&
          body.includes('Content-Type: text/plain\r\n\r\nencoder content\r\n') &&
          body.includes('name="sourceFiles.decoder"') &&
          body.includes('Content-Type: text/plain\r\n\r\ndecoder content\r\n')
        );
      })
      .reply(200);

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

    await waitFor(() => {
      expect(createSubmission.isDone()).toBe(true);
    });
  });
});
