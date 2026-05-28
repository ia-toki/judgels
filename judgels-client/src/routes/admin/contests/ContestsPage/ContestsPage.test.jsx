import { act, render, screen, waitFor, within } from '@testing-library/react';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockUriel } from '../../../../utils/nock';
import ContestsPage from './ContestsPage';

describe('ContestsPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({
    contests = [
      { jid: 'JIDCONTEST1', id: 1, slug: 'contest-1', name: 'Contest 1' },
      { jid: 'JIDCONTEST2', id: 2, slug: 'contest-2', name: 'Contest 2' },
    ],
  } = {}) => {
    nockUriel()
      .get('/contests?')
      .reply(200, {
        data: { page: contests, totalCount: contests.length },
        config: { canAdminister: true },
        rolesMap: {},
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/contests']}>
            <ContestsPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('renders placeholder when there are no contests', async () => {
    await renderComponent({ contests: [] });
    expect(await screen.findByText(/no contests/i)).toBeInTheDocument();
  });

  test('renders the contests table', async () => {
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
    ).toEqual([[], ['1', 'contest-1', 'Contest 1'], ['2', 'contest-2', 'Contest 2']]);
  });

  test('renders the create button', async () => {
    await renderComponent();
    expect(await screen.findByRole('button', { name: /new contest/i })).toBeInTheDocument();
  });
});
