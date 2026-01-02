import { act, cleanup, render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';

import { TestRouter } from '../../../../../../test/RouterWrapper';
import { IoiScoreboardTable } from './IoiScoreboardTable';

describe('IoiScoreboardTable', () => {
  const scoreboard = {
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

  const profilesMap = {
    JIDUSER1: { username: 'username1' },
    JIDUSER2: { username: 'username2' },
  };

  beforeEach(async () => {
    const props = { scoreboard, profilesMap };
    await act(async () =>
      render(
        <TestRouter>
          <IoiScoreboardTable {...props} />
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
      const incognitoScoreboard = { ...scoreboard, content: { entries: incognitoEntries } };
      const props = { scoreboard: incognitoScoreboard, profilesMap };
      await act(async () =>
        render(
          <TestRouter>
            <IoiScoreboardTable {...props} />
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
    expect(names).toEqual(['username2', 'username1']);
  });

  test('points', () => {
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

  describe('clicking a submission cell', () => {
    describe('when onClickSubmissionCell is passed', () => {
      const onClickSubmissionCell = vi.fn();

      beforeEach(async () => {
        cleanup();
        const props = { scoreboard, profilesMap, onClickSubmissionCell };
        await act(async () =>
          render(
            <TestRouter>
              <IoiScoreboardTable {...props} />
            </TestRouter>
          )
        );
      });

      test('shows submission for attempted cell', async () => {
        const user = userEvent.setup();
        const rows = screen.getAllByRole('row').slice(1);

        const firstRowCells = within(rows[0]).getAllByRole('cell');
        await user.click(firstRowCells[3]);

        expect(onClickSubmissionCell).toHaveBeenCalledWith('JIDUSER2', 'JIDPROG1');
      });

      test('does not show submission for unattempted cell', async () => {
        const user = userEvent.setup();
        const rows = screen.getAllByRole('row').slice(1);

        const secondRowCells = within(rows[1]).getAllByRole('cell');
        await user.click(secondRowCells[4]);

        expect(onClickSubmissionCell).not.toBeCalled();
      });
    });
  });
});
