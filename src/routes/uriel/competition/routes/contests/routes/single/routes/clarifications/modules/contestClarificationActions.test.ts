import { contestClarificationActions } from './contestClarificationActions';
import { contestJid, sessionState, token } from '../../../../../../../../../../fixtures/state';
import {
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
      getMyClarifications: jest.fn(),
    };
    toastActions = {
      showSuccessToast: jest.fn(),
    };
  });

  describe('create()', () => {
    const data = { title: 'Clarification' } as ContestClarificationData;
    const { create } = contestClarificationActions;
    const doCreate = async () =>
      create(contestJid, data)(dispatch, getState, { contestClarificationAPI, toastActions });

    beforeEach(async () => {
      await doCreate();
    });

    it('calls API to get create contest clarification', () => {
      expect(contestClarificationAPI.createClarification).toHaveBeenCalledWith(token, contestJid, data);
      expect(toastActions.showSuccessToast).toHaveBeenCalledWith('Clarification submitted.');
    });
  });

  describe('fetchMyList()', () => {
    const { fetchMyList } = contestClarificationActions;
    const doFetchMyList = async () => fetchMyList(contestJid, 'id')(dispatch, getState, { contestClarificationAPI });

    beforeEach(async () => {
      const response = {} as ContestClarificationsResponse;
      contestClarificationAPI.getMyClarifications.mockReturnValue(response);

      await doFetchMyList();
    });

    it('calls API to get contest clarifications', () => {
      expect(contestClarificationAPI.getMyClarifications).toHaveBeenCalledWith(token, contestJid, 'id');
    });
  });
});
