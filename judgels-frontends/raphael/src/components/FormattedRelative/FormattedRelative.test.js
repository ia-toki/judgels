import { FormattedRelative } from './FormattedRelative';

describe('FormattedRelative', () => {
  beforeAll(() => {
    jest.useFakeTimers('modern');
    jest.setSystemTime(new Date(2021, 10, 10, 10, 10, 10));
  });

  afterAll(() => {
    jest.useRealTimers();
  });

  test('past', () => {
    expect(FormattedRelative({ value: new Date(2021, 10, 10, 10, 10, 10) })).toEqual('just now');
    expect(FormattedRelative({ value: new Date(2021, 10, 10, 10, 10, 6) })).toEqual('just now');
    expect(FormattedRelative({ value: new Date(2021, 10, 10, 10, 10, 5) })).toEqual('5 seconds ago');
    expect(FormattedRelative({ value: new Date(2021, 10, 10, 10, 9, 10) })).toEqual('1 minute ago');
    expect(FormattedRelative({ value: new Date(2021, 10, 10, 10, 5, 9) })).toEqual('5 minutes ago');
    expect(FormattedRelative({ value: new Date(2021, 10, 10, 10, 5, 6) })).toEqual('5 minutes ago');
    expect(FormattedRelative({ value: new Date(2021, 10, 10, 9, 10, 10) })).toEqual('1 hour ago');
    expect(FormattedRelative({ value: new Date(2021, 10, 10, 5, 6, 7) })).toEqual('5 hours ago');
    expect(FormattedRelative({ value: new Date(2021, 10, 9, 10, 10, 10) })).toEqual('1 day ago');
    expect(FormattedRelative({ value: new Date(2021, 10, 9, 1, 0, 0) })).toEqual('1 day ago');
    expect(FormattedRelative({ value: new Date(2021, 10, 8, 12, 0, 0) })).toEqual('2 days ago');
    expect(FormattedRelative({ value: new Date(2021, 9, 10, 10, 10, 10) })).toEqual('1 month ago');
    expect(FormattedRelative({ value: new Date(2021, 9, 1, 0, 0, 0) })).toEqual('1 month ago');
    expect(FormattedRelative({ value: new Date(2021, 8, 12, 0, 0, 0) })).toEqual('2 months ago');
    expect(FormattedRelative({ value: new Date(2020, 10, 10, 10, 10, 10) })).toEqual('1 year ago');
    expect(FormattedRelative({ value: new Date(2020, 9, 1, 0, 0, 0, 0) })).toEqual('1 year ago');
    expect(FormattedRelative({ value: new Date(2020, 3, 12, 0, 0, 0, 0) })).toEqual('2 years ago');
  });

  test('future', () => {
    expect(FormattedRelative({ value: new Date(2021, 10, 10, 10, 10, 11) })).toEqual('in 1 second');
    expect(FormattedRelative({ value: new Date(2021, 10, 10, 10, 10, 15) })).toEqual('in 5 seconds');
    expect(FormattedRelative({ value: new Date(2021, 10, 10, 10, 11, 10) })).toEqual('in 1 minute');
    expect(FormattedRelative({ value: new Date(2021, 10, 10, 10, 15, 11) })).toEqual('in 5 minutes');
    expect(FormattedRelative({ value: new Date(2021, 10, 10, 10, 15, 14) })).toEqual('in 5 minutes');
    expect(FormattedRelative({ value: new Date(2021, 10, 10, 11, 10, 10) })).toEqual('in 1 hour');
    expect(FormattedRelative({ value: new Date(2021, 10, 10, 15, 14, 13) })).toEqual('in 5 hours');
    expect(FormattedRelative({ value: new Date(2021, 10, 11, 10, 10, 10) })).toEqual('in 1 day');
    expect(FormattedRelative({ value: new Date(2021, 10, 11, 15, 10, 10) })).toEqual('in 1 day');
    expect(FormattedRelative({ value: new Date(2021, 10, 11, 23, 10, 10) })).toEqual('in 2 days');
    expect(FormattedRelative({ value: new Date(2021, 11, 10, 10, 10, 10) })).toEqual('in 1 month');
    expect(FormattedRelative({ value: new Date(2021, 11, 20, 0, 0, 0) })).toEqual('in 1 month');
    expect(FormattedRelative({ value: new Date(2021, 11, 30, 0, 0, 0) })).toEqual('in 2 months');
    expect(FormattedRelative({ value: new Date(2022, 10, 10, 10, 10, 10) })).toEqual('in 1 year');
    expect(FormattedRelative({ value: new Date(2022, 12, 0, 0, 0, 0) })).toEqual('in 1 year');
    expect(FormattedRelative({ value: new Date(2023, 6, 0, 0, 0, 0) })).toEqual('in 2 years');
  });
});
