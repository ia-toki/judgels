import { act, render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';

import { ProblemType } from '../../../../modules/api/sandalphon/problem';
import { ProblemSetProblemEditDialog } from './ProblemSetProblemEditDialog';

const problemSet = {
  jid: 'problemSet-jid',
};

describe('ProblemSetProblemEditDialog', () => {
  let onGetProblems;
  let onSetProblems;

  const problems = [
    {
      alias: 'A',
      problemJid: 'jid-1',
      type: ProblemType.Programming,
      contestJids: [],
    },
    {
      alias: 'B',
      problemJid: 'jid-2',
      type: ProblemType.Bundle,
      contestJids: [],
    },
    {
      alias: 'C',
      problemJid: 'jid-3',
      type: ProblemType.Programming,
      contestJids: ['contestJid-1', 'contestJid-2'],
    },
  ];

  const problemsMap = {
    'jid-1': { slug: 'slug-1' },
    'jid-2': { slug: 'slug-2' },
    'jid-3': { slug: 'slug-3' },
  };
  const contestsMap = {
    'contestJid-1': { slug: 'contestSlug-1' },
    'contestJid-2': { slug: 'contestSlug-2' },
  };

  beforeEach(async () => {
    onGetProblems = jest.fn().mockReturnValue(Promise.resolve({ data: problems, problemsMap, contestsMap }));
    onSetProblems = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store = configureMockStore()({});

    const props = {
      isOpen: true,
      problemSet,
      onCloseDialog: jest.fn(),
      onGetProblems,
      onSetProblems,
    };
    await act(async () =>
      render(
        <Provider store={store}>
          <ProblemSetProblemEditDialog {...props} />
        </Provider>
      )
    );
  });

  test('edit problems dialog form', async () => {
    const user = userEvent.setup();

    const editButton = screen.getByRole('button', { name: /edit/i });
    await user.click(editButton);

    const problemsField = screen.getByRole('textbox', /problems/i);
    expect(problemsField).toHaveValue('A,slug-1\nB,slug-2,BUNDLE\nC,slug-3,PROGRAMMING,contestSlug-1;contestSlug-2');

    await user.clear(problemsField);
    await user.type(problemsField, 'P, slug-3\n  Q,slug-4,BUNDLE  ,contestSlug-3 ');

    const submitButton = screen.getByRole('button', { name: /save/i });
    await user.click(submitButton);

    expect(onSetProblems).toHaveBeenCalledWith(problemSet.jid, [
      {
        alias: 'P',
        slug: 'slug-3',
        type: 'PROGRAMMING',
        contestSlugs: [],
      },
      {
        alias: 'Q',
        slug: 'slug-4',
        type: 'BUNDLE',
        contestSlugs: ['contestSlug-3'],
      },
    ]);
  });
});
