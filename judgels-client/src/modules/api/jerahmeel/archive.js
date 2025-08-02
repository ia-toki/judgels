import { get, post } from '../http';

export const ArchiveErrors = {
  SlugAlreadyExists: 'Jerahmeel:ArchiveSlugAlreadyExists',
};

export const baseArchivesURL = `/api/v2/archives`;

export function baseArchiveURL(archiveJid) {
  return `${baseArchivesURL}/${archiveJid}`;
}

export const archiveAPI = {
  createArchive: (token, data) => {
    return post(baseArchivesURL, token, data);
  },

  updateArchive: (token, archiveJid, data) => {
    return post(`${baseArchiveURL(archiveJid)}`, token, data);
  },

  getArchives: token => {
    return get(baseArchivesURL, token);
  },
};
