import { shallow, ShallowWrapper } from 'enzyme';
import * as React from 'react';

import { UsersMap } from '../../../../../../../../../../modules/api/jophiel/user';
import { IoiScoreboard } from '../../../../../../../../../../modules/api/uriel/scoreboard';
import { IoiScoreboardTable, IoiScoreboardTableProps } from './IoiScoreboardTable';

describe('IoiScoreboardTable', () => {
  let wrapper: ShallowWrapper<IoiScoreboardTableProps>;

  const scoreboard: IoiScoreboard = {
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
          scores: [70, 30, 90],
          totalScores: 190,
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

  const usersMap: UsersMap = {
    JIDUSER1: { username: 'username1' },
    JIDUSER2: { username: 'username2' },
  };

  beforeEach(() => {
    const props = { scoreboard, usersMap };
    wrapper = shallow(<IoiScoreboardTable {...props} />);
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

  test('ranks', () => {
    const ranks = wrapper
      .find('tbody')
      .children()
      .map(tr => tr.childAt(0).text());
    expect(ranks).toEqual(['1', '2']);
  });

  test('display names', () => {
    const ranks = wrapper
      .find('tbody')
      .children()
      .map(tr => tr.childAt(1).text());
    expect(ranks).toEqual(['username2', 'username1']);
  });

  test('points', () => {
    const mapCell = td => td.text();
    const mapRow = tr => [2, 3, 4, 5].map(x => tr.childAt(x)).map(mapCell);
    const points = wrapper
      .find('tbody')
      .children()
      .map(mapRow);
    expect(points).toEqual([['190', '70', '30', '90'], ['60', '50', '', '10']]);
  });
});
