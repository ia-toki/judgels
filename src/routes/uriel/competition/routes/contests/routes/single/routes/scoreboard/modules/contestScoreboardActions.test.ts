import { contestScoreboardActions } from './contestScoreboardActions';
import { contestJid } from '../../../../../../../../../../fixtures/state';
import { Scoreboard } from '../../../../../../../../../../modules/api/uriel/scoreboard';

describe('contestScoreboardActions', () => {
  let dispatch: jest.Mock<any>;
  let getState: jest.Mock<any>;

  let contestScoreboardAPI: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();
    getState = jest.fn();

    contestScoreboardAPI = {
      getScoreboard: jest.fn(),
    };
  });

  describe('get()', () => {
    const { get } = contestScoreboardActions;
    const doGet = async () => get(contestJid)(dispatch, getState, { contestScoreboardAPI });

    beforeEach(async () => {
      const scoreboard = {} as Scoreboard;
      contestScoreboardAPI.getScoreboard.mockReturnValue(scoreboard);

      await doGet();
    });

    it('calls API to get contest scoreboard', () => {
      expect(contestScoreboardAPI.getScoreboard).toHaveBeenCalledWith(contestJid);
    });
  });
});
