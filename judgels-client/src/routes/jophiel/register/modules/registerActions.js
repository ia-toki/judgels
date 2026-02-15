import { userAccountAPI } from '../../../../modules/api/jophiel/userAccount';
import { userRegistrationWebAPI } from '../../../../modules/api/jophiel/userRegistration';
import { userSearchAPI } from '../../../../modules/api/jophiel/userSearch';
import { SubmissionError } from '../../../../modules/form/submissionError';

export async function getWebConfig() {
  return await userRegistrationWebAPI.getWebConfig();
}

export async function registerUser(data) {
  const usernameExists = await userSearchAPI.usernameExists(data.username);
  const emailExists = await userSearchAPI.emailExists(data.email);

  if (usernameExists || emailExists) {
    const usernameError = usernameExists ? { username: 'Username already exists' } : {};
    const emailError = emailExists ? { email: 'Email already exists' } : {};
    throw new SubmissionError({
      ...usernameError,
      ...emailError,
    });
  }

  await userAccountAPI.registerUser(data);
}
