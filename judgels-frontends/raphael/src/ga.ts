import * as ReactGA from 'react-ga';

import { APP_CONFIG } from './conf';

export function initGA(history) {
  if (APP_CONFIG.googleAnalytics) {
    ReactGA.initialize(APP_CONFIG.googleAnalytics.trackingId);
    history.listen(location => {
      const page = (location.pathname + location.search).replace(/\/+$/, '');
      ReactGA.set({ page });
      ReactGA.pageview(page);
    });
  }
}

export function setGAUser(userJid?: string) {
  if (APP_CONFIG.googleAnalytics) {
    if (userJid) {
      ReactGA.set({ userId: this.props.userJid });
    }
  }
}

export function sendGAEvent(args: ReactGA.EventArgs) {
  if (APP_CONFIG.googleAnalytics) {
    ReactGA.event(args);
  }
}
