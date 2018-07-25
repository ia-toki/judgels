export function getLeague(rating?: number) {
  if (rating === null || rating === undefined) {
    return 'unrated';
  }
  if (rating < 1650) {
    return 'gray';
  }
  if (rating < 1750) {
    return 'green';
  }
  if (rating < 2000) {
    return 'blue';
  }
  if (rating < 2200) {
    return 'purple';
  }
  if (rating < 2500) {
    return 'orange';
  }
  if (rating < 3000) {
    return 'red';
  }
  return 'legend';
}

export function getLeagueClass(rating?: number) {
  return 'league-' + getLeague(rating);
}
