import { stringify } from 'query-string';
import ReactGA from 'react-ga4';

import { APP_CONFIG } from './conf';

export function initGA() {
  if (APP_CONFIG.googleAnalytics) {
    ReactGA.initialize(APP_CONFIG.googleAnalytics.trackingId);
  }
}

export function sendGAPageview(location) {
  if (APP_CONFIG.googleAnalytics) {
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
  if (APP_CONFIG.googleAnalytics) {
    if (userJid) {
      ReactGA.set({ user_id: userJid });
    }
  }
}

export function sendGAEvent(args) {
  if (APP_CONFIG.googleAnalytics) {
    ReactGA.event(args);
  }
}
