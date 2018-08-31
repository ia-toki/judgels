import { push } from 'react-router-redux';
import { SubmissionError } from 'redux-form';

import { BadRequestError } from 'modules/api/error';
import { ContestErrors, ContestPage } from 'modules/api/uriel/contest';
import { AppState } from 'modules/store';
import { contest, contestId, sessionState, token, contestJid } from 'fixtures/state';

import { contestActions } from './contestActions';
import { EditContest, PutContest } from './contestReducer';

describe('contestActions', () => {
  let dispatch: jest.Mock<any>;
  const getState = (): Partial<AppState> => ({ session: sessionState });

  let contestAPI: jest.Mocked<any>;
  let toastActions: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();

    contestAPI = {
      createContest: jest.fn(),
      getActiveContests: jest.fn(),
      getContests: jest.fn(),
      getContestBySlug: jest.fn(),
      startVirtualContest: jest.fn(),
      getContestDescription: jest.fn(),
    };

    toastActions = {
      showSuccessToast: jest.fn(),
    };
  });

  describe('createContest()', () => {
    const { createContest } = contestActions;
    const doCreateContest = async () =>
      createContest({ slug: 'new-contest' })(dispatch, getState, { contestAPI, toastActions });

    describe('when the slug does not already exist', () => {
      beforeEach(async () => {
        contestAPI.createContest.mockReturnValue(Promise.resolve({}));
        await doCreateContest();
      });

      it('calls API to create contest', () => {
        expect(contestAPI.createContest).toHaveBeenCalledWith(token, { slug: 'new-contest' });
      });

      it('pushes the history to the contest page', () => {
        expect(dispatch).toHaveBeenCalledWith(push('/contests/new-contest'));
      });

      it('immediately opens the edit dialog', () => {
        expect(dispatch).toHaveBeenCalledWith(EditContest.create(true));
      });

      it('shows the success toast', () => {
        expect(toastActions.showSuccessToast).toHaveBeenCalledWith('Contest created.');
      });
    });

    describe('when the slug already exists', () => {
      beforeEach(() => {
        contestAPI.createContest.mockImplementation(() => {
          throw new BadRequestError({ errorName: ContestErrors.SlugAlreadyExists });
        });
      });

      it('throws SubmissionError', async () => {
        await expect(doCreateContest()).rejects.toEqual(new SubmissionError({ slug: ContestErrors.SlugAlreadyExists }));
      });
    });
  });

  describe('getContests()', () => {
    const { getContests } = contestActions;
    const doGetContests = async () => getContests(2, 20)(dispatch, getState, { contestAPI });

    beforeEach(async () => {
      const contestPage: ContestPage = {
        totalData: 3,
        data: [],
      };
      contestAPI.getContests.mockImplementation(() => contestPage);

      await doGetContests();
    });

    it('calls API to get contests', () => {
      expect(contestAPI.getContests).toHaveBeenCalledWith(token, 2, 20);
    });
  });

  describe('getContestBySlug()', () => {
    const { getContestBySlug } = contestActions;
    const doGetContestBySlug = async () => getContestBySlug('ioi')(dispatch, getState, { contestAPI });

    beforeEach(async () => {
      contestAPI.getContestBySlug.mockImplementation(() => contest);

      await doGetContestBySlug();
    });

    it('calls API to get contest', () => {
      expect(contestAPI.getContestBySlug).toHaveBeenCalledWith(token, 'ioi');
    });

    it('puts the contest', () => {
      expect(dispatch).toHaveBeenCalledWith(PutContest.create(contest));
    });
  });

  describe('startVirtualContest()', () => {
    const { startVirtualContest } = contestActions;
    const doStartVirtualContest = async () => startVirtualContest(contestId)(dispatch, getState, { contestAPI });

    beforeEach(async () => {
      await doStartVirtualContest();
    });

    it('calls API to start virtual contest', () => {
      expect(contestAPI.startVirtualContest).toHaveBeenCalledWith(token, contestId);
    });
  });

  describe('getContestDescription()', () => {
    const { getContestDescription } = contestActions;
    const doGetContestDescription = async () => getContestDescription(contestJid)(dispatch, getState, { contestAPI });

    beforeEach(async () => {
      contestAPI.getContestDescription.mockImplementation(() => contest);

      await doGetContestDescription();
    });

    it('calls API to get contest description', () => {
      expect(contestAPI.getContestDescription).toHaveBeenCalledWith(token, contestJid);
    });
  });
});
