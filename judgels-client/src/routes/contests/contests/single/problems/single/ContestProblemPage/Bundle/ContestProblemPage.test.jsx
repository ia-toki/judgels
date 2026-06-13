import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import { ItemType } from '../../../../../../../../modules/api/problemBundle';
import { setSession } from '../../../../../../../../modules/session';
import { WebPrefsProvider } from '../../../../../../../../modules/webPrefs';
import { QueryClientProviderWrapper } from '../../../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../../../test/RouterWrapper';
import { nockApi } from '../../../../../../../../utils/nock';
import ContestProblemPage from './ContestProblemPage';

describe('BundleContestProblemPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async () => {
    nockApi().persist().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
      style: 'BUNDLE',
    });

    nockApi()
      .get('/contests/contestJid/problems/C/bundle/worksheet')
      .query({ language: 'id' })
      .reply(200, {
        defaultLanguage: 'fakelang',
        languages: ['fakelang'],
        problem: {
          problemJid: 'problemJid',
          alias: 'C',
          status: 'OPEN',
          submissionsLimit: 0,
        },
        totalSubmissions: 0,
        worksheet: {
          statement: {
            name: 'Fake Name',
            text: 'Lorem ipsum dos color sit amet',
          },
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
      });

    nockApi()
      .get('/contests/submissions/bundle/answers')
      .query({ contestJid: 'contestJid', problemAlias: 'C' })
      .reply(200, {});

    await act(async () =>
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
      )
    );
  };

  test('form', async () => {
    await renderComponent();

    await screen.findByText('somestatement');

    const createSubmission = nockApi()
      .post('/contests/submissions/bundle', {
        containerJid: 'contestJid',
        problemJid: 'problemJid',
        itemJid: 'fakeitemjid',
        answer: 'a',
      })
      .reply(200);

    const user = userEvent.setup();

    const input = document.querySelector('.problem-multiple-choice-item-choice input');
    await user.click(input);

    await waitFor(() => {
      expect(createSubmission.isDone()).toBe(true);
    });
  });
});
