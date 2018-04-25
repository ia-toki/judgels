import { contestScoreboardActions } from './contestScoreboardActions';
import { contestJid, sessionState, token } from '../../../../../../../../../../fixtures/state';
import { ContestScoreboardResponse } from '../../../../../../../../../../modules/api/uriel/contestScoreboard';
import { AppState } from '../../../../../../../../../../modules/store';

describe('contestScoreboardActions', () => {
  let dispatch: jest.Mock<any>;
  const getState = (): Partial<AppState> => ({ session: sessionState });

  let contestScoreboardAPI: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();

    contestScoreboardAPI = {
      getScoreboard: jest.fn(),
    };
  });

  describe('get()', () => {
    const { get } = contestScoreboardActions;
    const doGet = async () => get(contestJid)(dispatch, getState, { contestScoreboardAPI });

    beforeEach(async () => {
      const scoreboard = {} as ContestScoreboardResponse;
      contestScoreboardAPI.getScoreboard.mockReturnValue(scoreboard);

      await doGet();
    });

    it('calls API to get contest scoreboard', () => {
      expect(contestScoreboardAPI.getScoreboard).toHaveBeenCalledWith(token, contestJid);
    });
  });
});
