import { act, render, screen } from '@testing-library/react';

import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockUriel } from '../../../../utils/nock';
import ContestsPage from './ContestsPage';

describe('ContestsPage', () => {
  const renderComponent = async contests => {
    nockUriel()
      .get('/contests')
      .reply(200, {
        data: {
          page: contests,
          totalCount: contests.length,
        },
        rolesMap: {
          contestJid1: 'CONTESTANT',
          contestJid2: 'NONE',
        },
        config: {
          canAdminister: false,
        },
      });

    await act(async () => {
      return render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/contests']}>
            <ContestsPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      );
    });
  };

  test('renders placeholder when there are no contests', async () => {
    await renderComponent([]);

    expect(await screen.findByText('No contests.')).toBeInTheDocument();
    expect(screen.queryByRole('link', { name: /contest/i })).not.toBeInTheDocument();
  });

  test('renders contests', async () => {
    await renderComponent([
      {
        jid: 'contestJid1',
        slug: 'contest-1',
        name: 'Contest 1',
      },
      {
        jid: 'contestJid2',
        slug: 'contest-2',
        name: 'Contest 2',
      },
    ]);

    const links = await screen.findAllByRole('link');
    expect(links).toHaveLength(2);

    expect(links[0]).toHaveTextContent('Contest 1CONTESTANT');
    expect(links[0]).toHaveAttribute('href', '/contests/contest-1');

    expect(links[1]).toHaveTextContent('Contest 2');
    expect(links[1]).toHaveAttribute('href', '/contests/contest-2');
  });
});
