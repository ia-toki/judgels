import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJerahmeel } from '../../../../utils/nock';
import { ArchiveGeneralSection } from './ArchiveGeneralSection';

describe('ArchiveGeneralSection', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const archive = {
    id: 1,
    jid: 'JIDARCHIVE1',
    slug: 'archive-1',
    name: 'Archive 1',
    category: 'Category 1',
    description: 'Description 1',
  };

  const renderComponent = async () => {
    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <ArchiveGeneralSection archive={archive} />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('renders general details', async () => {
    await renderComponent();

    const table = screen.getByRole('table');
    expect(
      screen
        .getAllByRole('row')
        .map(row => screen.getAllByRole('cell', { container: row }).map(cell => cell.textContent))
    );
  });

  test('form', async () => {
    await renderComponent();

    const u = userEvent.setup();

    const button = await screen.findByRole('button', { name: /edit/i });
    await u.click(button);

    const slug = screen.getByRole('textbox', { name: /slug/i });
    expect(slug).toHaveValue('archive-1');
    await u.clear(slug);
    await u.type(slug, 'new-archive');

    const names = screen.getAllByRole('textbox', { name: /name/i });
    expect(names[0]).toHaveValue('Archive 1');
    await u.clear(names[0]);
    await u.type(names[0], 'New Archive');

    const category = screen.getByRole('textbox', { name: /category/i });
    expect(category).toHaveValue('Category 1');
    await u.clear(category);
    await u.type(category, 'New Category');

    const description = document.querySelector('textarea[name="description"]');
    expect(description).toHaveValue('Description 1');
    await u.clear(description);
    await u.type(description, 'New Description');

    nockJerahmeel()
      .post('/archives/JIDARCHIVE1', {
        slug: 'new-archive',
        name: 'New Archive',
        category: 'New Category',
        description: 'New Description',
      })
      .reply(200);

    await u.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
