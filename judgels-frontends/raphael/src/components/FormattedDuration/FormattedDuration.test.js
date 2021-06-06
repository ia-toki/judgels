import { FormattedDuration } from './FormattedDuration';

describe('FormattedDuration', () => {
  test('FormattedDuration', () => {
    expect(FormattedDuration({ value: 1000 })).toEqual('1 second');
    expect(FormattedDuration({ value: 60 * 1000 })).toEqual('1 minute');
    expect(FormattedDuration({ value: 60 * 60 * 1000 })).toEqual('1 hour');
    expect(FormattedDuration({ value: 24 * 60 * 60 * 1000 })).toEqual('1 day');
    expect(
      FormattedDuration({ value: 5 * 24 * 60 * 60 * 1000 + 4 * 60 * 60 * 1000 + 3 * 60 * 1000 + 2 * 1000 })
    ).toEqual('5 days 4 hours 3 minutes 2 seconds');
  });
});
