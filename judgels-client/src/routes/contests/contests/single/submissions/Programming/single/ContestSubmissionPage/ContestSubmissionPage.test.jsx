import { act, render, screen } from '@testing-library/react';
import { vi } from 'vitest';

import { OutputOnlyOverrides } from '../../../../../../../../modules/api/gabriel/language';
import { WebPrefsProvider } from '../../../../../../../../modules/webPrefs';
import { QueryClientProviderWrapper } from '../../../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../../../utils/nock';
import ContestSubmissionPage from './ContestSubmissionPage';

import * as contestSubmissionActions from '../../modules/contestSubmissionActions';

vi.mock('../../modules/contestSubmissionActions');

describe('ContestSubmissionPage', () => {
  beforeEach(async () => {
    contestSubmissionActions.getSubmissionWithSource.mockReturnValue(
      Promise.resolve({
        data: {
          submission: {
            id: 10,
            gradingEngine: OutputOnlyOverrides.KEY,
          },
          source: {},
        },
      })
    );

    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
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
