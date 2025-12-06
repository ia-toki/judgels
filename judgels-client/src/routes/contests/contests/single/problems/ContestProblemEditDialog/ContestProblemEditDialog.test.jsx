import { render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';
import { vi } from 'vitest';

import { ContestProblemStatus } from '../../../../../../modules/api/uriel/contestProblem';
import { ContestProblemEditDialog } from './ContestProblemEditDialog';

describe('ContestProblemEditDialog', () => {
  let onSetProblems;
  const problems = [
    {
      alias: 'A',
      slug: 'pp1',
      status: ContestProblemStatus.Open,
      submissionsLimit: undefined,
      points: 0,
    },
    {
      alias: 'B',
      slug: 'pp2',
      status: ContestProblemStatus.Open,
      submissionsLimit: 10,
      points: 0,
    },
    {
      alias: 'C',
      slug: 'pp3',
      status: ContestProblemStatus.Closed,
      submissionsLimit: undefined,
      points: 0,
    },
    {
      alias: 'D',
      slug: 'pp4',
      status: ContestProblemStatus.Closed,
      submissionsLimit: 10,
      points: 0,
    },
  ];

  beforeEach(() => {
    onSetProblems = vi.fn().mockReturnValue(() => Promise.resolve({}));

    const store = configureMockStore()({});

    const props = {
      contest: { jid: 'contestJid' },
      problems,
      onSetProblems,
    };
    render(
      <Provider store={store}>
        <ContestProblemEditDialog {...props} />
      </Provider>
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

    const submitButton = screen.getByRole('button', { name: /save/i });
    await user.click(submitButton);

    expect(onSetProblems).toHaveBeenCalledWith('contestJid', [
      {
        alias: 'P',
        slug: 'qq1',
        status: ContestProblemStatus.Open,
        submissionsLimit: undefined,
        points: undefined,
      },
      {
        alias: 'Q',
        slug: 'qq2',
        status: ContestProblemStatus.Open,
        submissionsLimit: 20,
        points: undefined,
      },
      {
        alias: 'R',
        slug: 'qq3',
        status: ContestProblemStatus.Closed,
        submissionsLimit: undefined,
        points: undefined,
      },
      {
        alias: 'S',
        slug: 'qq4',
        status: ContestProblemStatus.Closed,
        submissionsLimit: 20,
        points: undefined,
      },
    ]);
  });
});
