import { cleanup, render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';

import { IcpcScoreboardProblemState } from '../../../../../../modules/api/uriel/scoreboard';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { IcpcScoreboardTable } from './IcpcScoreboardTable';

describe('IcpcScoreboardTable', () => {
  const scoreboard = {
    state: {
      problemJids: ['JIDPROG1', 'JIDPROG2', 'JIDPROG3', 'JIDPROG4'],
      problemAliases: ['A', 'B', 'C', 'D'],
      contestantJids: ['JIDUSER1', 'JIDUSER2'],
    },
    content: {
      entries: [
        {
          rank: 1,
          contestantJid: 'JIDUSER2',
          totalAccepted: 3,
          totalPenalties: 66,
          attemptsList: [1, 3, 1, 0],
          penaltyList: [3, 14, 9, 0],
          problemStateList: [
            IcpcScoreboardProblemState.Accepted,
            IcpcScoreboardProblemState.FirstAccepted,
            IcpcScoreboardProblemState.FirstAccepted,
            IcpcScoreboardProblemState.NotAccepted,
          ],
        },
        {
          rank: 2,
          contestantJid: 'JIDUSER1',
          totalAccepted: 1,
          totalPenalties: 17,
          attemptsList: [1, 1, 0, 3],
          penaltyList: [10, 17, 0, 22],
          problemStateList: [
            IcpcScoreboardProblemState.NotAccepted,
            IcpcScoreboardProblemState.Accepted,
            IcpcScoreboardProblemState.Frozen,
            IcpcScoreboardProblemState.NotAccepted,
          ],
        },
      ],
    },
  };

  const profilesMap = {
    JIDUSER1: { username: 'username1' },
    JIDUSER2: { username: 'username2' },
  };

  beforeEach(() => {
    const props = { scoreboard, profilesMap };
    render(
      <TestRouter>
        <IcpcScoreboardTable {...props} />
      </TestRouter>
    );
  });

  test('ranks', () => {
    const rows = screen.getAllByRole('row').slice(1);
    const ranks = rows.map(row => within(row).getAllByRole('cell')[0].textContent);
    expect(ranks).toEqual(['1', '2']);
  });

  describe('incognito ranks', () => {
    beforeEach(() => {
      cleanup();
      const incognitoEntries = scoreboard.content.entries.map(entry => ({ ...entry, rank: -1 }));
      const incognitoScoreboard = { ...scoreboard, content: { entries: incognitoEntries } };
      const props = { scoreboard: incognitoScoreboard, profilesMap };
      render(
        <TestRouter>
          <IcpcScoreboardTable {...props} />
        </TestRouter>
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
      ['3/66', 'G 1/3', 'D 3/14', 'D 1/9', '-/-'],
      ['1/17', 'R 1/-', 'G 1/17', 'F ?/?', 'R 3/-'],
    ]);
  });

  describe('clicking a submission cell', () => {
    describe('when onClickSubmissionCell is passed', () => {
      const onClickSubmissionCell = vi.fn();

      beforeEach(() => {
        cleanup();
        const props = { scoreboard, profilesMap, onClickSubmissionCell };
        render(
          <TestRouter>
            <IcpcScoreboardTable {...props} />
          </TestRouter>
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

        const firstRowCells = within(rows[0]).getAllByRole('cell');
        await user.click(firstRowCells[6]);

        expect(onClickSubmissionCell).not.toBeCalled();
      });
    });
  });
});
