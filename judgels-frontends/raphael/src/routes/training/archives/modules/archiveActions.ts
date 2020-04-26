import { SubmissionError } from 'redux-form';

import { selectToken } from '../../../../modules/session/sessionSelectors';
import { BadRequestError } from '../../../../modules/api/error';
import {
  archiveAPI,
  ArchiveCreateData,
  ArchiveUpdateData,
  ArchiveErrors,
} from '../../../../modules/api/jerahmeel/archive';
import * as toastActions from '../../../../modules/toast/toastActions';

export function createArchive(data: ArchiveCreateData) {
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

export function updateArchive(archiveJid: string, data: ArchiveUpdateData) {
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
