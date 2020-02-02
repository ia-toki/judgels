import * as ReactGA from 'react-ga';

import { APP_CONFIG } from './conf';

export function initGA(history) {
  if (APP_CONFIG.googleAnalytics) {
    ReactGA.initialize(APP_CONFIG.googleAnalytics.trackingId);
    history.listen(location => {
      let page = location.pathname.replace(/\/+$/, '');
      if (page === '') {
        page = '/';
      }
      page += location.search;

      ReactGA.set({ page });
      ReactGA.pageview(page);
    });
  }
}

export function setGAUser(userJid?: string) {
  if (APP_CONFIG.googleAnalytics) {
    if (userJid) {
      ReactGA.set({ userId: userJid });
    }
  }
}

export function sendGAEvent(args: ReactGA.EventArgs) {
  if (APP_CONFIG.googleAnalytics) {
    ReactGA.event(args);
  }
}
