import { getGradingLanguageFilenameExtensions } from '../../modules/api/gabriel/language.js';

export const Required = value => (value ? undefined : 'Required');

export const Username = value =>
  /^[a-zA-Z0-9._]{3,20}$/.test(value)
    ? undefined
    : 'Must contain between 3 and 20 alphanumeric characters, dots, or underscores';

export const NonnegativeNumber = value => (+value >= 0 ? undefined : 'Must be a non-negative number');

export const Slug = value =>
  /^[a-zA-Z0-9-]{3,75}$/.test(value) ? undefined : 'Must contain between 3 and 75 alphanumeric characters or dashes';

export const Alias = value =>
  /^[a-zA-Z0-9-]{1,20}$/.test(value) ? undefined : 'Must contain between 1 and 20 alphanumeric characters or dashes';

export const EmailAddress = value =>
  /[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}/.test(value) ? undefined : 'Invalid email address';

export const ConfirmPassword = (value, { password }) =>
  value === password ? undefined : 'Confirmed password does not match';

export const MaxCodeLength50KB = value => {
  return value && value.length <= 50 * 1024 ? undefined : 'Code length must be at most 50 KB';
};

export const MaxFileSize100KB = value => {
  return value && value.size <= 100 * 1024 ? undefined : 'File size must be at most 100 KB';
};

export const MaxFileSize300KB = value => {
  return !value || value.size <= 300 * 1024 ? undefined : 'File size must be at most 300 KB';
};

export const MaxFileSize10MB = value => {
  return !value || value.size <= 10 * 1024 * 1024 ? undefined : 'File size must be at most 10 MB';
};

export const MaxFileSize20MB = value => {
  return value && value.size <= 20 * 1024 * 1024 ? undefined : 'File size must be at most 20 MB';
};

export const Max100Lines = value => {
  return value
    .split('\n')
    .map(s => s.trim())
    .filter(s => s.length > 0).length <= 100
    ? undefined
    : 'Max 100 lines';
};

export const Max1000Lines = value => {
  return value
    .split('\n')
    .map(s => s.trim())
    .filter(s => s.length > 0).length <= 1000
    ? undefined
    : 'Max 1000 lines';
};

export function HasImageExtension(value) {
  const extensions = ['png', 'jpg', 'jpeg'];
  const re = new RegExp('\\.(' + extensions.join('|') + ')$', 'i');
  if (value.name.toLowerCase().match(re)) {
    return undefined;
  }
  return 'Allowed extensions: ' + extensions.join(', ') + '.';
}

export function CompatibleFilenameExtensionForGradingLanguage(value, { gradingLanguage }) {
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

export function composeValidators(...validators) {
  return (value, allValues) => validators.reduce((error, validator) => error || validator(value, allValues), undefined);
}
