import { act, render, screen, within } from '@testing-library/react';

import { TestRouter } from '../../../../../../test/RouterWrapper';
import { BundleScoreboardTable } from './BundleScoreboardTable';

describe('BundleScoreboardTable', () => {
  const mockScoreboard = {
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

  const mockProfilesMap = {
    JIDUSER1: { username: 'username1' },
    JIDUSER2: { username: 'username2' },
  };

  const renderComponent = async ({ scoreboard = mockScoreboard, profilesMap = mockProfilesMap } = {}) => {
    await act(async () =>
      render(
        <TestRouter>
          <BundleScoreboardTable scoreboard={scoreboard} profilesMap={profilesMap} />
        </TestRouter>
      )
    );
  };

  test('ranks', async () => {
    await renderComponent();
    const rows = screen.getAllByRole('row').slice(1);
    const ranks = rows.map(row => within(row).getAllByRole('cell')[0].textContent);
    expect(ranks).toEqual(['1', '2']);
  });

  test('incognito ranks only show question marks', async () => {
    const incognitoEntries = mockScoreboard.content.entries.map(entry => ({ ...entry, rank: -1 }));
    const incognitoScoreboard = {
      ...mockScoreboard,
      content: { entries: incognitoEntries },
    };
    await renderComponent({ scoreboard: incognitoScoreboard });
    const rows = screen.getAllByRole('row').slice(1);
    const ranks = rows.map(row => within(row).getAllByRole('cell')[0].textContent);
    expect(ranks).toEqual(['?', '?']);
  });

  test('display names', async () => {
    await renderComponent();
    const rows = screen.getAllByRole('row').slice(1);
    const names = rows.map(row => within(row).getAllByRole('cell')[1].textContent);
    expect(names).toEqual(['username1', 'username2']);
  });

  test('display score', async () => {
    await renderComponent();
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

  test('display points', async () => {
    await renderComponent();
    const headerCells = screen.getAllByRole('columnheader');
    const points = [headerCells[3], headerCells[4]].map(cell => cell.textContent);
    expect(points).toEqual(['A', 'B']);
  });
});
