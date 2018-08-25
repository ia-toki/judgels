import { getGradingLanguageFilenameExtensions } from 'modules/api/gabriel/language';

export const Required = value => (value ? undefined : 'Required');

export const Username = value =>
  /^[a-zA-Z0-9._]{3,20}$/.test(value)
    ? undefined
    : 'Must contain between 3 and 20 alphanumeric characters, dots, or underscores';

export const Slug = value =>
  /^[a-zA-Z0-9-]{3,20}$/.test(value) ? undefined : 'Must contain between 3 and 20 alphanumeric characters or dashes';

export const EmailAddress = value =>
  /[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}/.test(value) ? undefined : 'Invalid email address';

export const ConfirmPassword = (value, { password }) =>
  value === password ? undefined : 'Confirmed password does not match';

export const MaxFileSize300KB = (value: File) => {
  return value && value.size <= 300 * 1024 ? undefined : 'File size must be at most 300 KB';
};

export function CompatibleFilenameExtensionForGradingLanguage(value: File, { gradingLanguage }) {
  if (!gradingLanguage || !value) {
    return undefined;
  }
  const extensions = getGradingLanguageFilenameExtensions(gradingLanguage);
  const re = new RegExp('\\.(' + extensions.join('|') + ')$', 'i');
  if (value.name.match(re)) {
    return undefined;
  }
  return 'Allowed extensions: ' + extensions.join(', ') + '.';
}
