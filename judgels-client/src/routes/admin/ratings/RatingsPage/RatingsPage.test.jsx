import { act, render, screen, within } from '@testing-library/react';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import RatingsPage from './RatingsPage';

describe('RatingsPage', () => {
  const mockRatingChangesMap = {
    contestJid1: {
      ratingsMap: {
        userJid1: { publicRating: 1600, hiddenRating: 1500 },
        userJid2: { publicRating: 1700, hiddenRating: 1400 },
      },
      profilesMap: {
        userJid1: { username: 'user1' },
        userJid2: { username: 'user2' },
      },
    },
    contestJid2: {
      ratingsMap: {
        userJid1: { publicRating: 1500, hiddenRating: 1400 },
      },
      profilesMap: {
        userJid1: { username: 'user1' },
      },
    },
  };

  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({
    contests = [
      {
        jid: 'contestJid1',
        slug: 'contest-1',
        name: 'Contest 1',
        beginTime: 100,
        duration: 50,
      },
      {
        jid: 'contestJid2',
        slug: 'contest-2',
        name: 'Contest 2',
        beginTime: 200,
        duration: 100,
      },
    ],
    ratingChangesMap = mockRatingChangesMap,
  } = {}) => {
    nockApi().get('/v2/contest-rating/pending').reply(200, {
      data: contests,
      ratingChangesMap,
    });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <RatingsPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('renders placeholder when there are no contests', async () => {
    await renderComponent({ contests: [] });
    expect(await screen.findByText(/no contests/i)).toBeInTheDocument();
  });

  test('renders the contests', async () => {
    await renderComponent();

    const rows = await screen.findAllByRole('row');

    expect(
      rows.slice(1).map(row =>
        within(row)
          .getAllByRole('cell')
          .map(td => td.textContent)
      )
    ).toEqual([
      ['Contest 1', 'View rating changes'],
      ['Contest 2', 'View rating changes'],
    ]);
  });

  test('links to the rating changes of each contest', async () => {
    await renderComponent();

    const rows = await screen.findAllByRole('row');
    expect(within(rows[1]).getByRole('link', { name: /view rating changes/i })).toHaveAttribute(
      'href',
      '/admin/ratings/contest-1'
    );
  });
});
