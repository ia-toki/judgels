import { render, screen } from '@testing-library/react';

import { ScoreboardTable } from './ScoreboardTable';

describe('ScoreboardTable', () => {
  const renderComponent = ({ problemPoints } = {}) => {
    const state = {
      problemJids: ['JIDPROG1', 'JIDPROG2', 'JIDPROG3'],
      problemAliases: ['A', 'B', 'C'],
      contestantJids: ['JIDUSER1', 'JIDUSER2'],
      problemPoints,
    };

    render(<ScoreboardTable className="className" state={state} />);
  };

  test('displays header without points', () => {
    renderComponent();
    const headerCells = screen.getAllByRole('columnheader');
    const header = headerCells.map(th => th.textContent);
    expect(header).toEqual(['#', 'Contestant', 'Total', 'A', 'B', 'C']);
  });

  test('displays header with points', () => {
    renderComponent({ problemPoints: [10, 0, 30] });
    const headerCells = screen.getAllByRole('columnheader');
    const header = headerCells.map(th => th.textContent);
    expect(header).toEqual(['#', 'Contestant', 'Total', 'A10', 'B0', 'C30']);
  });
});
