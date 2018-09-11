import { formatDateTime, formatDateTimezoneOffset, parseDateTime } from './datetime';

describe('datetime', () => {
  const dat = new Date('2018-08-30T13:10:00+07:00');

  test('formatDateTime()', () => {
    expect(formatDateTime(dat)).toEqual('2018-08-30 13:10');
    expect(formatDateTime(dat, true)).toEqual('2018-08-30 13:10 UTC+7');
  });

  test('formatDateTimeOffset()', () => {
    expect(formatDateTimezoneOffset(dat)).toEqual('UTC+7');
  });

  test('parseDateTime()', () => {
    const parsedDate = parseDateTime('2018-08-30 13:10 UTC+7');
    expect(parsedDate.getFullYear()).toEqual(2018);
    expect(parsedDate.getMonth()).toEqual(7);
    expect(parsedDate.getDate()).toEqual(30);
    expect(parsedDate.getHours()).toEqual(13);
    expect(parsedDate.getMinutes()).toEqual(10);
  });
});
