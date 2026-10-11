import { get, put } from './http';
import { baseProblemURL } from './problem';

export const PartnerPermission = {
  View: 'VIEW',
  Update: 'UPDATE',
};

export const partnerPermissionNamesMap = {
  [PartnerPermission.View]: 'View',
  [PartnerPermission.Update]: 'View and update',
};

const baseURL = problemJid => `${baseProblemURL(problemJid)}/partners`;

export const problemPartnerAPI = {
  getPartners: (token, problemJid) => {
    return get(baseURL(problemJid), token);
  },

  setPartners: (token, problemJid, data) => {
    return put(baseURL(problemJid), token, data);
  },
};
