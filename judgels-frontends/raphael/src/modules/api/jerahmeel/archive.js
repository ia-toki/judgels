import { APP_CONFIG } from '../../../conf';
import { get, post } from '../http';

export interface Archive {
  id: number;
  jid: string;
  slug: string;
  name: string;
  category: string;
  description: string;
}

export interface ArchivesResponse {
  data: Archive[];
}

export interface ArchiveCreateData {
  slug: string;
  name: string;
  category: string;
  description?: string;
}

export interface ArchiveUpdateData {
  slug?: string;
  name?: string;
  category?: string;
  description?: string;
}

export enum ArchiveErrors {
  SlugAlreadyExists = 'Jerahmeel:ArchiveSlugAlreadyExists',
}

export const baseArchivesURL = `${APP_CONFIG.apiUrls.jerahmeel}/archives`;

export function baseArchiveURL(archiveJid: string) {
  return `${baseArchivesURL}/${archiveJid}`;
}

export const archiveAPI = {
  createArchive: (token: string, data: ArchiveCreateData): Promise<Archive> => {
    return post(baseArchivesURL, token, data);
  },

  updateArchive: (token: string, archiveJid: string, data: ArchiveUpdateData): Promise<Archive> => {
    return post(`${baseArchiveURL(archiveJid)}`, token, data);
  },

  getArchives: (token: string): Promise<ArchivesResponse> => {
    return get(baseArchivesURL, token);
  },
};
