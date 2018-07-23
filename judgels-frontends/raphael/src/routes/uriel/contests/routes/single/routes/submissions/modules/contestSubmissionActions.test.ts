import { push } from 'react-router-redux';

import { contestSubmissionActions } from './contestSubmissionActions';
import { contestJid, problemJid, sessionState, token } from '../../../../../../../../fixtures/state';
import { SubmissionWithSourceResponse } from '../../../../../../../../modules/api/sandalphon/submission';
import { ContestSubmissionsResponse } from '../../../../../../../../modules/api/uriel/contestSubmission';
import { AppState } from '../../../../../../../../modules/store';
import { ProblemSubmissionFormData } from '../../../../../../../../components/ProblemWorksheetCard/ProblemSubmissionForm/ProblemSubmissionForm';

describe('contestSubmissionActions', () => {
  let dispatch: jest.Mock<any>;
  const getState = (): Partial<AppState> => ({ session: sessionState });

  let contestSubmissionAPI: jest.Mocked<any>;
  let toastActions: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();

    contestSubmissionAPI = {
      getMySubmissions: jest.fn(),
      getSubmissionWithSource: jest.fn(),
      createSubmission: jest.fn(),
    };

    toastActions = {
      showSuccessToast: jest.fn(),
    };
  });

  describe('getMySubmissions()', () => {
    const { getMySubmissions } = contestSubmissionActions;
    const doGetMySubmissions = async () =>
      getMySubmissions(contestJid, 3)(dispatch, getState, { contestSubmissionAPI });

    beforeEach(async () => {
      const submissions = {} as ContestSubmissionsResponse;
      contestSubmissionAPI.getMySubmissions.mockReturnValue(submissions);

      await doGetMySubmissions();
    });

    it('calls API to get my submissions', () => {
      expect(contestSubmissionAPI.getMySubmissions).toHaveBeenCalledWith(token, contestJid, 3);
    });
  });

  describe('getSubmissionWithSource()', () => {
    const { getSubmissionWithSource } = contestSubmissionActions;
    const doGetSubmissionWithSource = async () =>
      getSubmissionWithSource(contestJid, 3, 'id')(dispatch, getState, { contestSubmissionAPI });

    describe('when the contestJid matches', () => {
      beforeEach(async () => {
        const submissionWithSource = {
          data: { submission: { containerJid: contestJid } },
        } as SubmissionWithSourceResponse;
        contestSubmissionAPI.getSubmissionWithSource.mockReturnValue(submissionWithSource);

        await doGetSubmissionWithSource();
      });

      it('calls API to get submission with source', () => {
        expect(contestSubmissionAPI.getSubmissionWithSource).toHaveBeenCalledWith(token, 3, 'id');
      });
    });

    describe('when the contestJid does not match', () => {
      beforeEach(() => {
        const submissionWithSource = {
          data: { submission: { containerJid: 'bogus' } },
        } as SubmissionWithSourceResponse;
        contestSubmissionAPI.getSubmissionWithSource.mockReturnValue(submissionWithSource);
      });

      it('throws not found error', async () => {
        await expect(doGetSubmissionWithSource()).rejects.toMatchObject({});
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
      createSubmission(contestJid, 1, problemJid, data)(dispatch, getState, { contestSubmissionAPI, toastActions });

    beforeEach(async () => {
      await doCreateSubmission();
    });

    it('calls API to create a submission', () => {
      expect(contestSubmissionAPI.createSubmission).toHaveBeenCalledWith(token, contestJid, problemJid, 'Pascal', {
        'sourceFiles.encoder': {} as File,
        'sourceFiles.decoder': {} as File,
      });
      expect(toastActions.showSuccessToast).toHaveBeenCalledWith('Solution submitted.');
      expect(dispatch).toHaveBeenCalledWith(push(`/contests/1/submissions`));
    });
  });
});
