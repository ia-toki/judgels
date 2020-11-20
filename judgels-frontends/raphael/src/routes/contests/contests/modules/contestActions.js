import { push } from 'connected-react-router';
import { SubmissionError } from 'redux-form';

import { BadRequestError } from '../../../../modules/api/error';
import { contestAPI, ContestErrors } from '../../../../modules/api/uriel/contest';
import { selectToken } from '../../../../modules/session/sessionSelectors';
import { DelContest, EditContest, PutContest } from './contestReducer';
import * as toastActions from '../../../../modules/toast/toastActions';

export function createContest(data) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    try {
      await contestAPI.createContest(token, data);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === ContestErrors.SlugAlreadyExists) {
        throw new SubmissionError({ slug: 'Slug already exists' });
      }
      throw error;
    }
    dispatch(push(`/contests/${data.slug}`));
    dispatch(EditContest(true));
    toastActions.showSuccessToast('Contest created.');
  };
}

export function updateContest(contestJid, contestSlug, data) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    try {
      await contestAPI.updateContest(token, contestJid, data);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === ContestErrors.SlugAlreadyExists) {
        throw new SubmissionError({ slug: 'Slug already exists' });
      }
      throw error;
    }
    toastActions.showSuccessToast('Contest updated.');

    if (data.slug && data.slug !== contestSlug) {
      dispatch(push(`/contests/${data.slug}`));
    }
  };
}

export function getContests(name, page) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestAPI.getContests(token, name, page);
  };
}

export function getActiveContests() {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestAPI.getActiveContests(token);
  };
}

export function getContestBySlug(contestSlug) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const contest = await contestAPI.getContestBySlug(token, contestSlug);
    dispatch(PutContest(contest));
    return contest;
  };
}

export function startVirtualContest(contestId) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await contestAPI.startVirtualContest(token, contestId);
  };
}

export function resetVirtualContest(contestId) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await contestAPI.resetVirtualContest(token, contestId);
    toastActions.showSuccessToast('All contestant virtual start time has been reset.');
  };
}

export function getContestDescription(contestJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const { description } = await contestAPI.getContestDescription(token, contestJid);
    return description;
  };
}

export function updateContestDescription(contestJid, description) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await contestAPI.updateContestDescription(token, contestJid, { description });
    toastActions.showSuccessToast('Description updated.');
  };
}

export const clearContest = DelContest;
