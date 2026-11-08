import { stringify } from 'query-string';
import ReactGA from 'react-ga4';

import { getGoogleAnalyticsTrackingId } from './modules/webConfig';

export function initGA() {
  const trackingId = getGoogleAnalyticsTrackingId();
  if (trackingId && !ReactGA.isInitialized) {
    ReactGA.initialize(trackingId);
  }
}

export function sendGAPageview(location) {
  if (ReactGA.isInitialized) {
    let page = location.pathname.replace(/\/+$/, '');
    if (page === '') {
      page = '/';
    }

    const search = stringify(location.search);
    if (search) {
      page += `?${search}`;
    }

    ReactGA.set({ page });
    ReactGA.send({ hitType: 'pageview', page });
  }
}

export function setGAUser(userJid) {
  if (ReactGA.isInitialized) {
    if (userJid) {
      ReactGA.set({ user_id: userJid });
    }
  }
}

export function sendGAEvent(args) {
  if (ReactGA.isInitialized) {
    ReactGA.event(args);
  }
}
