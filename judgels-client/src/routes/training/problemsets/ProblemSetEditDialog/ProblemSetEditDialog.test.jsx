import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { parseDateTime } from '../../../../utils/datetime';
import { nockJerahmeel } from '../../../../utils/nock';
import { ProblemSetEditDialog } from './ProblemSetEditDialog';

const problemSet = {
  id: 1,
  jid: 'problemSetJid',
  slug: 'problemset',
  name: 'Problem Set',
  archiveJid: 'JIDARCH',
  description: 'This is a problem set',
  contestTime: parseDateTime('1970-01-01 00:00').getTime(),
};

describe('ProblemSetEditDialog', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  test('edit dialog form', async () => {
    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <ProblemSetEditDialog
              isOpen={true}
              problemSet={problemSet}
              archiveSlug="archive"
              onCloseDialog={() => {}}
            />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );

    const user = userEvent.setup();

    const slug = screen.getByRole('textbox', { name: /^slug/i });
    expect(slug).toHaveValue('problemset');
    await user.clear(slug);
    await user.type(slug, 'new-problemset');

    const name = screen.getByRole('textbox', { name: /name/i });
    expect(name).toHaveValue('Problem Set');
    await user.clear(name);
    await user.type(name, 'New Problem Set');

    const archiveSlug = screen.getByRole('textbox', { name: /archive slug/i });
    expect(archiveSlug).toHaveValue('archive');
    await user.clear(archiveSlug);
    await user.type(archiveSlug, 'new-archive');

    const description = screen.getByRole('textbox', { name: /description/i });
    expect(description).toHaveValue('This is a problem set');
    await user.clear(description);
    await user.type(description, 'New description');

    const contestTime = document.querySelector('input[name="contestTime"]');
    await user.clear(contestTime);
    await user.type(contestTime, '2100-01-01 00:00');

    nockJerahmeel()
      .post('/problemsets/problemSetJid', {
        slug: 'new-problemset',
        name: 'New Problem Set',
        archiveSlug: 'new-archive',
        description: 'New description',
        contestTime: parseDateTime('2100-01-01 00:00').getTime(),
      })
      .reply(200);

    const submitButton = screen.getByRole('button', { name: /update/i });
    await user.click(submitButton);

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
