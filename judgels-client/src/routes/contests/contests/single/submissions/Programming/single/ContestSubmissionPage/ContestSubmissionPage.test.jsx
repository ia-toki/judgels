import { act, render, screen } from '@testing-library/react';

import { setSession } from '../../../../../../../../modules/session';
import { WebPrefsProvider } from '../../../../../../../../modules/webPrefs';
import { QueryClientProviderWrapper } from '../../../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../../../utils/nock';
import ContestSubmissionPage from './ContestSubmissionPage';

describe('ContestSubmissionPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  beforeEach(async () => {
    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });

    nockUriel()
      .get('/contests/submissions/programming/id/10')
      .query({ language: 'en' })
      .reply(200, {
        data: {
          submission: {
            id: 10,
            containerJid: 'contestJid',
            gradingEngine: 'OutputOnly',
          },
          source: {},
        },
      });

    await act(async () => {
      render(
        <WebPrefsProvider initialPrefs={{ statementLanguage: 'en' }}>
          <QueryClientProviderWrapper>
            <TestRouter
              initialEntries={['/contests/contest-slug/submissions/10']}
              path="/contests/$contestSlug/submissions/$submissionId"
            >
              <ContestSubmissionPage />
            </TestRouter>
          </QueryClientProviderWrapper>
        </WebPrefsProvider>
      );
    });
  });

  test('page', async () => {
    expect(await screen.findByText(/Submission #10/)).toBeInTheDocument();
  });
});
