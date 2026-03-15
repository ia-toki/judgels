import { act, render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';

import { TestRouter } from '../../../../../../test/RouterWrapper';
import { IoiScoreboardTable } from './IoiScoreboardTable';

describe('IoiScoreboardTable', () => {
  const mockScoreboard = {
    state: {
      problemJids: ['JIDPROG1', 'JIDPROG2', 'JIDPROG3'],
      problemAliases: ['A', 'B', 'C'],
      contestantJids: ['JIDUSER1', 'JIDUSER2'],
    },
    content: {
      entries: [
        {
          rank: 1,
          contestantJid: 'JIDUSER2',
          scores: [70, 30, 0],
          totalScores: 100,
          lastAffectingPenalty: 0,
        },
        {
          rank: 2,
          contestantJid: 'JIDUSER1',
          scores: [50, null, 10],
          totalScores: 60,
          lastAffectingPenalty: 0,
        },
      ],
    },
  };

  const mockProfilesMap = {
    JIDUSER1: { username: 'username1' },
    JIDUSER2: { username: 'username2' },
  };

  const renderComponent = async ({
    scoreboard = mockScoreboard,
    profilesMap = mockProfilesMap,
    onClickSubmissionCell,
  } = {}) => {
    await act(async () =>
      render(
        <TestRouter>
          <IoiScoreboardTable
            scoreboard={scoreboard}
            profilesMap={profilesMap}
            onClickSubmissionCell={onClickSubmissionCell}
          />
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
    const rows = screen.getAllByRole('row').slice(1);
    const points = rows.map(row => {
      const cells = within(row).getAllByRole('cell');
      return [cells[2], cells[3], cells[4], cells[5]].map(cell => cell.textContent);
    });
    expect(points).toEqual([
      ['100', '70', '30', '0'],
      ['60', '50', '-', '10'],
    ]);
  });

  test('renders submission for attempted cell when onClickSubmissionCell is passed', async () => {
    const onClickSubmissionCell = vi.fn();
    await renderComponent({ onClickSubmissionCell });

    const user = userEvent.setup();
    const rows = screen.getAllByRole('row').slice(1);

    const firstRowCells = within(rows[0]).getAllByRole('cell');
    await user.click(firstRowCells[3]);

    expect(onClickSubmissionCell).toHaveBeenCalledWith('JIDUSER2', 'JIDPROG1');
  });

  test('does not render submission for unattempted cell when onClickSubmissionCell is passed', async () => {
    const onClickSubmissionCell = vi.fn();
    await renderComponent({ onClickSubmissionCell });

    const user = userEvent.setup();
    const rows = screen.getAllByRole('row').slice(1);

    const secondRowCells = within(rows[1]).getAllByRole('cell');
    await user.click(secondRowCells[4]);

    expect(onClickSubmissionCell).not.toBeCalled();
  });
});
