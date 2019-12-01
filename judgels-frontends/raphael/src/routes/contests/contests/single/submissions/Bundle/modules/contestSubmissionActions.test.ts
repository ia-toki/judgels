import { AppState } from '../../../../../../../modules/store';
import { sessionState, contestJid, token } from '../../../../../../../fixtures/state';
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
      getSubmissionSummary: jest.fn(),
      getLatestSubmissions: jest.fn(),
    };

    toastActions = {
      showToast: jest.fn(),
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
    it('calls API to create submission', async () => {
      const action = createItemSubmission('testcontestjid', 'testprobjid', 'testitemjid', 'testans');
      contestSubmissionBundleAPI.createItemSubmission.mockResolvedValue({});
      await action(dispatch, getState, { contestSubmissionBundleAPI, toastActions });
      expect(contestSubmissionBundleAPI.createItemSubmission).toHaveBeenCalledWith(token, {
        containerJid: 'testcontestjid',
        problemJid: 'testprobjid',
        itemJid: 'testitemjid',
        answer: 'testans',
      });
      expect(toastActions.showToast).toHaveBeenCalledWith('Answer saved.');
    });
  });

  describe('getSubmissionSummary()', () => {
    const { getSubmissionSummary } = contestSubmissionActions;
    it('calls API to get summary', async () => {
      const action = getSubmissionSummary('contestjid', 'username', 'language');
      contestSubmissionBundleAPI.getSubmissionSummary.mockResolvedValue({});
      await action(dispatch, getState, { contestSubmissionBundleAPI });
      expect(contestSubmissionBundleAPI.getSubmissionSummary).toHaveBeenCalledWith(
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
      contestSubmissionBundleAPI.getLatestSubmissions.mockResolvedValue({});
      await action(dispatch, getState, { contestSubmissionBundleAPI });
      expect(contestSubmissionBundleAPI.getLatestSubmissions).toHaveBeenCalledWith(token, 'contestjid', 'alias');
    });
  });
});
