import { mount } from 'enzyme';
import { MemoryRouter } from 'react-router';

import { BundleScoreboardTable } from './BundleScoreboardTable';

describe('BundleScoreboardTable', () => {
  let wrapper;
  const scoreboard = {
    state: {
      problemJids: ['JIDBUND1', 'JIDBUND2'],
      problemAliases: ['A', 'B'],
      contestantJids: ['JIDUSER1', 'JIDUSER2'],
    },
    content: {
      entries: [
        {
          rank: 1,
          contestantJid: 'JIDUSER1',
          scores: [12, 3],
          totalScores: 15,
        },
        {
          rank: 2,
          contestantJid: 'JIDUSER2',
          scores: [10, 2],
          totalScores: 12,
        },
      ],
    },
  };

  const profilesMap = {
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
    expect(ranks).toEqual(['username1', 'username2']);
  });

  test('display score', () => {
    const mapCell = td => td.text();
    const mapRow = tr => [3, 4].map(x => tr.childAt(x)).map(mapCell);
    const score = wrapper.find('tbody').children().map(mapRow);
    expect(score).toEqual([
      ['12', '3'],
      ['10', '2'],
    ]);
  });

  test('display points', () => {
    const mapCell = td => td.text();
    const mapRow = tr => [3, 4].map(x => tr.childAt(x)).map(mapCell);
    const points = wrapper.find('thead').children().map(mapRow);
    expect(points).toEqual([['A', 'B']]);
  });
});
