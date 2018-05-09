import { contestSubmissionActions } from './contestSubmissionActions';
import { contestJid, sessionState, token } from '../../../../../../../../../../fixtures/state';
import { ContestSubmissionsResponse } from '../../../../../../../../../../modules/api/uriel/contestSubmission';
import { AppState } from '../../../../../../../../../../modules/store';

describe('contestSubmissionActions', () => {
  let dispatch: jest.Mock<any>;
  const getState = (): Partial<AppState> => ({ session: sessionState });

  let contestSubmissionAPI: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();

    contestSubmissionAPI = {
      getMySubmissions: jest.fn(),
    };
  });

  describe('fetchMyList()', () => {
    const { fetchMyList } = contestSubmissionActions;
    const doFetch = async () => fetchMyList(contestJid, 3)(dispatch, getState, { contestSubmissionAPI });

    beforeEach(async () => {
      const submissions = {} as ContestSubmissionsResponse;
      contestSubmissionAPI.getMySubmissions.mockReturnValue(submissions);

      await doFetch();
    });

    it('calls API to get contest submissions', () => {
      expect(contestSubmissionAPI.getMySubmissions).toHaveBeenCalledWith(token, contestJid, 3);
    });
  });
});
