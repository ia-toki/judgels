import { act, render, screen, waitFor } from '@testing-library/react';

import { setSession } from '../../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import ContestContestantsPage from './ContestContestantsPage';

describe('ContestContestantsPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  let contestants;
  let canManage;

  const renderComponent = async () => {
    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });

    nockUriel()
      .get('/contests/contestJid/contestants')
      .reply(200, {
        data: {
          page: contestants,
          totalCount: contestants.length,
        },
        profilesMap: {
          userJid1: { username: 'username1' },
          userJid2: { username: 'username2' },
        },
        config: {
          canManage,
        },
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/contests/contest-slug/contestants']} path="/contests/$contestSlug/contestants">
            <ContestContestantsPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  describe('action buttons', () => {
    beforeEach(() => {
      contestants = [];
    });

    describe('when not canManage', () => {
      beforeEach(async () => {
        canManage = false;
        await renderComponent();
      });

      it('shows no buttons', async () => {
        await screen.findByRole('heading', { name: 'Contestants' });
        expect(screen.queryByRole('button', { name: /add contestants/i })).not.toBeInTheDocument();
        expect(screen.queryByRole('button', { name: /remove contestants/i })).not.toBeInTheDocument();
      });
    });

    describe('when canManage', () => {
      beforeEach(async () => {
        canManage = true;
        await renderComponent();
      });

      it('shows action buttons', async () => {
        expect(await screen.findByRole('button', { name: /add contestants/i })).toBeInTheDocument();
        expect(screen.getByRole('button', { name: /remove contestants/i })).toBeInTheDocument();
      });
    });
  });

  describe('content', () => {
    describe('when there are no contestants', () => {
      beforeEach(async () => {
        contestants = [];
        await renderComponent();
      });

      it('shows placeholder text and no contestants', async () => {
        expect(await screen.findByText(/no contestants/i)).toBeInTheDocument();
        expect(screen.queryByRole('row')).not.toBeInTheDocument();
      });
    });

    describe('when there are contestants', () => {
      beforeEach(async () => {
        contestants = [
          {
            userJid: 'userJid1',
          },
          {
            userJid: 'userJid2',
          },
        ];
        await renderComponent();
      });

      it('shows the contestants', async () => {
        await waitFor(() => {
          expect(screen.getAllByRole('row').length).toBeGreaterThan(1);
        });
        const rows = screen.getAllByRole('row');
        expect(rows.map(row => [...row.querySelectorAll('td')].map(cell => cell.textContent))).toEqual([
          [],
          ['1', 'username1'],
          ['2', 'username2'],
        ]);
      });
    });
  });
});
