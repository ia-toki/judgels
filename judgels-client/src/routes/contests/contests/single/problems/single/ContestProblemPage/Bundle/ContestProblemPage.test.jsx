import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { ItemType } from '../../../../../../../../modules/api/sandalphon/problemBundle';
import { setSession } from '../../../../../../../../modules/session';
import { WebPrefsProvider } from '../../../../../../../../modules/webPrefs';
import { QueryClientProviderWrapper } from '../../../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../../../utils/nock';
import ContestProblemPage from './ContestProblemPage';

describe('BundleContestProblemPage', () => {
  beforeEach(async () => {
    setSession('token', { jid: 'userJid' });

    nockUriel().persist().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
      style: 'BUNDLE',
    });

    nockUriel()
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

    nockUriel()
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
  });

  afterEach(() => {
    nock.cleanAll();
  });

  test('form', async () => {
    await screen.findByText('somestatement');

    const createSubmission = nockUriel()
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
