import { BadRequestError } from '../../../../modules/api/error';
import { ArchiveErrors, archiveAPI } from '../../../../modules/api/jerahmeel/archive';
import { SubmissionError } from '../../../../modules/form/submissionError';
import { selectToken } from '../../../../modules/session/sessionSelectors';

import * as toastActions from '../../../../modules/toast/toastActions';

export function createArchive(data) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    try {
      await archiveAPI.createArchive(token, data);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === ArchiveErrors.SlugAlreadyExists) {
        throw new SubmissionError({ slug: 'Slug already exists' });
      }
      throw error;
    }
    toastActions.showSuccessToast('Archive created.');
  };
}

export function updateArchive(archiveJid, data) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    try {
      await archiveAPI.updateArchive(token, archiveJid, data);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === ArchiveErrors.SlugAlreadyExists) {
        throw new SubmissionError({ slug: 'Slug already exists' });
      }
      throw error;
    }
    toastActions.showSuccessToast('Archive updated.');
  };
}

export function getArchives() {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await archiveAPI.getArchives(token);
  };
}
