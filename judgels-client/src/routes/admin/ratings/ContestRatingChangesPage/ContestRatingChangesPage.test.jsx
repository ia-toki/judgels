import { act, render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import ContestRatingChangesPage from './ContestRatingChangesPage';

describe('ContestRatingChangesPage', () => {
  const ratingsMap = {
    userJid1: { publicRating: 1600, hiddenRating: 1500 },
    userJid2: { publicRating: 1700, hiddenRating: 1400 },
  };

  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async () => {
    nockApi()
      .get('/v2/contest-rating/pending')
      .reply(200, {
        data: [{ jid: 'contestJid1', slug: 'contest-1', name: 'Contest 1', beginTime: 100, duration: 50 }],
        ratingChangesMap: {
          contestJid1: {
            ratingsMap,
            profilesMap: {
              userJid1: { username: 'user1' },
              userJid2: { username: 'user2' },
            },
          },
        },
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/ratings/contest-1']} path="/admin/ratings/$contestSlug">
            <ContestRatingChangesPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('renders the users with rating changes', async () => {
    await renderComponent();

    await screen.findByText('user1');
    const rows = screen.getAllByRole('row').slice(1);
    expect(
      rows.map(row =>
        within(row)
          .getAllByRole('cell')
          .map(td => td.textContent)
      )
    ).toEqual([
      ['user2', '1700'],
      ['user1', '1600'],
    ]);
  });

  test('applies the rating changes', async () => {
    await renderComponent();
    const user = userEvent.setup();

    nockApi().post('/v2/user-rating', { eventJid: 'contestJid1', time: 150, ratingsMap }).reply(200);

    await user.click(await screen.findByRole('button', { name: /apply rating changes/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
