import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { MemoryRouter } from 'react-router';

import { ProfilesMap } from '../../../../../../modules/api/jophiel/profile';
import { IcpcScoreboardProblemState, IcpcScoreboard } from '../../../../../../modules/api/uriel/scoreboard';

import { IcpcScoreboardTable, IcpcScoreboardTableProps } from './IcpcScoreboardTable';

describe('IcpcScoreboardTable', () => {
  let wrapper: ReactWrapper<IcpcScoreboardTableProps>;

  const scoreboard: IcpcScoreboard = {
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

  const profilesMap: ProfilesMap = {
    JIDUSER1: { username: 'username1' },
    JIDUSER2: { username: 'username2' },
  };

  beforeEach(() => {
    const props = { scoreboard, profilesMap };
    wrapper = mount(
      <MemoryRouter>
        <IcpcScoreboardTable {...props} />
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
          <IcpcScoreboardTable {...props} />
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
      td === undefined
        ? ''
        : td === 'first-accepted'
          ? 'D '
          : td === 'accepted' ? 'G ' : td === 'not-accepted' ? 'R ' : td === 'frozen' ? 'F ' : 'X ';
    const mapCell = td => getColor(td.prop('className')) + td.find('strong').text() + '/' + td.find('small').text();
    const mapRow = tr => [2, 3, 4, 5, 6].map(x => tr.childAt(x)).map(mapCell);
    const points = wrapper
      .find('tbody')
      .children()
      .map(mapRow);
    expect(points).toEqual([
      ['3/66', 'G 1/3', 'D 3/14', 'D 1/9', 'X -/-'],
      ['1/17', 'R 1/-', 'G 1/17', 'F ?/?', 'R 3/-'],
    ]);
  });
});
