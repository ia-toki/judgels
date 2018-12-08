import { get, postMultipart } from '../http';
import { baseContestURL } from './contest';

export interface ContestFile {
  name: string;
  size: number;
  lastModifiedTime: number;
}

export interface ContestFileConfig {
  canManage: boolean;
}

export interface ContestFilesResponse {
  data: ContestFile[];
  config: ContestFileConfig;
}

const baseURL = (contestJid: string) => `${baseContestURL(contestJid)}/files`;

export const contestFileAPI = {
  getFiles: (token: string, contestJid: string): Promise<ContestFilesResponse> => {
    return get(`${baseURL(contestJid)}`, token);
  },

  uploadFile: (token: string, contestJid: string, file: File): Promise<void> => {
    return postMultipart(`${baseURL(contestJid)}`, token, { file: file });
  },

  renderDownloadFilesUrl: (contestJid: string) => `${baseContestURL(contestJid)}/files`,

  renderDownloadFileUrl: (contestJid: string, filename: string) => `${baseContestURL(contestJid)}/files/${filename}`,
};
