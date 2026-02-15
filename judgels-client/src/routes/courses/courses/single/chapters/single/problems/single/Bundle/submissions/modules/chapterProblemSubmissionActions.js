import { submissionBundleAPI } from '../../../../../../../../../../../modules/api/jerahmeel/submissionBundle';
import { getToken } from '../../../../../../../../../../../modules/session';

export async function getSubmissions(chapterJid, username, problemAlias, page) {
  const token = getToken();
  return await submissionBundleAPI.getSubmissions(token, chapterJid, username, problemAlias, page);
}

export async function createItemSubmission(chapterJid, problemJid, itemJid, answer) {
  const token = getToken();
  const data = {
    containerJid: chapterJid,
    problemJid,
    itemJid,
    answer,
  };

  await submissionBundleAPI.createItemSubmission(token, data);
}

export async function getSubmissionSummary(chapterJid, problemAlias, language) {
  const token = getToken();
  return submissionBundleAPI.getSubmissionSummary(token, chapterJid, undefined, undefined, problemAlias, language);
}

export async function getLatestSubmissions(chapterJid, problemAlias) {
  const token = getToken();
  return submissionBundleAPI.getLatestSubmissions(token, chapterJid, problemAlias);
}
