import { contestClarificationActions } from './contestClarificationActions';
import { contestJid, sessionState, token } from '../../../../../../../../../../fixtures/state';
import { ContestClarificationsResponse } from '../../../../../../../../../../modules/api/uriel/contestClarification';
import { AppState } from '../../../../../../../../../../modules/store';

describe('contestClarificationActions', () => {
  let dispatch: jest.Mock<any>;
  const getState = (): Partial<AppState> => ({ session: sessionState });

  let contestClarificationAPI: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();

    contestClarificationAPI = {
      getMyClarifications: jest.fn(),
    };
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
