import { act, render, screen, waitFor, within } from '@testing-library/react';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJerahmeel } from '../../../../utils/nock';
import ArchivesPage from './ArchivesPage';

describe('ArchivesPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({
    archives = [
      { jid: 'JIDARCHIVE1', id: 1, slug: 'archive-1', name: 'Archive 1', category: 'Category 1' },
      { jid: 'JIDARCHIVE2', id: 2, slug: 'archive-2', name: 'Archive 2', category: 'Category 2' },
    ],
  } = {}) => {
    nockJerahmeel().get('/archives').reply(200, { data: archives });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/archives']}>
            <ArchivesPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('renders placeholder when there are no archives', async () => {
    await renderComponent({ archives: [] });
    expect(await screen.findByText(/no archives/i)).toBeInTheDocument();
  });

  test('renders the archives table', async () => {
    await renderComponent();

    await waitFor(() => {
      expect(screen.getAllByRole('row').length).toBeGreaterThan(1);
    });
    const rows = screen.getAllByRole('row');
    expect(
      rows.map(row =>
        within(row)
          .queryAllByRole('cell')
          .map(cell => cell.textContent)
      )
    ).toEqual([[], ['1', 'archive-1', 'Archive 1', 'Category 1'], ['2', 'archive-2', 'Archive 2', 'Category 2']]);
  });

  test('renders the create button', async () => {
    await renderComponent();
    expect(await screen.findByRole('button', { name: /new archive/i })).toBeInTheDocument();
  });
});
