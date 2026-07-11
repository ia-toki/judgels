import { act, render, screen, within } from '@testing-library/react';

import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockApi } from '../../../../../../utils/nock';
import ContestHistoryPage from './ContestHistoryPage';

describe('ContestHistoryPage', () => {
  const renderComponent = async response => {
    nockApi().get('/contest-history/public').query({ username: 'username1' }).reply(200, response);

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/profiles/username1']} path="/profiles/$username">
            <ContestHistoryPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('renders placeholder when there are no contests', async () => {
    await renderComponent({ data: [], contestsMap: {} });
    expect(await screen.findByText(/no contests/i)).toBeInTheDocument();
  });

  test('renders the ranks, with a dash for unofficial contests', async () => {
    await renderComponent({
      data: [
        { contestJid: 'contestJid1', rank: 3 },
        { contestJid: 'contestJid2', rank: 0 },
      ],
      contestsMap: {
        contestJid1: { name: 'Contest 1', slug: 'contest-1' },
        contestJid2: { name: 'Contest 2', slug: 'contest-2' },
      },
    });

    const rows = await screen.findAllByRole('row');

    // Rows are rendered most-recent first.
    expect(
      rows.slice(1).map(row =>
        within(row)
          .getAllByRole('cell')
          .slice(1, 3)
          .map(td => td.textContent)
      )
    ).toEqual([
      ['Contest 2', '-'],
      ['Contest 1', '3'],
    ]);
  });
});
