import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJerahmeel } from '../../../../utils/nock';
import { ArchiveEditDialog } from './ArchiveEditDialog';

const archive = {
  id: 1,
  jid: 'archiveJid',
  slug: 'archive',
  name: 'Archive',
  category: 'Category',
  description: 'This is a archive',
};

describe('ArchiveEditDialog', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  test('edit dialog form', async () => {
    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <ArchiveEditDialog isOpen={true} archive={archive} onCloseDialog={() => {}} />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );

    const user = userEvent.setup();

    const slug = screen.getByRole('textbox', { name: /slug/i });
    expect(slug).toHaveValue('archive');
    await user.clear(slug);
    await user.type(slug, 'new-archive');

    const name = screen.getByRole('textbox', { name: /name/i });
    expect(name).toHaveValue('Archive');
    await user.clear(name);
    await user.type(name, 'New archive');

    const category = screen.getByRole('textbox', { name: /category/i });
    expect(category).toHaveValue('Category');
    await user.clear(category);
    await user.type(category, 'New category');

    const description = screen.getByRole('textbox', { name: /description/i });
    expect(description).toHaveValue('This is a archive');
    await user.clear(description);
    await user.type(description, 'New description');

    nockJerahmeel()
      .post('/archives/archiveJid', {
        slug: 'new-archive',
        name: 'New archive',
        category: 'New category',
        description: 'New description',
      })
      .reply(200);

    const submitButton = screen.getByRole('button', { name: /update/i });
    await user.click(submitButton);

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
