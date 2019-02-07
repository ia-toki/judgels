import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { MemoryRouter } from 'react-router';

import { ProfilesMap } from 'modules/api/jophiel/profile';
import { GcjScoreboardProblemState, GcjScoreboard } from 'modules/api/uriel/scoreboard';

import { GcjScoreboardTable, GcjScoreboardTableProps } from './GcjScoreboardTable';

describe('IcpcScoreboardTable', () => {
  let wrapper: ReactWrapper<GcjScoreboardTableProps>;

  const scoreboard: GcjScoreboard = {
    state: {
      problemJids: ['JIDPROG1', 'JIDPROG2', 'JIDPROG3', 'JIDPROG4'],
      problemAliases: ['A', 'B', 'C', 'D'],
      contestantJids: ['JIDUSER1', 'JIDUSER2'],
      points: [1, 10, 100, 1000],
    },
    content: {
      entries: [
        {
          rank: 1,
          contestantJid: 'JIDUSER2',
          totalPoints: 111,
          totalPenalties: 66,
          attemptsList: [1, 3, 0, 0],
          penaltyList: [3, 14, 9, 0],
          problemStateList: [
            GcjScoreboardProblemState.Accepted,
            GcjScoreboardProblemState.Accepted,
            GcjScoreboardProblemState.Accepted,
            GcjScoreboardProblemState.NotAccepted,
          ],
        },
        {
          rank: 2,
          contestantJid: 'JIDUSER1',
          totalPoints: 10,
          totalPenalties: 17,
          attemptsList: [1, 1, 0, 3],
          penaltyList: [10, 17, 0, 22],
          problemStateList: [
            GcjScoreboardProblemState.NotAccepted,
            GcjScoreboardProblemState.Accepted,
            GcjScoreboardProblemState.Frozen,
            GcjScoreboardProblemState.NotAccepted,
          ],
        },
      ],
    },
  };

  const profilesMap: ProfilesMap = {
    JIDUSER1: { username: 'username1' },
    JIDUSER2: { username: 'username2' },
  };

  beforeEach(() => {
    const props = { scoreboard, profilesMap };
    wrapper = mount(
      <MemoryRouter>
        <GcjScoreboardTable {...props} />
      </MemoryRouter>
    );
  });

  test('ranks', () => {
    const ranks = wrapper
      .find('tbody')
      .children()
      .map(tr => tr.childAt(0).text());
    expect(ranks).toEqual(['1', '2']);
  });

  describe('incognito ranks', () => {
    beforeEach(() => {
      const incognitoEntries = scoreboard.content.entries.map(entry => ({ ...entry, rank: -1 }));
      const incognitoScoreboard = { ...scoreboard, content: { entries: incognitoEntries } };
      const props = { scoreboard: incognitoScoreboard, profilesMap };
      wrapper = mount(
        <MemoryRouter>
          <GcjScoreboardTable {...props} />
        </MemoryRouter>
      );
    });

    it('only shows question marks', () => {
      const ranks = wrapper
        .find('tbody')
        .children()
        .map(tr => tr.childAt(0).text());
      expect(ranks).toEqual(['?', '?']);
    });
  });

  test('display names', () => {
    const ranks = wrapper
      .find('tbody')
      .children()
      .map(tr => tr.childAt(1).text());
    expect(ranks).toEqual(['username2', 'username1']);
  });

  test('points', () => {
    const getColor = td =>
      td === undefined ? '' : td === 'accepted' ? 'G ' : td === 'not-accepted' ? 'R ' : td === 'frozen' ? 'F ' : 'X ';
    const mapCell = td => getColor(td.prop('className')) + td.find('strong').text() + '/' + td.find('small').text();
    const mapRow = tr => [2, 3, 4, 5, 6].map(x => tr.childAt(x)).map(mapCell);
    const points = wrapper
      .find('tbody')
      .children()
      .map(mapRow);
    expect(points).toEqual([
      ['111/66', 'G 1/3', 'G 3/14', 'G 0/9', 'X -/-'],
      ['10/17', 'R 1/-', 'G 1/17', 'F -/-', 'R 3/-'],
    ]);
  });
});
