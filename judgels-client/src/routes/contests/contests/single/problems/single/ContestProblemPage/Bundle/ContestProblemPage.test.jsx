import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';

import { ItemType } from '../../../../../../../../modules/api/sandalphon/problemBundle';
import { ContestStyle } from '../../../../../../../../modules/api/uriel/contest';
import { ContestProblemStatus } from '../../../../../../../../modules/api/uriel/contestProblem';
import { WebPrefsProvider } from '../../../../../../../../modules/webPrefs';
import { QueryClientProviderWrapper } from '../../../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../../../utils/nock';
import ContestProblemPage from './ContestProblemPage';

import * as contestSubmissionActions from '../../../../submissions/Bundle/modules/contestSubmissionActions';
import * as contestProblemActions from '../../../modules/contestProblemActions';

vi.mock('../../../modules/contestProblemActions');
vi.mock('../../../../submissions/Bundle/modules/contestSubmissionActions');

describe('BundleContestProblemPage', () => {
  beforeEach(async () => {
    contestProblemActions.getBundleProblemWorksheet.mockReturnValue(
      Promise.resolve({
        defaultLanguage: 'fakelang',
        languages: ['fakelang'],
        problem: {
          problemJid: 'problemJid',
          alias: 'C',
          status: ContestProblemStatus.Open,
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
      })
    );

    contestSubmissionActions.createItemSubmission.mockReturnValue(Promise.resolve({}));
    contestSubmissionActions.getLatestSubmissions.mockReturnValue(Promise.resolve({}));

    nockUriel().persist().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
      style: ContestStyle.Bundle,
    });

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

  test('form', async () => {
    await screen.findByText('somestatement');

    const user = userEvent.setup();

    const input = document.querySelector('.problem-multiple-choice-item-choice input');
    await user.click(input);

    expect(contestSubmissionActions.createItemSubmission).toHaveBeenCalledWith(
      'contestJid',
      'problemJid',
      'fakeitemjid',
      'a'
    );
  });
});
