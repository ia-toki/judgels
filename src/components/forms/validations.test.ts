import { ConfirmPassword, EmailAddress, Required, Username } from './validations';

test('Required', () => {
  expect(Required(undefined)).toBeTruthy();
  expect(Required('value')).toBeUndefined();
});

test('Username', () => {
  expect(Username('fu')).toBeTruthy();
  expect(Username('fusharfusharfusharfushar')).toBeTruthy();
  expect(Username(' fushar ')).toBeTruthy();
  expect(Username('fushar@')).toBeTruthy();
  expect(Username('_fus4.r')).toBeUndefined();
});

test('EmailAddress', () => {
  expect(EmailAddress('emaildomain')).toBeTruthy();
  expect(EmailAddress('email@domain')).toBeTruthy();
  expect(EmailAddress('emaildomain.com')).toBeTruthy();
  expect(EmailAddress('emaIL+judgels@domain.com')).toBeUndefined();
});

test('ConfirmPassword', () => {
  expect(ConfirmPassword('pass', { password: undefined })).toBeTruthy();
  expect(ConfirmPassword('pass', { password: 'Pass' })).toBeTruthy();
  expect(ConfirmPassword('pass', { password: 'pass' })).toBeUndefined();
});
