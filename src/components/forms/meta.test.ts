import { Intent } from '@blueprintjs/core';

import { getIntent, getIntentClassName, isValid } from './meta';

describe('meta', () => {
  test('isValid()', () => {
    expect(isValid({ touched: false, valid: false })).toBeTruthy();
    expect(isValid({ touched: false, valid: true })).toBeTruthy();
    expect(isValid({ touched: true, valid: false })).toBeFalsy();
    expect(isValid({ touched: true, valid: true })).toBeTruthy();
  });

  test('getIntent()', () => {
    expect(getIntent({ touched: true, valid: false })).toEqual(Intent.DANGER);
    expect(getIntent({ touched: true, valid: true })).toBeUndefined();
  });

  test('getIntentClassName()', () => {
    expect(getIntentClassName({ touched: true, valid: false })['pt-intent-danger']).toBeTruthy();
    expect(getIntentClassName({ touched: true, valid: true })['pt-intent-danger']).toBeFalsy();
  });
});
