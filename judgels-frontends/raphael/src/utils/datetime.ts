export function formatDateTime(date: Date, withUTCOffset?: boolean) {
  const year = '' + date.getFullYear();
  const month = asTwoDigitString(date.getMonth() + 1);
  const dat = asTwoDigitString(date.getDate());
  const hour = asTwoDigitString(date.getHours());
  const minute = asTwoDigitString(date.getMinutes());

  let res = `${year}-${month}-${dat} ${hour}:${minute}`;
  if (withUTCOffset) {
    res = `${res} ${formatDateTimezoneOffset(date)}`;
  }
  return res;
}

export function formatDateTimezoneOffset(date: Date): string {
  const offset = -date.getTimezoneOffset() / 60;
  return 'UTC' + (offset === 0 ? '' : offset < 0 ? offset : '+' + offset);
}

export function parseDateTime(str: string) {
  const re = /^(\d+)-(\d+)-(\d+) (\d+):(\d+)$/;
  const res = re.exec(str);
  if (!res) {
    return new Date();
  }
  return new Date(+res[1], +res[2] - 1, +res[3], +res[4], +res[5], 0, 0);
}

function asTwoDigitString(x: number): string {
  return x < 10 ? '0' + x : '' + x;
}
