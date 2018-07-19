import { contestClarificationActions } from './contestClarificationActions';
import { contestJid, sessionState, token } from '../../../../../../../../../../fixtures/state';
import {
  ContestClarificationConfig,
  ContestClarificationData,
  ContestClarificationsResponse,
} from '../../../../../../../../../../modules/api/uriel/contestClarification';
import { AppState } from '../../../../../../../../../../modules/store';

describe('contestClarificationActions', () => {
  let dispatch: jest.Mock<any>;
  const getState = (): Partial<AppState> => ({ session: sessionState });

  let contestClarificationAPI: jest.Mocked<any>;
  let toastActions: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();

    contestClarificationAPI = {
      createClarification: jest.fn(),
      getClarificationConfig: jest.fn(),
      getMyClarifications: jest.fn(),
    };
    toastActions = {
      showSuccessToast: jest.fn(),
    };
  });

  describe('createClarification()', () => {
    const data = { title: 'Clarification' } as ContestClarificationData;
    const { createClarification } = contestClarificationActions;
    const doCreateClarification = async () =>
      createClarification(contestJid, data)(dispatch, getState, { contestClarificationAPI, toastActions });

    beforeEach(async () => {
      await doCreateClarification();
    });

    it('calls API to create clarification', () => {
      expect(contestClarificationAPI.createClarification).toHaveBeenCalledWith(token, contestJid, data);
      expect(toastActions.showSuccessToast).toHaveBeenCalledWith('Clarification submitted.');
    });
  });

  describe('getClarificationConfig()', () => {
    const { getClarificationConfig } = contestClarificationActions;
    const doGetClarificationConfig = async () =>
      getClarificationConfig(contestJid, 'id')(dispatch, getState, { contestClarificationAPI });

    beforeEach(async () => {
      const response = {} as ContestClarificationConfig;
      contestClarificationAPI.getClarificationConfig.mockReturnValue(response);

      await doGetClarificationConfig();
    });

    it('calls API to get clarification config', () => {
      expect(contestClarificationAPI.getClarificationConfig).toHaveBeenCalledWith(token, contestJid, 'id');
    });
  });

  describe('getMyClarifications()', () => {
    const { getMyClarifications } = contestClarificationActions;
    const doGetMyClarifications = async () =>
      getMyClarifications(contestJid, 'id')(dispatch, getState, { contestClarificationAPI });

    beforeEach(async () => {
      const response = {} as ContestClarificationsResponse;
      contestClarificationAPI.getMyClarifications.mockReturnValue(response);

      await doGetMyClarifications();
    });

    it('calls API to get my clarifications', () => {
      expect(contestClarificationAPI.getMyClarifications).toHaveBeenCalledWith(token, contestJid, 'id');
    });
  });
});
