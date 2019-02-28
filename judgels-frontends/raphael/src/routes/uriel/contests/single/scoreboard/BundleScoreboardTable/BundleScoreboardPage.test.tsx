import * as React from 'react';
import { ReactWrapper, mount } from 'enzyme';
import { BundleScoreboardTableProps, BundleScoreboardTable } from './BundleScoreboardPage';
import { BundleScoreboard } from 'modules/api/uriel/scoreboard';
import { ProfilesMap } from 'modules/api/jophiel/profile';
import { MemoryRouter } from 'react-router';

describe('BundleScoreboardTable', () => {
  let wrapper: ReactWrapper<BundleScoreboardTableProps>;
  const scoreboard: BundleScoreboard = {
    state: {
      problemJids: ['JIDBUND1', 'JIDBUND2'],
      problemAliases: ['A_20', 'B_05'],
      contestantJids: ['JIDUSER1', 'JIDUSER2'],
      problemPoints: [20, 5],
    },
    content: {
      entries: [
        {
          rank: 1,
          contestantJid: 'JIDUSER1',
          answeredItems: [12, 3],
          totalAnsweredItems: 15,
        },
        {
          rank: 2,
          contestantJid: 'JIDUSER2',
          answeredItems: [10, 2],
          totalAnsweredItems: 12,
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
        <BundleScoreboardTable {...props} />
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
      const incognitoScoreboard = {
        ...scoreboard,
        content: { entries: incognitoEntries },
      };
      const props = { scoreboard: incognitoScoreboard, profilesMap };
      wrapper = mount(
        <MemoryRouter>
          <BundleScoreboardTable {...props} />
        </MemoryRouter>
      );
    });
  });

  test('display names', () => {
    const ranks = wrapper
      .find('tbody')
      .children()
      .map(tr => tr.childAt(1).text());
    expect(ranks).toEqual(['username1', 'username2']);
  });

  test('display score', () => {
    const mapCell = td => td.text();
    const mapRow = tr => [3, 4].map(x => tr.childAt(x)).map(mapCell);
    const score = wrapper
      .find('tbody')
      .children()
      .map(mapRow);
    expect(score).toEqual([['12/20', '3/5'], ['10/20', '2/5']]);
  });
});
