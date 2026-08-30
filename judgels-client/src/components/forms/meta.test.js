import { Intent } from '@blueprintjs/core';

import { getIntent, getIntentClassName, isValid } from './meta';

describe('meta', () => {
  test('isValid()', () => {
    expect(isValid({ touched: false, modified: false, submitFailed: false, valid: false })).toBeTruthy();
    expect(isValid({ touched: true, modified: false, submitFailed: false, valid: false })).toBeTruthy();
    expect(isValid({ touched: false, modified: true, submitFailed: false, valid: false })).toBeTruthy();
    expect(isValid({ touched: true, modified: true, submitFailed: false, valid: false })).toBeFalsy();
    expect(isValid({ touched: true, modified: true, submitFailed: false, valid: true })).toBeTruthy();
    expect(isValid({ touched: false, modified: false, submitFailed: true, valid: false })).toBeFalsy();
    expect(isValid({ touched: false, modified: false, submitFailed: true, valid: true })).toBeTruthy();
  });

  test('getIntent()', () => {
    expect(getIntent({ touched: true, modified: true, valid: false })).toEqual(Intent.DANGER);
    expect(getIntent({ touched: true, modified: true, valid: true })).toBeUndefined();
  });

  test('getIntentClassName()', () => {
    expect(getIntentClassName({ touched: true, modified: true, valid: false })['pt-intent-danger']).toBeTruthy();
    expect(getIntentClassName({ touched: true, modified: true, valid: true })['pt-intent-danger']).toBeFalsy();
  });
});
