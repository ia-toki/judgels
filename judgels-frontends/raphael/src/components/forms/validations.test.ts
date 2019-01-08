import {
  CompatibleFilenameExtensionForGradingLanguage,
  ConfirmPassword,
  EmailAddress,
  MaxFileSize300KB,
  NonnegativeNumber,
  Required,
  Slug,
  Username,
} from './validations';

describe('validations', () => {
  test('Required', () => {
    expect(Required(undefined)).toBeTruthy();
    expect(Required('value')).toBeUndefined();
  });

  test('NonnegativeNumber', () => {
    expect(NonnegativeNumber(undefined)).toBeTruthy();
    expect(NonnegativeNumber('1')).toBeUndefined();
    expect(NonnegativeNumber('0')).toBeUndefined();
    expect(NonnegativeNumber('-1')).toBeTruthy();
  });

  test('Username', () => {
    expect(Username('fu')).toBeTruthy();
    expect(Username('fusharfusharfusharfushar')).toBeTruthy();
    expect(Username(' fushar ')).toBeTruthy();
    expect(Username('fushar@')).toBeTruthy();
    expect(Username('_fus4.r')).toBeUndefined();
  });

  test('Alias', () => {
    expect(Slug('fusharfusharfusharfushar')).toBeTruthy();
    expect(Slug(' A ')).toBeTruthy();
    expect(Slug('A.')).toBeTruthy();
    expect(Slug('A-1')).toBeUndefined();
  });

  test('Slug', () => {
    expect(Slug('fu')).toBeTruthy();
    expect(Slug('fusharfusharfusharfushar')).toBeTruthy();
    expect(Slug(' fushar ')).toBeTruthy();
    expect(Slug('fushar.')).toBeTruthy();
    expect(Slug('fus-4-r')).toBeUndefined();
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

  test('MaxFileSize300KB', () => {
    expect(MaxFileSize300KB({ size: 500000 } as File)).toBeTruthy();
    expect(MaxFileSize300KB({ size: 300000 } as File)).toBeUndefined();
  });

  test('CompatibleFilenameExtensionForGradingLanguage', () => {
    expect(
      CompatibleFilenameExtensionForGradingLanguage({ name: 'sol.pas' } as File, { gradingLanguage: 'Cpp11' })
    ).toEqual('Allowed extensions: cc, cpp.');
    expect(
      CompatibleFilenameExtensionForGradingLanguage({ name: 'sol.cpp' } as File, { gradingLanguage: 'Cpp11' })
    ).toBeUndefined();
  });
});
