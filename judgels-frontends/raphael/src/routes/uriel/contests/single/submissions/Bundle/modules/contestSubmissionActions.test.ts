import { AppState } from 'modules/store';
import { sessionState, contestJid, token } from 'fixtures/state';
import { contestSubmissionActions } from './contestSubmissionActions';

describe('bundle contestSubmissionActions', () => {
  let dispatch: jest.Mock<any>;
  const getState = (): Partial<AppState> => ({ session: sessionState });

  let contestSubmissionBundleAPI: jest.Mocked<any>;
  let toastActions: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();

    contestSubmissionBundleAPI = {
      getSubmissions: jest.fn(),
      createItemSubmission: jest.fn(),
      getAnswerSummaryForContestant: jest.fn(),
      getLatestSubmissionsByUserForProblemInContest: jest.fn(),
    };

    toastActions = {
      showSuccessToast: jest.fn(),
    };
  });

  describe('getSubmissions()', () => {
    const { getSubmissions } = contestSubmissionActions;
    it('calls API to get bundle submissions', async () => {
      const action = getSubmissions(contestJid, 'username', 'alias', 3);
      contestSubmissionBundleAPI.getSubmissions.mockReturnValue({});
      await expect(action(dispatch, getState, { contestSubmissionBundleAPI })).resolves.toBeDefined();
      expect(contestSubmissionBundleAPI.getSubmissions).toHaveBeenCalledWith(token, contestJid, 'username', 'alias', 3);
    });
  });

  describe('createItemSubmission()', () => {
    const { createItemSubmission } = contestSubmissionActions;
    it('calls API to create submission, and show toast when succeded', async () => {
      const action = createItemSubmission('testcontestjid', 'testprobjid', 'testitemjid', 'testans');
      contestSubmissionBundleAPI.createItemSubmission.mockResolvedValue({});
      await action(dispatch, getState, { contestSubmissionBundleAPI, toastActions });
      expect(contestSubmissionBundleAPI.createItemSubmission).toHaveBeenCalledWith(token, {
        contestJid: 'testcontestjid',
        problemJid: 'testprobjid',
        itemJid: 'testitemjid',
        answer: 'testans',
      });
      expect(toastActions.showSuccessToast).toHaveBeenCalledTimes(1);
    });

    it('calls API to create submission, and not show toast when failed', async () => {
      const action = createItemSubmission('testcontestjid', 'testprobjid', 'testitemjid', 'testans');
      contestSubmissionBundleAPI.createItemSubmission.mockRejectedValue({});
      expect(action(dispatch, getState, { contestSubmissionBundleAPI, toastActions })).rejects.toBeDefined();
      expect(contestSubmissionBundleAPI.createItemSubmission).toHaveBeenCalledWith(token, {
        contestJid: 'testcontestjid',
        problemJid: 'testprobjid',
        itemJid: 'testitemjid',
        answer: 'testans',
      });
      expect(toastActions.showSuccessToast).not.toHaveBeenCalled();
    });
  });

  describe('getSummary()', () => {
    const { getSummary } = contestSubmissionActions;
    it('calls API to get summary', async () => {
      const action = getSummary('contestjid', 'username', 'language');
      contestSubmissionBundleAPI.getAnswerSummaryForContestant.mockResolvedValue({});
      await action(dispatch, getState, { contestSubmissionBundleAPI });
      expect(contestSubmissionBundleAPI.getAnswerSummaryForContestant).toHaveBeenCalledWith(
        token,
        'contestjid',
        'username',
        'language'
      );
    });
  });

  describe('getLatestSubmissions()', () => {
    const { getLatestSubmissions } = contestSubmissionActions;
    it('calls API to get latest submissions', async () => {
      const action = getLatestSubmissions('contestjid', 'alias');
      contestSubmissionBundleAPI.getLatestSubmissionsByUserForProblemInContest.mockResolvedValue({});
      await action(dispatch, getState, { contestSubmissionBundleAPI });
      expect(contestSubmissionBundleAPI.getLatestSubmissionsByUserForProblemInContest).toHaveBeenCalledWith(
        token,
        'contestjid',
        'alias'
      );
    });
  });
});
