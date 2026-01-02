import { act, cleanup, render, screen, within } from '@testing-library/react';

import { TestRouter } from '../../../../../../test/RouterWrapper';
import { BundleScoreboardTable } from './BundleScoreboardTable';

describe('BundleScoreboardTable', () => {
  const scoreboard = {
    state: {
      problemJids: ['JIDBUND1', 'JIDBUND2'],
      problemAliases: ['A', 'B'],
      contestantJids: ['JIDUSER1', 'JIDUSER2'],
    },
    content: {
      entries: [
        {
          rank: 1,
          contestantJid: 'JIDUSER1',
          scores: [12, 3],
          totalScores: 15,
        },
        {
          rank: 2,
          contestantJid: 'JIDUSER2',
          scores: [10, 2],
          totalScores: 12,
        },
      ],
    },
  };

  const profilesMap = {
    JIDUSER1: { username: 'username1' },
    JIDUSER2: { username: 'username2' },
  };

  beforeEach(async () => {
    const props = { scoreboard, profilesMap };
    await act(async () =>
      render(
        <TestRouter>
          <BundleScoreboardTable {...props} />
        </TestRouter>
      )
    );
  });

  test('ranks', () => {
    const rows = screen.getAllByRole('row').slice(1);
    const ranks = rows.map(row => within(row).getAllByRole('cell')[0].textContent);
    expect(ranks).toEqual(['1', '2']);
  });

  describe('incognito ranks', () => {
    beforeEach(async () => {
      cleanup();
      const incognitoEntries = scoreboard.content.entries.map(entry => ({ ...entry, rank: -1 }));
      const incognitoScoreboard = {
        ...scoreboard,
        content: { entries: incognitoEntries },
      };
      const props = { scoreboard: incognitoScoreboard, profilesMap };
      await act(async () =>
        render(
          <TestRouter>
            <BundleScoreboardTable {...props} />
          </TestRouter>
        )
      );
    });

    it('only shows question marks', () => {
      const rows = screen.getAllByRole('row').slice(1);
      const ranks = rows.map(row => within(row).getAllByRole('cell')[0].textContent);
      expect(ranks).toEqual(['?', '?']);
    });
  });

  test('display names', () => {
    const rows = screen.getAllByRole('row').slice(1);
    const names = rows.map(row => within(row).getAllByRole('cell')[1].textContent);
    expect(names).toEqual(['username1', 'username2']);
  });

  test('display score', () => {
    const rows = screen.getAllByRole('row').slice(1);
    const score = rows.map(row => {
      const cells = within(row).getAllByRole('cell');
      return [cells[3], cells[4]].map(cell => cell.textContent);
    });
    expect(score).toEqual([
      ['12', '3'],
      ['10', '2'],
    ]);
  });

  test('display points', () => {
    const headerCells = screen.getAllByRole('columnheader');
    const points = [headerCells[3], headerCells[4]].map(cell => cell.textContent);
    expect(points).toEqual(['A', 'B']);
  });
});
