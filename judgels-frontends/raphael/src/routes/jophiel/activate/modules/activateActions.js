import { userAccountAPI } from '../../../../modules/api/jophiel/userAccount';

export function activateUser(emailCode) {
  return async () => {
    await userAccountAPI.activateUser(emailCode);
  };
}
