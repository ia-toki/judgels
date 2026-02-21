import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import { ContestProblemEditDialog } from './ContestProblemEditDialog';

describe('ContestProblemEditDialog', () => {
  const problems = [
    {
      alias: 'A',
      slug: 'pp1',
      status: 'OPEN',
      submissionsLimit: undefined,
      points: 0,
    },
    {
      alias: 'B',
      slug: 'pp2',
      status: 'OPEN',
      submissionsLimit: 10,
      points: 0,
    },
    {
      alias: 'C',
      slug: 'pp3',
      status: 'CLOSED',
      submissionsLimit: undefined,
      points: 0,
    },
    {
      alias: 'D',
      slug: 'pp4',
      status: 'CLOSED',
      submissionsLimit: 10,
      points: 0,
    },
  ];

  beforeEach(async () => {
    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <ContestProblemEditDialog contest={{ jid: 'contestJid' }} problems={problems} />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  });

  test('form', async () => {
    const user = userEvent.setup();

    const button = screen.getByRole('button');
    await user.click(button);

    const problems = screen.getByRole('textbox');
    expect(problems).toHaveValue('A,pp1\nB,pp2,OPEN,10\nC,pp3,CLOSED\nD,pp4,CLOSED,10');

    await user.clear(problems);
    await user.type(problems, 'P, qq1\n Q,qq2,OPEN,20\nR,qq3,CLOSED \nS,qq4,CLOSED,20');

    nockUriel()
      .put('/contests/contestJid/problems', [
        { alias: 'P', slug: 'qq1', status: 'OPEN' },
        { alias: 'Q', slug: 'qq2', status: 'OPEN', submissionsLimit: 20 },
        { alias: 'R', slug: 'qq3', status: 'CLOSED' },
        { alias: 'S', slug: 'qq4', status: 'CLOSED', submissionsLimit: 20 },
      ])
      .reply(200);

    const submitButton = screen.getByRole('button', { name: /save/i });
    await user.click(submitButton);

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
