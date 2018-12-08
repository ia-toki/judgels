import { APP_CONFIG } from 'conf';

import { get, postMultipart } from '../http';

export interface ContestFile {
  name: string;
  size: number;
  lastModifiedTime: number;
}

export interface ContestFileConfig {
  canSupervise: boolean;
}

export interface ContestFilesResponse {
  data: ContestFile[];
  config: ContestFileConfig;
}

export function createContestFileAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.uriel}/contests`;

  return {
    getFiles: (token: string, contestJid: string): Promise<ContestFilesResponse> => {
      return get(`${baseURL}/${contestJid}/files`, token);
    },

    uploadFile: (token: string, contestJid: string, file: File): Promise<void> => {
      return postMultipart(`${baseURL}/${contestJid}/files`, token, { file: file });
    },
  };
}

export function getDownloadFileUrl(contestJid: string, filename: string) {
  return `${APP_CONFIG.apiUrls.uriel}/contests/${contestJid}/files/${filename}`;
}
