import { contestJid, sessionState, token } from 'fixtures/state';
import { ContestProblemsResponse, ContestProblemWorksheet } from 'modules/api/uriel/contestProblem';
import { AppState } from 'modules/store';

import { contestProblemActions } from './contestProblemActions';

describe('contestProblemActions', () => {
  let dispatch: jest.Mock<any>;
  const getState = (): Partial<AppState> => ({ session: sessionState });

  let contestProblemAPI: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();

    contestProblemAPI = {
      getMyProblems: jest.fn(),
      getProblemWorksheet: jest.fn(),
    };
  });

  describe('getMyProblems()', () => {
    const { getMyProblems } = contestProblemActions;
    const doGetMyProblems = async () => getMyProblems(contestJid)(dispatch, getState, { contestProblemAPI });

    beforeEach(async () => {
      const problems = {} as ContestProblemsResponse;
      contestProblemAPI.getMyProblems.mockReturnValue(problems);

      await doGetMyProblems();
    });

    it('calls API to get my problems', () => {
      expect(contestProblemAPI.getMyProblems).toHaveBeenCalledWith(token, contestJid);
    });
  });

  describe('getProblemWorksheet()', () => {
    const { getProblemWorksheet } = contestProblemActions;
    const doGetProblemWorksheet = async () =>
      getProblemWorksheet(contestJid, 'C', 'id')(dispatch, getState, { contestProblemAPI });

    beforeEach(async () => {
      const worksheet = {} as ContestProblemWorksheet;
      contestProblemAPI.getProblemWorksheet.mockReturnValue(worksheet);

      await doGetProblemWorksheet();
    });

    it('calls API to get problem worksheet', () => {
      expect(contestProblemAPI.getProblemWorksheet).toHaveBeenCalledWith(token, contestJid, 'C', 'id');
    });
  });
});
