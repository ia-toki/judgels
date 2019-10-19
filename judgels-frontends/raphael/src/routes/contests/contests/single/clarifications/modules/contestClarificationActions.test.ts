import { contestJid, sessionState, token } from '../../../../../../fixtures/state';
import {
  ContestClarificationData,
  ContestClarificationsResponse,
} from '../../../../../../modules/api/uriel/contestClarification';
import { AppState } from '../../../../../../modules/store';

import { contestClarificationActions } from './contestClarificationActions';

describe('contestClarificationActions', () => {
  let dispatch: jest.Mock<any>;
  const getState = (): Partial<AppState> => ({ session: sessionState });

  let contestClarificationAPI: jest.Mocked<any>;
  let toastActions: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();

    contestClarificationAPI = {
      createClarification: jest.fn(),
      getClarifications: jest.fn(),
      answerClarification: jest.fn(),
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

  describe('getClarifications()', () => {
    const { getClarifications } = contestClarificationActions;
    const doGetMyClarifications = async () =>
      getClarifications(contestJid, 'id', 3)(dispatch, getState, { contestClarificationAPI });

    beforeEach(async () => {
      const response = {} as ContestClarificationsResponse;
      contestClarificationAPI.getClarifications.mockReturnValue(response);

      await doGetMyClarifications();
    });

    it('calls API to get clarifications', () => {
      expect(contestClarificationAPI.getClarifications).toHaveBeenCalledWith(token, contestJid, 'id', 3);
    });
  });

  describe('createClarification()', () => {
    const { answerClarification } = contestClarificationActions;
    const doAnswerClarification = async () =>
      answerClarification(contestJid, 'clarificationJid123', 'Yes.')(dispatch, getState, { contestClarificationAPI });

    beforeEach(async () => {
      await doAnswerClarification();
    });

    it('calls API to answer clarification', () => {
      expect(contestClarificationAPI.answerClarification).toHaveBeenCalledWith(
        token,
        contestJid,
        'clarificationJid123',
        { answer: 'Yes.' }
      );
    });
  });
});
