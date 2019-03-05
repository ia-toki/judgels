import { push } from 'react-router-redux';

import { contestJid, problemJid, sessionState, token } from 'fixtures/state';
import { NotFoundError } from 'modules/api/error';
import { SubmissionWithSourceResponse } from 'modules/api/sandalphon/submissionProgramming';
import { ContestSubmissionsResponse } from 'modules/api/uriel/contestSubmissionProgramming';
import { AppState } from 'modules/store';

import { contestSubmissionActions } from './contestSubmissionActions';
import { ProblemSubmissionFormData } from 'components/ProblemWorksheetCard/Programming/ProblemSubmissionForm/ProblemSubmissionForm';

describe('contestSubmissionActions', () => {
  let dispatch: jest.Mock<any>;
  const getState = (): Partial<AppState> => ({ session: sessionState });

  let contestSubmissionProgrammingAPI: jest.Mocked<any>;
  let toastActions: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();

    contestSubmissionProgrammingAPI = {
      getSubmissions: jest.fn(),
      getSubmissionConfig: jest.fn(),
      getSubmissionWithSource: jest.fn(),
      createSubmission: jest.fn(),
    };

    toastActions = {
      showSuccessToast: jest.fn(),
    };
  });

  describe('getSubmissions()', () => {
    const { getSubmissions } = contestSubmissionActions;
    const doGetSubmissions = async () =>
      getSubmissions(contestJid, 'userJid', 'problemJid', 3)(dispatch, getState, { contestSubmissionProgrammingAPI });

    beforeEach(async () => {
      const submissions = {} as ContestSubmissionsResponse;
      contestSubmissionProgrammingAPI.getSubmissions.mockReturnValue(submissions);

      await doGetSubmissions();
    });

    it('calls API to get submissions', () => {
      expect(contestSubmissionProgrammingAPI.getSubmissions).toHaveBeenCalledWith(
        token,
        contestJid,
        'userJid',
        'problemJid',
        3
      );
    });
  });

  describe('getSubmissionWithSource()', () => {
    const { getSubmissionWithSource } = contestSubmissionActions;
    const doGetSubmissionWithSource = async () =>
      getSubmissionWithSource(contestJid, 3, 'id')(dispatch, getState, { contestSubmissionProgrammingAPI });

    describe('when the contestJid matches', () => {
      beforeEach(async () => {
        const submissionWithSource = {
          data: { submission: { containerJid: contestJid } },
        } as SubmissionWithSourceResponse;
        contestSubmissionProgrammingAPI.getSubmissionWithSource.mockReturnValue(submissionWithSource);

        await doGetSubmissionWithSource();
      });

      it('calls API to get submission with source', () => {
        expect(contestSubmissionProgrammingAPI.getSubmissionWithSource).toHaveBeenCalledWith(token, 3, 'id');
      });
    });

    describe('when the contestJid does not match', () => {
      beforeEach(() => {
        const submissionWithSource = {
          data: { submission: { containerJid: 'bogus' } },
        } as SubmissionWithSourceResponse;
        contestSubmissionProgrammingAPI.getSubmissionWithSource.mockReturnValue(submissionWithSource);
      });

      it('throws not found error', async () => {
        await expect(doGetSubmissionWithSource()).rejects.toBeInstanceOf(NotFoundError);
      });
    });
  });

  describe('createSubmission()', () => {
    const { createSubmission } = contestSubmissionActions;
    const sourceFiles = {
      encoder: {} as File,
      decoder: {} as File,
    };
    const data: ProblemSubmissionFormData = {
      gradingLanguage: 'Pascal',
      sourceFiles,
    };
    const doCreateSubmission = async () =>
      createSubmission(contestJid, 'contest-a', problemJid, data)(dispatch, getState, {
        contestSubmissionProgrammingAPI,
        toastActions,
      });

    beforeEach(async () => {
      await doCreateSubmission();
    });

    it('calls API to create a submission', () => {
      expect(contestSubmissionProgrammingAPI.createSubmission).toHaveBeenCalledWith(
        token,
        contestJid,
        problemJid,
        'Pascal',
        {
          'sourceFiles.encoder': {} as File,
          'sourceFiles.decoder': {} as File,
        }
      );
      expect(toastActions.showSuccessToast).toHaveBeenCalledWith('Solution submitted.');
      expect(dispatch).toHaveBeenCalledWith(push(`/contests/contest-a/submissions`));
    });
  });
});
