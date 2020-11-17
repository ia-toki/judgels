import { userAccountAPI } from '../../../modules/api/jophiel/userAccount';
import * as toastActions from '../../../modules/toast/toastActions';

export function resendActivationEmail(email: string) {
  return async () => {
    await userAccountAPI.resendActivationEmail(email);
    toastActions.showToast(`Email has been sent`);
  };
}
