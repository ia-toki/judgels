import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import { ContestClarificationAnswerBox } from './ContestClarificationAnswerBox';

describe('ContestClarificationAnswerBox', () => {
  beforeEach(async () => {
    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <ContestClarificationAnswerBox
              contest={{ jid: 'contestJid' }}
              clarification={{ jid: 'clarificationJid123' }}
              isBoxOpen={true}
              onToggleBox={() => {}}
            />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  });

  test('form', async () => {
    const user = userEvent.setup();

    const answer = screen.getByRole('textbox');
    await user.type(answer, 'Yes.');

    nockUriel().put('/contests/contestJid/clarifications/clarificationJid123/answer', { answer: 'Yes.' }).reply(200);

    const submitButton = screen.getByRole('button', { name: /answer/i });
    await user.click(submitButton);

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
