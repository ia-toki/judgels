export const Required = value => (value ? undefined : 'Required');

export const Username = value =>
  /^[a-zA-Z0-9\._]{3,20}$/.test(value)
    ? undefined
    : 'Must contain between 3 and 20 alphanumeric characters, dots, or underscores';

export const EmailAddress = value =>
  /[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}/.test(value) ? undefined : 'Invalid email address';

export const ConfirmPassword = (value, { password }) =>
  value === password ? undefined : 'Confirmed password does not match';
