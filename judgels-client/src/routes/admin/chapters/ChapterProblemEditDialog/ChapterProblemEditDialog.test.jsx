import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJerahmeel } from '../../../../utils/nock';
import { ChapterProblemEditDialog } from './ChapterProblemEditDialog';

const chapter = {
  jid: 'chapter-jid',
};

describe('ChapterProblemEditDialog', () => {
  const problems = [
    { alias: 'A', problemJid: 'jid-1', type: 'PROGRAMMING' },
    { alias: 'B', problemJid: 'jid-2', type: 'BUNDLE' },
  ];

  const problemsMap = {
    'jid-1': { slug: 'slug-1' },
    'jid-2': { slug: 'slug-2' },
  };

  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  test('edit problems dialog form', async () => {
    nockJerahmeel().get('/chapters/chapter-jid/problems').reply(200, { data: problems, problemsMap });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <ChapterProblemEditDialog isOpen={true} chapter={chapter} onCloseDialog={() => {}} />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );

    const user = userEvent.setup();

    const editButton = await screen.findByRole('button', { name: /edit/i });
    await user.click(editButton);

    const problemsField = screen.getByRole('textbox', /problems/i);
    expect(problemsField).toHaveValue('A,slug-1\nB,slug-2,BUNDLE');

    await user.clear(problemsField);
    await user.type(problemsField, 'P, slug-3\n  Q,slug-4,BUNDLE  ');

    nockJerahmeel()
      .put('/chapters/chapter-jid/problems', [
        { alias: 'P', slug: 'slug-3', type: 'PROGRAMMING' },
        { alias: 'Q', slug: 'slug-4', type: 'BUNDLE' },
      ])
      .reply(200);

    const submitButton = screen.getByRole('button', { name: /save/i });
    await user.click(submitButton);

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
