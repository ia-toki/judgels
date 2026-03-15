import { act, render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJophiel, nockUriel } from '../../../../utils/nock';
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
        name: 'Contest 1',
        beginTime: 100,
        duration: 50,
      },
      {
        jid: 'contestJid2',
        name: 'Contest 2',
        beginTime: 200,
        duration: 100,
      },
    ],
    ratingChangesMap = mockRatingChangesMap,
  } = {}) => {
    nockUriel().get('/contest-rating/pending').reply(200, {
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

  test('renders the users with rating changes', async () => {
    await renderComponent();

    const user = userEvent.setup();
    const rows = await screen.findAllByRole('row');
    const viewButton = within(rows[1]).getByRole('button', { name: /view rating changes/i });
    await user.click(viewButton);

    const dialog = screen.getByRole('dialog');
    const dialogRows = within(dialog).getAllByRole('row').slice(1);

    expect(
      dialogRows.map(row =>
        within(row)
          .getAllByRole('cell')
          .map(td => td.textContent)
      )
    ).toEqual([
      ['user2', '1700'],
      ['user1', '1600'],
    ]);
  });

  test('calls API', async () => {
    await renderComponent();

    const user = userEvent.setup();
    const rows = await screen.findAllByRole('row');
    const viewButton = within(rows[1]).getByRole('button', { name: /view rating changes/i });
    await user.click(viewButton);

    nockJophiel()
      .post('/user-rating', {
        eventJid: 'contestJid1',
        time: 150,
        ratingsMap: mockRatingChangesMap.contestJid1.ratingsMap,
      })
      .reply(200);

    const dialog = screen.getByRole('dialog');
    const buttons = within(dialog).getAllByRole('button');
    const applyButton = buttons[buttons.length - 1];
    await user.click(applyButton);

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
