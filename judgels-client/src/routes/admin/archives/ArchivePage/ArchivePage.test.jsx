import { act, render, screen, within } from '@testing-library/react';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJerahmeel } from '../../../../utils/nock';
import ArchivePage from './ArchivePage';

describe('ArchivePage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async () => {
    nockJerahmeel()
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

  test('renders archive details', async () => {
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
});
