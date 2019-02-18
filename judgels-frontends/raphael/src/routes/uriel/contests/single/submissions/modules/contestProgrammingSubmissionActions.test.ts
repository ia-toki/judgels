import { push } from 'react-router-redux';

import { contestJid, problemJid, sessionState, token } from 'fixtures/state';
import { NotFoundError } from 'modules/api/error';
import { SubmissionWithSourceResponse } from 'modules/api/sandalphon/submission';
import { ContestSubmissionsResponse } from 'modules/api/uriel/contestProgrammingSubmission';
import { AppState } from 'modules/store';

import { contestProgrammingSubmissionActions } from './contestProgrammingSubmissionActions';
import { ProgrammingProblemSubmissionFormData } from 'components/ProblemWorksheetCard/ProgrammingProblemWorksheetCard/ProgrammingProblemSubmissionForm/ProgrammingProblemSubmissionForm';

describe('contestProgrammingSubmissionActions', () => {
  let dispatch: jest.Mock<any>;
  const getState = (): Partial<AppState> => ({ session: sessionState });

  let contestProgrammingSubmissionAPI: jest.Mocked<any>;
  let toastActions: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();

    contestProgrammingSubmissionAPI = {
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
    const { getSubmissions } = contestProgrammingSubmissionActions;
    const doGetSubmissions = async () =>
      getSubmissions(contestJid, 'userJid', 'problemJid', 3)(dispatch, getState, { contestProgrammingSubmissionAPI });

    beforeEach(async () => {
      const submissions = {} as ContestSubmissionsResponse;
      contestProgrammingSubmissionAPI.getSubmissions.mockReturnValue(submissions);

      await doGetSubmissions();
    });

    it('calls API to get submissions', () => {
      expect(contestProgrammingSubmissionAPI.getSubmissions).toHaveBeenCalledWith(
        token,
        contestJid,
        'userJid',
        'problemJid',
        3
      );
    });
  });

  describe('getSubmissionWithSource()', () => {
    const { getSubmissionWithSource } = contestProgrammingSubmissionActions;
    const doGetSubmissionWithSource = async () =>
      getSubmissionWithSource(contestJid, 3, 'id')(dispatch, getState, { contestProgrammingSubmissionAPI });

    describe('when the contestJid matches', () => {
      beforeEach(async () => {
        const submissionWithSource = {
          data: { submission: { containerJid: contestJid } },
        } as SubmissionWithSourceResponse;
        contestProgrammingSubmissionAPI.getSubmissionWithSource.mockReturnValue(submissionWithSource);

        await doGetSubmissionWithSource();
      });

      it('calls API to get submission with source', () => {
        expect(contestProgrammingSubmissionAPI.getSubmissionWithSource).toHaveBeenCalledWith(token, 3, 'id');
      });
    });

    describe('when the contestJid does not match', () => {
      beforeEach(() => {
        const submissionWithSource = {
          data: { submission: { containerJid: 'bogus' } },
        } as SubmissionWithSourceResponse;
        contestProgrammingSubmissionAPI.getSubmissionWithSource.mockReturnValue(submissionWithSource);
      });

      it('throws not found error', async () => {
        await expect(doGetSubmissionWithSource()).rejects.toBeInstanceOf(NotFoundError);
      });
    });
  });

  describe('createSubmission()', () => {
    const { createSubmission } = contestProgrammingSubmissionActions;
    const sourceFiles = {
      encoder: {} as File,
      decoder: {} as File,
    };
    const data: ProgrammingProblemSubmissionFormData = {
      gradingLanguage: 'Pascal',
      sourceFiles,
    };
    const doCreateSubmission = async () =>
      createSubmission(contestJid, 'contest-a', problemJid, data)(dispatch, getState, {
        contestProgrammingSubmissionAPI,
        toastActions,
      });

    beforeEach(async () => {
      await doCreateSubmission();
    });

    it('calls API to create a submission', () => {
      expect(contestProgrammingSubmissionAPI.createSubmission).toHaveBeenCalledWith(
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
