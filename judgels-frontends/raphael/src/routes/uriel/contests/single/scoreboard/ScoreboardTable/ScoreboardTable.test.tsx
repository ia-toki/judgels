import { shallow, ShallowWrapper } from 'enzyme';
import * as React from 'react';

import { ScoreboardState } from 'modules/api/uriel/scoreboard';

import { ScoreboardTable, ScoreboardTableProps } from './ScoreboardTable';

describe('ScoreboardTable', () => {
  let wrapper: ShallowWrapper<ScoreboardTableProps>;
  let problemPoints;

  const className = 'className';

  const render = () => {
    const state: ScoreboardState = {
      problemJids: ['JIDPROG1', 'JIDPROG2', 'JIDPROG3'],
      problemAliases: ['A', 'B', 'C'],
      contestantJids: ['JIDUSER1', 'JIDUSER2'],
      problemPoints,
    };

    const props = { className, state };
    wrapper = shallow(<ScoreboardTable {...props} />);
  };

  describe('header', () => {
    describe('without points', () => {
      beforeEach(() => render());

      it('does not display the points', () => {
        const header = wrapper
          .find('thead')
          .find('tr')
          .first()
          .children()
          .map(th => th.text());
        expect(header).toEqual(['#', 'Contestant', 'Total', 'A', 'B', 'C']);
      });
    });

    describe('points', () => {
      beforeEach(() => {
        problemPoints = [10, 0, 30];
        render();
      });

      it('displays the points', () => {
        const header = wrapper
          .find('thead')
          .find('tr')
          .first()
          .children()
          .map(th => th.text());
        expect(header).toEqual(['#', 'Contestant', 'Total', 'A[10]', 'B[0]', 'C[30]']);
      });
    });
  });
});
