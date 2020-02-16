import { userAccountAPI } from '../../../../modules/api/jophiel/userAccount';

export function activateUser(emailCode: string) {
  return async () => {
    await userAccountAPI.activateUser(emailCode);
  };
}
