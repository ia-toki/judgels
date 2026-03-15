import { act, render, screen, within } from '@testing-library/react';

import { TrocScoreboardProblemState } from '../../../../../../modules/api/uriel/scoreboard';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { TrocScoreboardTable } from './TrocScoreboardTable';

describe('TrocScoreboardTable', () => {
  const mockScoreboard = {
    state: {
      problemJids: ['JIDPROG1', 'JIDPROG2', 'JIDPROG3', 'JIDPROG4'],
      problemAliases: ['A', 'B', 'C', 'D'],
      contestantJids: ['JIDUSER1', 'JIDUSER2'],
      problemPoints: [1, 10, 100, 1000],
    },
    content: {
      entries: [
        {
          rank: 1,
          contestantJid: 'JIDUSER2',
          totalPoints: 111,
          totalPenalties: 66,
          attemptsList: [2, 4, 1, 0],
          penaltyList: [3, 14, 69, 0],
          problemStateList: [
            TrocScoreboardProblemState.Accepted,
            TrocScoreboardProblemState.FirstAccepted,
            TrocScoreboardProblemState.FirstAccepted,
            TrocScoreboardProblemState.NotAccepted,
          ],
        },
        {
          rank: 2,
          contestantJid: 'JIDUSER1',
          totalPoints: 10,
          totalPenalties: 17,
          attemptsList: [1, 2, 0, 3],
          penaltyList: [10, 17, 0, 22],
          problemStateList: [
            TrocScoreboardProblemState.NotAccepted,
            TrocScoreboardProblemState.Accepted,
            TrocScoreboardProblemState.Frozen,
            TrocScoreboardProblemState.NotAccepted,
          ],
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
          <TrocScoreboardTable scoreboard={scoreboard} profilesMap={profilesMap} />
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
    const incognitoScoreboard = { ...mockScoreboard, content: { entries: incognitoEntries } };
    await renderComponent({ scoreboard: incognitoScoreboard });
    const rows = screen.getAllByRole('row').slice(1);
    const ranks = rows.map(row => within(row).getAllByRole('cell')[0].textContent);
    expect(ranks).toEqual(['?', '?']);
  });

  test('display names', async () => {
    await renderComponent();
    const rows = screen.getAllByRole('row').slice(1);
    const names = rows.map(row => within(row).getAllByRole('cell')[1].textContent);
    expect(names).toEqual(['username2', 'username1']);
  });

  test('points', async () => {
    await renderComponent();
    const getColor = td =>
      td === 'first-accepted'
        ? 'D '
        : td === 'accepted'
          ? 'G '
          : td === 'not-accepted'
            ? 'R '
            : td === 'frozen'
              ? 'F '
              : '';
    const rows = screen.getAllByRole('row').slice(1);
    const points = rows.map(row => {
      const cells = within(row).getAllByRole('cell');
      return [cells[2], cells[3], cells[4], cells[5], cells[6]].map(cell => {
        const className = cell.className;
        const top = cell.querySelector('span.top')?.textContent;
        const bottom = cell.querySelector('span.bottom')?.textContent;
        return getColor(className) + top + '/' + bottom;
      });
    });
    expect(points).toEqual([
      ['111/01:06', 'G +1/00:03', 'D +3/00:14', 'D +/01:09', '-/-'],
      ['10/00:17', 'R +1/-', 'G +1/00:17', 'F -/-', 'R +3/-'],
    ]);
  });
});
