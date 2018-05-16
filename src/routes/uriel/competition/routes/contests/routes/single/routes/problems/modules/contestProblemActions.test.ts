import { contestProblemActions } from './contestProblemActions';
import { contestJid, sessionState, token } from '../../../../../../../../../../fixtures/state';
import {
  ContestContestantProblemsResponse,
  ContestContestantProblemStatement,
} from '../../../../../../../../../../modules/api/uriel/contestProblem';
import { AppState } from '../../../../../../../../../../modules/store';

describe('contestProblemActions', () => {
  let dispatch: jest.Mock<any>;
  const getState = (): Partial<AppState> => ({ session: sessionState });

  let contestProblemAPI: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();

    contestProblemAPI = {
      getMyProblems: jest.fn(),
      getProblemStatement: jest.fn(),
    };
  });

  describe('fetchMyList()', () => {
    const { fetchMyList } = contestProblemActions;
    const doFetch = async () => fetchMyList(contestJid)(dispatch, getState, { contestProblemAPI });

    beforeEach(async () => {
      const problems = {} as ContestContestantProblemsResponse;
      contestProblemAPI.getMyProblems.mockReturnValue(problems);

      await doFetch();
    });

    it('calls API to get contest problems', () => {
      expect(contestProblemAPI.getMyProblems).toHaveBeenCalledWith(token, contestJid);
    });
  });

  describe('fetchStatement()', () => {
    const { fetchStatement } = contestProblemActions;
    const doFetch = async () => fetchStatement(contestJid, 'C')(dispatch, getState, { contestProblemAPI });

    beforeEach(async () => {
      const statement = {} as ContestContestantProblemStatement;
      contestProblemAPI.getProblemStatement.mockReturnValue(statement);

      await doFetch();
    });

    it('calls API to get contest problem statement', () => {
      expect(contestProblemAPI.getProblemStatement).toHaveBeenCalledWith(token, contestJid, 'C');
    });
  });
});
