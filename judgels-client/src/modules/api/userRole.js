import { APP_CONFIG } from '../../conf';
import { get, post, put } from './http';

export const UserRole = {
  Superadmin: 'SUPERADMIN',
  SystemAdmin: 'SYSTEM_ADMIN',
  ProblemAdmin: 'PROBLEM_ADMIN',
  ProblemWriter: 'PROBLEM_WRITER',
  ContestAdmin: 'CONTEST_ADMIN',
  TrainingAdmin: 'TRAINING_ADMIN',
};

// `roles` is the list of roles a user holds, e.g. `webConfig.roles`. The superadmin holds every role.
function holds(roles, role) {
  return !!roles && (roles.includes(role) || roles.includes(UserRole.Superadmin));
}

export const isSuperadmin = roles => !!roles && roles.includes(UserRole.Superadmin);
export const isSystemAdmin = roles => holds(roles, UserRole.SystemAdmin);
export const isProblemAdmin = roles => holds(roles, UserRole.ProblemAdmin);
export const isProblemWriter = roles => holds(roles, UserRole.ProblemWriter) || isProblemAdmin(roles);
export const isContestAdmin = roles => holds(roles, UserRole.ContestAdmin);
export const isTrainingAdmin = roles => holds(roles, UserRole.TrainingAdmin);

const baseURL = `${APP_CONFIG.apiUrl}/v2/user-roles`;

export const userRoleAPI = {
  getRoles: token => {
    return get(baseURL, token);
  },

  upsertRoles: (token, usernames, roles) => {
    return post(`${baseURL}/batch-upsert`, token, { usernames, roles });
  },

  setRoles: (token, userJid, roles) => {
    return put(`${baseURL}/${userJid}`, token, roles);
  },
};
