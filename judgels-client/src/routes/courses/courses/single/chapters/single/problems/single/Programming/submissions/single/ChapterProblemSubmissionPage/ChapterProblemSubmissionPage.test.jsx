import { act, render, screen } from '@testing-library/react';

import { setSession } from '../../../../../../../../../../../../modules/session';
import { WebPrefsProvider } from '../../../../../../../../../../../../modules/webPrefs';
import { QueryClientProviderWrapper } from '../../../../../../../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../../../../../../../test/RouterWrapper';
import { nockJerahmeel } from '../../../../../../../../../../../../utils/nock';
import ChapterProblemSubmissionPage from './ChapterProblemSubmissionPage';

describe('ChapterProblemSubmissionPage', () => {
  let source = {};

  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async () => {
    nockJerahmeel().get('/courses/slug/courseSlug').reply(200, { jid: 'courseJid', slug: 'courseSlug' });
    nockJerahmeel().get('/courses/courseJid/chapters/chapter-1').reply(200, { jid: 'chapterJid', name: 'Chapter 1' });

    nockJerahmeel()
      .get('/submissions/programming/id/10')
      .query(true)
      .reply(200, {
        data: {
          submission: {
            id: 10,
            jid: 'submissionJid',
            gradingEngine: 'OutputOnly',
          },
          source,
        },
      });

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
