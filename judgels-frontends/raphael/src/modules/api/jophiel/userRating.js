import { APP_CONFIG } from '../../../conf';
import { post } from '../http';

const baseURL = `${APP_CONFIG.apiUrls.jophiel}/user-rating`;

export const userRatingAPI = {
  updateRatings: (token, data) => {
    return post(`${baseURL}`, token, data);
  },
};

export function getRatingName(rating) {
  if (rating === null || rating === undefined) {
    return 'unrated';
  }
  const publicRating = rating.publicRating;
  if (publicRating < 1650) {
    return 'gray';
  }
  if (publicRating < 1750) {
    return 'green';
  }
  if (publicRating < 2000) {
    return 'blue';
  }
  if (publicRating < 2200) {
    return 'purple';
  }
  if (publicRating < 2500) {
    return 'orange';
  }
  if (publicRating < 3000) {
    return 'red';
  }
  return 'legend';
}

export function getRatingClass(rating) {
  return 'rating-' + getRatingName(rating);
}
