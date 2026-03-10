import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJerahmeel } from '../../../../utils/nock';
import { ProblemSetProblemEditDialog } from './ProblemSetProblemEditDialog';

const problemSet = {
  jid: 'problemSet-jid',
};

describe('ProblemSetProblemEditDialog', () => {
  const problems = [
    { alias: 'A', problemJid: 'jid-1', type: 'PROGRAMMING', contestJids: [] },
    { alias: 'B', problemJid: 'jid-2', type: 'BUNDLE', contestJids: [] },
    { alias: 'C', problemJid: 'jid-3', type: 'PROGRAMMING', contestJids: ['contestJid-1', 'contestJid-2'] },
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

  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  test('edit problems dialog form', async () => {
    nockJerahmeel()
      .get('/problemsets/problemSet-jid/problems')
      .reply(200, { data: problems, problemsMap, contestsMap });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <ProblemSetProblemEditDialog isOpen={true} problemSet={problemSet} onCloseDialog={() => {}} />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );

    const user = userEvent.setup();

    const editButton = await screen.findByRole('button', { name: /edit/i });
    await user.click(editButton);

    const problemsField = screen.getByRole('textbox', /problems/i);
    expect(problemsField).toHaveValue('A,slug-1\nB,slug-2,BUNDLE\nC,slug-3,PROGRAMMING,contestSlug-1;contestSlug-2');

    await user.clear(problemsField);
    await user.type(problemsField, 'P, slug-3\n  Q,slug-4,BUNDLE  ,contestSlug-3 ');

    nockJerahmeel()
      .put('/problemsets/problemSet-jid/problems', [
        { alias: 'P', slug: 'slug-3', type: 'PROGRAMMING', contestSlugs: [] },
        { alias: 'Q', slug: 'slug-4', type: 'BUNDLE', contestSlugs: ['contestSlug-3'] },
      ])
      .reply(200);

    const submitButton = screen.getByRole('button', { name: /save/i });
    await user.click(submitButton);

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
