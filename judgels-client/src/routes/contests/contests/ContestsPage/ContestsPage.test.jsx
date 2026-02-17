import { act, render, screen } from '@testing-library/react';
import nock from 'nock';

import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockUriel } from '../../../../utils/nock';
import ContestsPage from './ContestsPage';

describe('ContestsPage', () => {
  let contests;

  const renderComponent = () => {
    nockUriel()
      .get('/contests')
      .query({ page: 1 })
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

    return render(
      <QueryClientProviderWrapper>
        <TestRouter initialEntries={['/contests']}>
          <ContestsPage />
        </TestRouter>
      </QueryClientProviderWrapper>
    );
  };

  describe('when there are no contests', () => {
    beforeEach(async () => {
      contests = [];
      await act(async () => {
        renderComponent();
      });
    });

    it('shows placeholder text and no contests', async () => {
      expect(await screen.findByText('No contests.')).toBeInTheDocument();
      expect(screen.queryByRole('link', { name: /contest/i })).not.toBeInTheDocument();
    });
  });

  describe('when there are contests', () => {
    beforeEach(async () => {
      contests = [
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
      ];
      await act(async () => {
        renderComponent();
      });
    });

    it('shows the contests', async () => {
      const links = await screen.findAllByRole('link');
      expect(links).toHaveLength(2);
      expect(links[0]).toHaveTextContent('Contest 1CONTESTANT');
      expect(links[0]).toHaveAttribute('href', '/contests/contest-1');

      expect(links[1]).toHaveTextContent('Contest 2');
      expect(links[1]).toHaveAttribute('href', '/contests/contest-2');
    });
  });
});
