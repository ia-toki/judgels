import { mount } from 'enzyme';
import { MemoryRouter } from 'react-router';

import { TrocScoreboardProblemState } from '../../../../../../modules/api/uriel/scoreboard';
import { TrocScoreboardTable } from './TrocScoreboardTable';

describe('TrocScoreboardTable', () => {
  let wrapper;

  const scoreboard = {
    state: {
      problemJids: ['JIDPROG1', 'JIDPROG2', 'JIDPROG3', 'JIDPROG4'],
      problemAliases: ['A', 'B', 'C', 'D'],
      contestantJids: ['JIDUSER1', 'JIDUSER2'],
      problemPoints: [1, 10, 100, 1000],
    },
    content: {
      entries: [
        {
          rank: 1,
          contestantJid: 'JIDUSER2',
          totalPoints: 111,
          totalPenalties: 66,
          attemptsList: [2, 4, 1, 0],
          penaltyList: [3, 14, 69, 0],
          problemStateList: [
            TrocScoreboardProblemState.Accepted,
            TrocScoreboardProblemState.FirstAccepted,
            TrocScoreboardProblemState.FirstAccepted,
            TrocScoreboardProblemState.NotAccepted,
          ],
        },
        {
          rank: 2,
          contestantJid: 'JIDUSER1',
          totalPoints: 10,
          totalPenalties: 17,
          attemptsList: [1, 2, 0, 3],
          penaltyList: [10, 17, 0, 22],
          problemStateList: [
            TrocScoreboardProblemState.NotAccepted,
            TrocScoreboardProblemState.Accepted,
            TrocScoreboardProblemState.Frozen,
            TrocScoreboardProblemState.NotAccepted,
          ],
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
        <TrocScoreboardTable {...props} />
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
          <TrocScoreboardTable {...props} />
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
        : td === 'accepted'
        ? 'G '
        : td === 'not-accepted'
        ? 'R '
        : td === 'frozen'
        ? 'F '
        : 'X ';
    const mapCell = td =>
      getColor(td.prop('className')) + td.find('span.top').text() + '/' + td.find('span.bottom').text();
    const mapRow = tr => [2, 3, 4, 5, 6].map(x => tr.childAt(x)).map(mapCell);
    const points = wrapper
      .find('tbody')
      .children()
      .map(mapRow);
    expect(points).toEqual([
      ['111/01:06', 'G +1/00:03', 'D +3/00:14', 'D +/01:09', 'X -/-'],
      ['10/00:17', 'R +1/-', 'G +1/00:17', 'F -/-', 'R +3/-'],
    ]);
  });
});
