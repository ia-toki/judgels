import { act, render, screen } from '@testing-library/react';
import { vi } from 'vitest';

import { ContestRole } from '../../../../modules/api/uriel/contestWeb';
import { TestRouter } from '../../../../test/RouterWrapper';
import ContestsPage from './ContestsPage';

import * as contestActions from '../modules/contestActions';

vi.mock('../modules/contestActions');

describe('ContestsPage', () => {
  let contests;

  const renderComponent = () => {
    contestActions.getContests.mockReturnValue(
      Promise.resolve({
        data: {
          page: contests,
        },
        rolesMap: {
          contestJid1: ContestRole.Contestant,
          contestJid2: ContestRole.None,
        },
        config: {
          canAdminister: false,
        },
      })
    );

    return render(
      <TestRouter initialEntries={['/contests']}>
        <ContestsPage />
      </TestRouter>
    );
  };

  describe('when there are no contests', () => {
    beforeEach(async () => {
      contests = [];
      await act(async () => {
        renderComponent();
      });
    });

    it('shows placeholder text and no contests', () => {
      expect(screen.getByText('No contests.')).toBeInTheDocument();
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

    it('shows the contests', () => {
      const links = screen.getAllByRole('link');
      expect(links).toHaveLength(2);
      expect(links[0]).toHaveTextContent('Contest 1CONTESTANT');
      expect(links[0]).toHaveAttribute('href', '/contests/contest-1');

      expect(links[1]).toHaveTextContent('Contest 2');
      expect(links[1]).toHaveAttribute('href', '/contests/contest-2');
    });
  });
});
