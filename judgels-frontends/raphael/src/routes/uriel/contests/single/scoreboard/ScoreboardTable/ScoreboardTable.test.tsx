import { shallow, ShallowWrapper } from 'enzyme';
import * as React from 'react';

import { ScoreboardState } from 'modules/api/uriel/scoreboard';

import { ScoreboardTable, ScoreboardTableProps } from './ScoreboardTable';

describe('ScoreboardTable', () => {
  let wrapper: ShallowWrapper<ScoreboardTableProps>;

  const state: ScoreboardState = {
    problemJids: ['JIDPROG1', 'JIDPROG2', 'JIDPROG3'],
    problemAliases: ['A', 'B', 'C'],
    contestantJids: ['JIDUSER1', 'JIDUSER2'],
    points: [0, 0, 0],
  };

  const className = 'className';

  beforeEach(() => {
    const props = { className, state };
    wrapper = shallow(<ScoreboardTable {...props} />);
  });

  test('header', () => {
    const header = wrapper
      .find('thead')
      .find('tr')
      .first()
      .children()
      .map(th => th.text());
    expect(header).toEqual(['#', 'Contestant', 'Total', 'A', 'B', 'C']);
  });
});
