import { act, render, screen } from '@testing-library/react';
import { vi } from 'vitest';

import { OutputOnlyOverrides } from '../../../../../../../../../../../../modules/api/gabriel/language';
import { WebPrefsProvider } from '../../../../../../../../../../../../modules/webPrefs';
import { QueryClientProviderWrapper } from '../../../../../../../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../../../../../../../test/RouterWrapper';
import { nockJerahmeel } from '../../../../../../../../../../../../utils/nock';
import ChapterProblemSubmissionPage from './ChapterProblemSubmissionPage';

import * as chapterProblemSubmissionActions from '../../modules/chapterProblemSubmissionActions';

vi.mock('../../modules/chapterProblemSubmissionActions');

describe('ChapterProblemSubmissionPage', () => {
  let source = {};

  const renderComponent = async () => {
    chapterProblemSubmissionActions.getSubmissionWithSource.mockReturnValue(
      Promise.resolve({
        data: {
          submission: {
            id: 10,
            jid: 'submissionJid',
            gradingEngine: OutputOnlyOverrides.KEY,
          },
          source,
        },
      })
    );
    chapterProblemSubmissionActions.getSubmissionSourceImage.mockReturnValue(Promise.resolve('image url'));

    nockJerahmeel().get('/courses/slug/courseSlug').reply(200, { jid: 'courseJid', slug: 'courseSlug' });
    nockJerahmeel().get('/courses/courseJid/chapters/chapter-1').reply(200, { jid: 'chapterJid', name: 'Chapter 1' });

    await act(async () =>
      render(
        <WebPrefsProvider initialPrefs={{ statementLanguage: 'en' }}>
          <QueryClientProviderWrapper>
            <TestRouter
              initialEntries={['/courses/courseSlug/chapters/chapter-1/problems/A/submissions/10']}
              path="/courses/$courseSlug/chapters/$chapterAlias/problems/$problemAlias/submissions/$submissionId"
            >
              <ChapterProblemSubmissionPage />
            </TestRouter>
          </QueryClientProviderWrapper>
        </WebPrefsProvider>
      )
    );
  };

  beforeEach(async () => {
    await renderComponent();
  });

  test('page', async () => {
    await screen.findByText('Submission #10');
  });
});
