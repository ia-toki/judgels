import { act, render, screen, waitFor, within } from '@testing-library/react';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJerahmeel } from '../../../../utils/nock';
import ProblemSetsPage from './ProblemSetsPage';

describe('ProblemSetsPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({
    problemSets = [
      { jid: 'JIDPROBLEMSET1', id: 1, slug: 'problemset-1', name: 'Problemset 1', archiveJid: 'JIDARCHIVE1' },
      { jid: 'JIDPROBLEMSET2', id: 2, slug: 'problemset-2', name: 'Problemset 2', archiveJid: 'JIDARCHIVE1' },
    ],
    archiveSlugsMap = { JIDARCHIVE1: 'archive-1' },
    totalCount = 2,
  } = {}) => {
    nockJerahmeel()
      .get('/problemsets')
      .query(true)
      .reply(200, {
        data: { page: problemSets, totalCount },
        archiveSlugsMap,
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/problemsets']}>
            <ProblemSetsPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('renders placeholder when there are no problem sets', async () => {
    await renderComponent({ problemSets: [], archiveSlugsMap: {}, totalCount: 0 });
    expect(await screen.findByText(/no problem sets/i)).toBeInTheDocument();
  });

  test('renders the problem sets table', async () => {
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
    ).toEqual([
      [],
      ['1', 'problemset-1', 'Problemset 1', 'archive-1'],
      ['2', 'problemset-2', 'Problemset 2', 'archive-1'],
    ]);
  });

  test('renders the create button', async () => {
    await renderComponent();
    expect(await screen.findByRole('button', { name: /new problemset/i })).toBeInTheDocument();
  });
});
