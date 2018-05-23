import { contestSubmissionActions } from './contestSubmissionActions';
import { contestJid, problemJid, sessionState, token } from '../../../../../../../../../../fixtures/state';
import { SubmissionWithSourceResponse } from '../../../../../../../../../../modules/api/sandalphon/submission';
import { ContestSubmissionsResponse } from '../../../../../../../../../../modules/api/uriel/contestSubmission';
import { AppState } from '../../../../../../../../../../modules/store';
import { ProblemSubmissionFormData } from '../../../../../../../../../../components/ProblemWorksheetCard/ProblemSubmissionForm/ProblemSubmissionForm';

describe('contestSubmissionActions', () => {
  let dispatch: jest.Mock<any>;
  const getState = (): Partial<AppState> => ({ session: sessionState });

  let contestSubmissionAPI: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();

    contestSubmissionAPI = {
      getMySubmissions: jest.fn(),
      getSubmissionWithSource: jest.fn(),
      createSubmission: jest.fn(),
    };
  });

  describe('fetchMyList()', () => {
    const { fetchMyList } = contestSubmissionActions;
    const doFetch = async () => fetchMyList(contestJid, 3)(dispatch, getState, { contestSubmissionAPI });

    beforeEach(async () => {
      const submissions = {} as ContestSubmissionsResponse;
      contestSubmissionAPI.getMySubmissions.mockReturnValue(submissions);

      await doFetch();
    });

    it('calls API to get contest submissions', () => {
      expect(contestSubmissionAPI.getMySubmissions).toHaveBeenCalledWith(token, contestJid, 3);
    });
  });

  describe('fetchWithSource()', () => {
    const { fetchWithSource } = contestSubmissionActions;
    const doFetch = async () => fetchWithSource(contestJid, 3, 'id')(dispatch, getState, { contestSubmissionAPI });

    describe('when the contestJid matches', () => {
      beforeEach(async () => {
        const submissionWithSource = {
          data: { submission: { containerJid: contestJid } },
        } as SubmissionWithSourceResponse;
        contestSubmissionAPI.getSubmissionWithSource.mockReturnValue(submissionWithSource);

        await doFetch();
      });

      it('calls API to get contest submission', () => {
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

      it('calls API to get contest submission', async () => {
        await expect(doFetch()).rejects.toMatchObject({});
      });
    });
  });

  describe('submit()', () => {
    const { submit } = contestSubmissionActions;
    const sourceFiles = {
      encoder: {} as File,
      decoder: {} as File,
    };
    const data: ProblemSubmissionFormData = {
      gradingLanguage: 'Pascal',
      sourceFiles,
    };
    const doSubmit = async () => submit(contestJid, problemJid, data)(dispatch, getState, { contestSubmissionAPI });

    beforeEach(async () => {
      await doSubmit();
    });

    it('calls API to get create a submission', () => {
      expect(contestSubmissionAPI.createSubmission).toHaveBeenCalledWith(
        token,
        contestJid,
        problemJid,
        'Pascal',
        sourceFiles
      );
    });
  });
});
