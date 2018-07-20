import { shallow, ShallowWrapper } from 'enzyme';
import * as React from 'react';

import { UsersMap } from '../../../../../../../../../../modules/api/jophiel/user';
import { IcpcScoreboardProblemState, IcpcScoreboard } from '../../../../../../../../../../modules/api/uriel/scoreboard';
import { IcpcScoreboardTable, IcpcScoreboardTableProps } from './IcpcScoreboardTable';

describe('IcpcScoreboardTable', () => {
  let wrapper: ShallowWrapper<IcpcScoreboardTableProps>;

  const scoreboard: IcpcScoreboard = {
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
          totalAccepted: 3,
          totalPenalties: 66,
          attemptsList: [1, 3, 1],
          penaltyList: [3, 14, 9],
          problemStateList: [
            IcpcScoreboardProblemState.Accepted,
            IcpcScoreboardProblemState.FirstAccepted,
            IcpcScoreboardProblemState.FirstAccepted,
          ],
        },
        {
          rank: 2,
          contestantJid: 'JIDUSER1',
          totalAccepted: 1,
          totalPenalties: 17,
          attemptsList: [1, 1, 0],
          penaltyList: [10, 17, 0],
          problemStateList: [
            IcpcScoreboardProblemState.NotAccepted,
            IcpcScoreboardProblemState.Accepted,
            IcpcScoreboardProblemState.NotAccepted,
          ],
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
    wrapper = shallow(<IcpcScoreboardTable {...props} />);
  });

  test('ranks', () => {
    const ranks = wrapper
      .find('tbody')
      .children()
      .map(tr => tr.childAt(0).text());
    expect(ranks).toEqual(['1', '2']);
  });

  test('incognito ranks', () => {
    beforeEach(() => {
      const incognitoEntries = scoreboard.content.entries.map(entry => ({ ...entry, rank: -1 }));
      const incognitoScoreboard = { ...scoreboard, content: { entries: incognitoEntries } };
      const props = { scoreboard: incognitoScoreboard, usersMap };
      wrapper = shallow(<IcpcScoreboardTable {...props} />);
    });

    it('only shows question marks', () => {
      const ranks = wrapper
        .find('tbody')
        .children()
        .map(tr => tr.childAt(0).text());
      expect(ranks).toEqual(['?', '?']);
    });
  });

  // TODO(fushar): find better way to verify usernames
  test.skip('display names', () => {
    const ranks = wrapper
      .find('tbody')
      .children()
      .map(tr => tr.childAt(1).text());
    expect(ranks).toEqual(['username2', 'username1']);
  });

  test('points', () => {
    const getColor = td =>
      td === undefined
        ? ''
        : td === 'first-accepted' ? 'D ' : td === 'accepted' ? 'G ' : td === 'not-accepted' ? 'R ' : 'X ';
    const mapCell = td => getColor(td.prop('className')) + td.find('strong').text() + '/' + td.find('small').text();
    const mapRow = tr => [2, 3, 4, 5].map(x => tr.childAt(x)).map(mapCell);
    const points = wrapper
      .find('tbody')
      .children()
      .map(mapRow);
    expect(points).toEqual([['3/66', 'G 1/3', 'D 3/14', 'D 1/9'], ['1/17', 'R 1/-', 'G 1/17', 'X -/-']]);
  });
});
