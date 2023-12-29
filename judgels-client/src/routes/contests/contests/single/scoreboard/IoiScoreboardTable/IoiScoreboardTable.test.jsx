import { mount } from 'enzyme';
import { MemoryRouter } from 'react-router';

import { IoiScoreboardTable } from './IoiScoreboardTable';

describe('IoiScoreboardTable', () => {
  let wrapper;

  const scoreboard = {
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

  const profilesMap = {
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
    const points = wrapper.find('tbody').children().map(mapRow);
    expect(points).toEqual([
      ['100', '70', '30', '0'],
      ['60', '50', '-', '10'],
    ]);
  });

  describe('shows submission as image', () => {
    describe('when canViewSubmissions', () => {
      const contestJid = 'contest-jid';
      const onOpenSubmissionImage = jest.fn();

      beforeEach(() => {
        const props = { scoreboard, profilesMap, contestJid, onOpenSubmissionImage, canViewSubmissions: true };
        wrapper = mount(
          <MemoryRouter>
            <IoiScoreboardTable {...props} />
          </MemoryRouter>
        );
      });

      test('shows submission for attempted cell', () => {
        wrapper.find('tbody').childAt(0).childAt(3).simulate('click');

        expect(onOpenSubmissionImage).toHaveBeenCalledWith(contestJid, 'JIDUSER2', 'JIDPROG1');
      });

      test('does not show submission for unattempted cell', () => {
        wrapper.find('tbody').childAt(1).childAt(4).simulate('click');

        expect(onOpenSubmissionImage).not.toBeCalled();
      });
    });

    describe('when not canViewSubmissions', () => {
      const contestJid = 'contest-jid';
      const onOpenSubmissionImage = jest.fn();

      beforeEach(() => {
        const props = { scoreboard, profilesMap, contestJid, onOpenSubmissionImage, canViewSubmissions: false };
        wrapper = mount(
          <MemoryRouter>
            <IoiScoreboardTable {...props} />
          </MemoryRouter>
        );
      });

      test('does not show submission', () => {
        wrapper.find('tbody').childAt(0).childAt(3).simulate('click');

        expect(onOpenSubmissionImage).not.toBeCalled();
      });
    });
  });
});
