import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import ArchiveCreatePage from './ArchiveCreatePage';

describe('ArchiveCreatePage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  test('creates a archive', async () => {
    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/archives/-/new']}>
            <ArchiveCreatePage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );

    const user = userEvent.setup();

    const slug = screen.getByRole('textbox', { name: /slug/i });
    await user.type(slug, 'new-archive');

    const name = screen.getByRole('textbox', { name: /^name$/i });
    await user.type(name, 'New archive');

    const category = screen.getByRole('textbox', { name: /category/i });
    await user.type(category, 'New category');

    const description = screen.getByRole('textbox', { name: /description/i });
    await user.type(description, 'New description');

    nockApi()
      .post('/v2/archives', {
        slug: 'new-archive',
        name: 'New archive',
        category: 'New category',
        description: 'New description',
      })
      .reply(200);

    const submitButton = screen.getByRole('button', { name: /create/i });
    await user.click(submitButton);

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
