import { contestJid, sessionState, token } from 'fixtures/state';
import { ContestScoreboardResponse } from 'modules/api/uriel/contestScoreboard';
import { AppState } from 'modules/store';

import { contestScoreboardActions } from './contestScoreboardActions';

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

  describe('getScoreboard()', () => {
    const { getScoreboard } = contestScoreboardActions;
    const doGetScoreboard = async () =>
      getScoreboard(contestJid, true, false)(dispatch, getState, { contestScoreboardAPI });

    beforeEach(async () => {
      const scoreboard = {} as ContestScoreboardResponse;
      contestScoreboardAPI.getScoreboard.mockReturnValue(scoreboard);

      await doGetScoreboard();
    });

    it('calls API to get scoreboard', () => {
      expect(contestScoreboardAPI.getScoreboard).toHaveBeenCalledWith(token, contestJid, true, false);
    });
  });
});
