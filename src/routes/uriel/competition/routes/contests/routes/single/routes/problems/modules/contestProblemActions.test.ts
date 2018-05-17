import { contestProblemActions } from './contestProblemActions';
import { contestJid, sessionState, token } from '../../../../../../../../../../fixtures/state';
import {
  ContestContestantProblemsResponse,
  ContestContestantProblemWorksheet,
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
      getProblemWorksheet: jest.fn(),
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

  describe('fetchWorksheet()', () => {
    const { fetchWorksheet } = contestProblemActions;
    const doFetch = async () => fetchWorksheet(contestJid, 'C')(dispatch, getState, { contestProblemAPI });

    beforeEach(async () => {
      const worksheet = {} as ContestContestantProblemWorksheet;
      contestProblemAPI.getProblemWorksheet.mockReturnValue(worksheet);

      await doFetch();
    });

    it('calls API to get contest problem worksheet', () => {
      expect(contestProblemAPI.getProblemWorksheet).toHaveBeenCalledWith(token, contestJid, 'C');
    });
  });
});
