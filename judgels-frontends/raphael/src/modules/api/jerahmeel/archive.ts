import { APP_CONFIG } from '../../../conf';
import { get } from '../http';

export interface Archive {
  slug: string;
  name: string;
  category: string;
}

export interface ArchivesResponse {
  data: Archive[];
}

export const baseArchivesURL = `${APP_CONFIG.apiUrls.jerahmeel}/archives`;

export const archiveAPI = {
  getArchives: (token: string): Promise<ArchivesResponse> => {
    return get(baseArchivesURL, token);
  },
};
