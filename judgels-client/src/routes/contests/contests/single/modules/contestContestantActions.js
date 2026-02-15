import { contestContestantAPI } from '../../../../../modules/api/uriel/contestContestant';
import { getToken } from '../../../../../modules/session';

import * as toastActions from '../../../../../modules/toast/toastActions';

export async function getMyContestantState(contestJid) {
  const token = getToken();
  return await contestContestantAPI.getMyContestantState(token, contestJid);
}

export async function getContestants(contestJid, page) {
  const token = getToken();
  return await contestContestantAPI.getContestants(token, contestJid, page);
}

export async function getApprovedContestantsCount(contestJid) {
  const token = getToken();
  return await contestContestantAPI.getApprovedContestantsCount(token, contestJid);
}

export async function getApprovedContestants(contestJid) {
  const token = getToken();
  return await contestContestantAPI.getApprovedContestants(token, contestJid);
}

export async function registerMyselfAsContestant(contestJid) {
  const token = getToken();
  await contestContestantAPI.registerMyselfAsContestant(token, contestJid);
  toastActions.showSuccessToast('Successfully registered to the contest.');
}

export async function unregisterMyselfAsContestant(contestJid) {
  const token = getToken();
  await contestContestantAPI.unregisterMyselfAsContestant(token, contestJid);
  toastActions.showSuccessToast('Successfully unregistered from the contest.');
}

export async function upsertContestants(contestJid, usernames) {
  const token = getToken();
  const response = await contestContestantAPI.upsertContestants(token, contestJid, usernames);
  if (Object.keys(response.insertedContestantProfilesMap).length === usernames.length) {
    toastActions.showSuccessToast('Contestants added.');
  }
  return response;
}

export async function deleteContestants(contestJid, usernames) {
  const token = getToken();
  const response = await contestContestantAPI.deleteContestants(token, contestJid, usernames);
  if (Object.keys(response.deletedContestantProfilesMap).length === usernames.length) {
    toastActions.showSuccessToast('Contestants removed.');
  }
  return response;
}
