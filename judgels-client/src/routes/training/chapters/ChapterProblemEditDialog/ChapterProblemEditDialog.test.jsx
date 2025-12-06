import { act, render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';
import { vi } from 'vitest';

import { ProblemType } from '../../../../modules/api/sandalphon/problem';
import { ChapterProblemEditDialog } from './ChapterProblemEditDialog';

const chapter = {
  jid: 'chapter-jid',
};

describe('ChapterProblemEditDialog', () => {
  let onGetProblems;
  let onSetProblems;

  const problems = [
    {
      alias: 'A',
      problemJid: 'jid-1',
      type: ProblemType.Programming,
    },
    {
      alias: 'B',
      problemJid: 'jid-2',
      type: ProblemType.Bundle,
    },
  ];

  const problemsMap = {
    'jid-1': { slug: 'slug-1' },
    'jid-2': { slug: 'slug-2' },
  };

  beforeEach(async () => {
    onGetProblems = vi.fn().mockReturnValue(Promise.resolve({ data: problems, problemsMap }));
    onSetProblems = vi.fn().mockReturnValue(() => Promise.resolve({}));

    const store = configureMockStore()({});

    const props = {
      isOpen: true,
      chapter,
      onCloseDialog: vi.fn(),
      onGetProblems,
      onSetProblems,
    };
    await act(async () =>
      render(
        <Provider store={store}>
          <ChapterProblemEditDialog {...props} />
        </Provider>
      )
    );
  });

  test('edit problems dialog form', async () => {
    const user = userEvent.setup();

    const editButton = screen.getByRole('button', { name: /edit/i });
    await user.click(editButton);

    const problemsField = screen.getByRole('textbox', /problems/i);
    expect(problemsField).toHaveValue('A,slug-1\nB,slug-2,BUNDLE');

    await user.clear(problemsField);
    await user.type(problemsField, 'P, slug-3\n  Q,slug-4,BUNDLE  ');

    const submitButton = screen.getByRole('button', { name: /save/i });
    await user.click(submitButton);

    expect(onSetProblems).toHaveBeenCalledWith(chapter.jid, [
      {
        alias: 'P',
        slug: 'slug-3',
        type: 'PROGRAMMING',
      },
      {
        alias: 'Q',
        slug: 'slug-4',
        type: 'BUNDLE',
      },
    ]);
  });
});
