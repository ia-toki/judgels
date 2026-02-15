import { BadRequestError } from '../../../../modules/api/error';
import { ArchiveErrors, archiveAPI } from '../../../../modules/api/jerahmeel/archive';
import { SubmissionError } from '../../../../modules/form/submissionError';
import { getToken } from '../../../../modules/session';

import * as toastActions from '../../../../modules/toast/toastActions';

export async function createArchive(data) {
  const token = getToken();
  try {
    await archiveAPI.createArchive(token, data);
  } catch (error) {
    if (error instanceof BadRequestError && error.message === ArchiveErrors.SlugAlreadyExists) {
      throw new SubmissionError({ slug: 'Slug already exists' });
    }
    throw error;
  }
  toastActions.showSuccessToast('Archive created.');
}

export async function updateArchive(archiveJid, data) {
  const token = getToken();
  try {
    await archiveAPI.updateArchive(token, archiveJid, data);
  } catch (error) {
    if (error instanceof BadRequestError && error.message === ArchiveErrors.SlugAlreadyExists) {
      throw new SubmissionError({ slug: 'Slug already exists' });
    }
    throw error;
  }
  toastActions.showSuccessToast('Archive updated.');
}

export async function getArchives() {
  const token = getToken();
  return await archiveAPI.getArchives(token);
}
