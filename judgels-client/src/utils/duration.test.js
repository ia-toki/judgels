import { formatDuration, parseDuration } from './duration';

describe('duration', () => {
  test('formatDuration()', () => {
    expect(formatDuration(0)).toEqual('0s');
    expect(formatDuration(2 * 1000)).toEqual('2s');
    expect(formatDuration(3 * 60 * 1000)).toEqual('3m');
    expect(formatDuration(4 * 60 * 60 * 1000)).toEqual('4h');
    expect(formatDuration(5 * 24 * 60 * 60 * 1000)).toEqual('5d');
    expect(formatDuration((5 * 24 * 60 * 60 + 4 * 60 * 60 + 3 * 60 + 2) * 1000)).toEqual('5d 4h 3m 2s');
    expect(formatDuration((5 * 60 * 60 + 40 * 60) * 1000)).toEqual('5h 40m');
  });

  test('parseDuration()', () => {
    expect(parseDuration(' 0s ')).toEqual(0);
    expect(parseDuration(' 2s ')).toEqual(2 * 1000);
    expect(parseDuration(' 3m ')).toEqual(3 * 60 * 1000);
    expect(parseDuration(' 4h ')).toEqual(4 * 60 * 60 * 1000);
    expect(parseDuration(' 5d ')).toEqual(5 * 24 * 60 * 60 * 1000);
    expect(parseDuration(' 5d  4h    3m 2s ')).toEqual((5 * 24 * 60 * 60 + 4 * 60 * 60 + 3 * 60 + 2) * 1000);
    expect(parseDuration('5h 40m')).toEqual((5 * 60 * 60 + 40 * 60) * 1000);
  });
});
