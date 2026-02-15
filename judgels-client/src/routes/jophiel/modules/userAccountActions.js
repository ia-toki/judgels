import { userAccountAPI } from '../../../modules/api/jophiel/userAccount';

import * as toastActions from '../../../modules/toast/toastActions';

export async function resendActivationEmail(email) {
  await userAccountAPI.resendActivationEmail(email);
  toastActions.showToast(`Email has been sent`);
}
