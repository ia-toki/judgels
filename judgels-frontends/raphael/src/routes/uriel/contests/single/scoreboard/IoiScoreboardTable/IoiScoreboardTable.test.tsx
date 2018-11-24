import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { MemoryRouter } from 'react-router';

import { ProfilesMap } from 'modules/api/jophiel/profile';
import { IoiScoreboard } from 'modules/api/uriel/scoreboard';

import { IoiScoreboardTable, IoiScoreboardTableProps } from './IoiScoreboardTable';

describe('IoiScoreboardTable', () => {
  let wrapper: ReactWrapper<IoiScoreboardTableProps>;

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

  const profilesMap: ProfilesMap = {
    JIDUSER1: { username: 'username1' },
    JIDUSER2: { username: 'username2' },
  };

  beforeEach(() => {
    const props = { scoreboard, profilesMap };
    wrapper = mount(
      <MemoryRouter>
        <IoiScoreboardTable {...props} />
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
          <IoiScoreboardTable {...props} />
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
    const mapCell = td => td.text();
    const mapRow = tr => [2, 3, 4, 5].map(x => tr.childAt(x)).map(mapCell);
    const points = wrapper
      .find('tbody')
      .children()
      .map(mapRow);
    expect(points).toEqual([['100', '70', '30', '0'], ['60', '50', '-', '10']]);
  });
});
