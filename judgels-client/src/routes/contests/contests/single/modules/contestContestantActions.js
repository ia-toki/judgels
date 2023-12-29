import { contestContestantAPI } from '../../../../../modules/api/uriel/contestContestant';
import { selectToken } from '../../../../../modules/session/sessionSelectors';

import * as toastActions from '../../../../../modules/toast/toastActions';

export function getMyContestantState(contestJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestContestantAPI.getMyContestantState(token, contestJid);
  };
}

export function getContestants(contestJid, page) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestContestantAPI.getContestants(token, contestJid, page);
  };
}

export function getApprovedContestantsCount(contestJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestContestantAPI.getApprovedContestantsCount(token, contestJid);
  };
}

export function getApprovedContestants(contestJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestContestantAPI.getApprovedContestants(token, contestJid);
  };
}

export function registerMyselfAsContestant(contestJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await contestContestantAPI.registerMyselfAsContestant(token, contestJid);
    toastActions.showSuccessToast('Successfully registered to the contest.');
  };
}

export function unregisterMyselfAsContestant(contestJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await contestContestantAPI.unregisterMyselfAsContestant(token, contestJid);
    toastActions.showSuccessToast('Successfully unregistered from the contest.');
  };
}

export function upsertContestants(contestJid, usernames) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const response = await contestContestantAPI.upsertContestants(token, contestJid, usernames);
    if (Object.keys(response.insertedContestantProfilesMap).length === usernames.length) {
      toastActions.showSuccessToast('Contestants added.');
    }
    return response;
  };
}

export function deleteContestants(contestJid, usernames) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const response = await contestContestantAPI.deleteContestants(token, contestJid, usernames);
    if (Object.keys(response.deletedContestantProfilesMap).length === usernames.length) {
      toastActions.showSuccessToast('Contestants removed.');
    }
    return response;
  };
}
