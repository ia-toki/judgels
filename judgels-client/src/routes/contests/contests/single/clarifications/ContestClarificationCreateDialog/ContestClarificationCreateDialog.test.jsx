import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import { ContestClarificationCreateDialog } from './ContestClarificationCreateDialog';

describe('ContestClarificationCreateDialog', () => {
  beforeEach(async () => {
    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <ContestClarificationCreateDialog
              contest={{ jid: 'contestJid' }}
              problemJids={['problemJid1', 'problemJid2']}
              problemAliasesMap={{ problemJid1: 'A', problemJid2: 'B' }}
              problemNamesMap={{ problemJid1: 'Problem 1', problemJid2: 'Problem 2' }}
            />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  });

  test('form', async () => {
    const user = userEvent.setup();

    const button = screen.getByRole('button');
    await user.click(button);

    await screen.findByRole('dialog');

    // TODO(fushar): make this work
    // See https://github.com/FezVrasta/popper.js/issues/478

    // const topicJid = screen.getByRole('button', { name: /topic/i });
    // await user.click(topicJid);

    const title = screen.getByRole('textbox', { name: /title/i });
    await user.type(title, 'Snack');

    const question = screen.getByRole('textbox', { name: /question/i });
    await user.type(question, 'Is snack provided?');

    nockUriel()
      .post('/contests/contestJid/clarifications', {
        topicJid: 'contestJid',
        title: 'Snack',
        question: 'Is snack provided?',
      })
      .reply(200);

    const submitButton = screen.getByRole('button', { name: /submit/i });
    await user.click(submitButton);

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
