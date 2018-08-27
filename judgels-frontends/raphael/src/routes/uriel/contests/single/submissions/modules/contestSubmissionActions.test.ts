import { push } from 'react-router-redux';

import { contestJid, problemJid, sessionState, token } from 'fixtures/state';
import { ProblemSubmissionFormData } from 'components/ProblemWorksheetCard/ProblemSubmissionForm/ProblemSubmissionForm';
import { NotFoundError } from 'modules/api/error';
import { SubmissionWithSourceResponse } from 'modules/api/sandalphon/submission';
import { ContestSubmissionsResponse } from 'modules/api/uriel/contestSubmission';
import { AppState } from 'modules/store';

import { contestSubmissionActions } from './contestSubmissionActions';
import { ContestSubmissionConfig } from '../../../../../../modules/api/uriel/contestSubmission';

describe('contestSubmissionActions', () => {
  let dispatch: jest.Mock<any>;
  const getState = (): Partial<AppState> => ({ session: sessionState });

  let contestSubmissionAPI: jest.Mocked<any>;
  let toastActions: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();

    contestSubmissionAPI = {
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
    const doGetSubmissions = async () => getSubmissions(contestJid, 3)(dispatch, getState, { contestSubmissionAPI });

    beforeEach(async () => {
      const submissions = {} as ContestSubmissionsResponse;
      contestSubmissionAPI.getSubmissions.mockReturnValue(submissions);

      await doGetSubmissions();
    });

    it('calls API to get submissions', () => {
      expect(contestSubmissionAPI.getSubmissions).toHaveBeenCalledWith(token, contestJid, 3);
    });
  });

  describe('getSubmissionConfig()', () => {
    const { getSubmissionConfig } = contestSubmissionActions;
    const doGetSubmissionConfig = async () =>
      getSubmissionConfig(contestJid)(dispatch, getState, { contestSubmissionAPI });

    beforeEach(async () => {
      const config = {} as ContestSubmissionConfig;
      contestSubmissionAPI.getSubmissionConfig.mockReturnValue(config);

      await doGetSubmissionConfig();
    });

    it('calls API to get submission config', () => {
      expect(contestSubmissionAPI.getSubmissionConfig).toHaveBeenCalledWith(token, contestJid);
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
        contestSubmissionAPI,
        toastActions,
      });

    beforeEach(async () => {
      await doCreateSubmission();
    });

    it('calls API to create a submission', () => {
      expect(contestSubmissionAPI.createSubmission).toHaveBeenCalledWith(token, contestJid, problemJid, 'Pascal', {
        'sourceFiles.encoder': {} as File,
        'sourceFiles.decoder': {} as File,
      });
      expect(toastActions.showSuccessToast).toHaveBeenCalledWith('Solution submitted.');
      expect(dispatch).toHaveBeenCalledWith(push(`/contests/contest-a/submissions`));
    });
  });
});
