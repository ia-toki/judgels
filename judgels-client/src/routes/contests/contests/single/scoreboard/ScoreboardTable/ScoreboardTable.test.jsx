import { render, screen } from '@testing-library/react';

import { ScoreboardTable } from './ScoreboardTable';

describe('ScoreboardTable', () => {
  let problemPoints;

  const className = 'className';

  const renderComponent = () => {
    const state = {
      problemJids: ['JIDPROG1', 'JIDPROG2', 'JIDPROG3'],
      problemAliases: ['A', 'B', 'C'],
      contestantJids: ['JIDUSER1', 'JIDUSER2'],
      problemPoints,
    };

    const props = { className, state };
    render(<ScoreboardTable {...props} />);
  };

  describe('header', () => {
    describe('without points', () => {
      beforeEach(() => renderComponent());

      it('does not display the points', () => {
        const headerCells = screen.getAllByRole('columnheader');
        const header = headerCells.map(th => th.textContent);
        expect(header).toEqual(['#', 'Contestant', 'Total', 'A', 'B', 'C']);
      });
    });

    describe('points', () => {
      beforeEach(() => {
        problemPoints = [10, 0, 30];
        renderComponent();
      });

      it('displays the points', () => {
        const headerCells = screen.getAllByRole('columnheader');
        const header = headerCells.map(th => th.textContent);
        expect(header).toEqual(['#', 'Contestant', 'Total', 'A10', 'B0', 'C30']);
      });
    });
  });
});
