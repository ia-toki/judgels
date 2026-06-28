import { act, render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import ArchivePage from './ArchivePage';

describe('ArchivePage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async () => {
    nockApi()
      .get('/archives')
      .reply(200, {
        data: [
          {
            id: 1,
            jid: 'JIDARCHIVE1',
            slug: 'archive-1',
            name: 'Archive 1',
            category: 'Category 1',
            description: 'Description 1',
          },
        ],
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/archives/archive-1']} path="/admin/archives/$archiveSlug">
            <ArchivePage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('details', async () => {
    await renderComponent();

    await screen.findByText(/Archive 1/);

    const tables = screen.getAllByRole('table');
    expect(
      within(tables[0])
        .getAllByRole('row')
        .map(row =>
          within(row)
            .getAllByRole('cell')
            .map(cell => cell.textContent)
        )
    ).toEqual([
      ['Slug', 'archive-1'],
      ['Name', 'Archive 1'],
      ['Category', 'Category 1'],
      ['Description', 'Description 1'],
    ]);
  });

  test('general form', async () => {
    await renderComponent();

    const user = userEvent.setup();

    await user.click(await screen.findByRole('button', { name: /edit/i }));

    const slug = screen.getByRole('textbox', { name: /slug/i });
    expect(slug).toHaveValue('archive-1');
    await user.clear(slug);
    await user.type(slug, 'new-archive');

    const names = screen.getAllByRole('textbox', { name: /name/i });
    expect(names[0]).toHaveValue('Archive 1');
    await user.clear(names[0]);
    await user.type(names[0], 'New Archive');

    const category = screen.getByRole('textbox', { name: /category/i });
    expect(category).toHaveValue('Category 1');
    await user.clear(category);
    await user.type(category, 'New Category');

    const description = document.querySelector('textarea[name="description"]');
    expect(description).toHaveValue('Description 1');
    await user.clear(description);
    await user.type(description, 'New Description');

    nockApi()
      .post('/archives/JIDARCHIVE1', {
        slug: 'new-archive',
        name: 'New Archive',
        category: 'New Category',
        description: 'New Description',
      })
      .reply(200);

    await user.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
