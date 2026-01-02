import { act, render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import { applyMiddleware, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import { TestRouter } from '../../../../test/RouterWrapper';
import RatingsPage from './RatingsPage';

import * as ratingActions from '../modules/ratingActions';

vi.mock('../modules/ratingActions');

describe('RatingsPage', () => {
  let contests;
  let ratingChangesMap;

  const renderComponent = async () => {
    ratingActions.getContestsPendingRating.mockReturnValue(() =>
      Promise.resolve({
        data: contests,
        ratingChangesMap,
      })
    );
    ratingActions.updateRatings.mockReturnValue(() => Promise.resolve());

    const store = createStore(() => {}, applyMiddleware(thunk));

    await act(async () =>
      render(
        <Provider store={store}>
          <TestRouter>
            <RatingsPage />
          </TestRouter>
        </Provider>
      )
    );
  };

  describe('when there are no contests pending ratings', () => {
    beforeEach(async () => {
      contests = [];
      await renderComponent();
    });

    it('shows placeholder text and no files', async () => {
      expect(screen.getByText(/no contests/i)).toBeInTheDocument();
    });
  });

  describe('when there are contests', () => {
    beforeEach(async () => {
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
      ];
      ratingChangesMap = {
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
      await renderComponent();
    });

    it('shows the contests', () => {
      const rows = screen.getAllByRole('row').slice(1);

      expect(
        rows.map(row =>
          within(row)
            .getAllByRole('cell')
            .map(td => td.textContent)
        )
      ).toEqual([
        ['Contest 1', 'View rating changes'],
        ['Contest 2', 'View rating changes'],
      ]);
    });

    describe('when view rating changes button is clicked', () => {
      let user;

      beforeEach(async () => {
        user = userEvent.setup();
        const rows = screen.getAllByRole('row').slice(1);
        const viewButton = within(rows[0]).getByRole('button', { name: /view rating changes/i });
        await user.click(viewButton);
      });

      it('shows the users with rating changes', () => {
        const dialog = screen.getByRole('dialog');
        const rows = within(dialog).getAllByRole('row').slice(1);

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

      describe('when apply rating changes button is clicked', () => {
        beforeEach(async () => {
          const dialog = screen.getByRole('dialog');
          const buttons = within(dialog).getAllByRole('button');
          const applyButton = buttons[buttons.length - 1];
          await user.click(applyButton);
        });

        it('calls API', () => {
          expect(ratingActions.updateRatings).toHaveBeenCalledWith({
            eventJid: 'contestJid1',
            time: 150,
            ratingsMap: ratingChangesMap.contestJid1.ratingsMap,
          });
        });
      });
    });
  });
});
