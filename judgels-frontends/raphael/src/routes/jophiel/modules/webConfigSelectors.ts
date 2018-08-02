import { AppState } from 'modules/store';

export function selectRecaptchaSiteKey(state: AppState) {
  const value = state.jophiel.webConfig.value;
  return value && value.recaptcha && value.recaptcha.siteKey;
}

export function selectUserRegistrationUseRecaptcha(state: AppState) {
  const value = state.jophiel.webConfig.value;
  return (value && value.userRegistration && value.userRegistration.useRecaptcha) || false;
}
