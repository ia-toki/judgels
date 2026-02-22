import { ForbiddenError } from '../../../../modules/api/error';
import { sessionAPI } from '../../../../modules/api/jophiel/session';
import { SessionErrors } from '../../../../modules/api/jophiel/session';
import { queryClient } from '../../../../modules/queryClient';
import { clearSession, getToken } from '../../../../modules/session';

export async function logOut() {
  try {
    await sessionAPI.logOut(getToken());
  } catch (error) {
    if (error instanceof ForbiddenError) {
      if (error.message === SessionErrors.LogoutDisabled) {
        throw new Error('Logout is currently disabled.');
      }
    }
    throw error;
  }
  clearSession();
  queryClient.setQueryData(['user-web-config'], { role: {} });
}
