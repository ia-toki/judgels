export function formatDuration(ms: number) {
  let num = Math.floor(ms / 1000);
  let res = '';

  if (num === 0) {
    return '0s';
  }

  if (num % 60 !== 0) {
    res = `${num % 60}s ${res}`;
  }
  num = Math.floor(num / 60);

  if (num % 60 !== 0) {
    res = `${num % 60}m ${res}`;
  }
  num = Math.floor(num / 60);

  if (num % 24 !== 0) {
    res = `${num % 24}h ${res}`;
  }
  num = Math.floor(num / 24);

  if (num !== 0) {
    res = `${num}d ${res}`;
  }

  return res.slice(0, -1);
}

export function parseDuration(str: string) {
  let secs = 0;
  for (const token of str.split(' ')) {
    let match;

    match = /^(\d+)d$/.exec(token);
    if (match) {
      secs += +match[1] * 24 * 60 * 60;
    }

    match = /^(\d+)h$/.exec(token);
    if (match) {
      secs += +match[1] * 60 * 60;
    }

    match = /^(\d+)m$/.exec(token);
    if (match) {
      secs += +match[1] * 60;
    }

    match = /^(\d+)s$/.exec(token);
    if (match) {
      secs += +match[1];
    }
  }

  return secs * 1000;
}
