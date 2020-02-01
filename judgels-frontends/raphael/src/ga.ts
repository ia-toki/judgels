import * as ReactGA from 'react-ga';

import { APP_CONFIG } from './conf';

export function initGA(history) {
  if (APP_CONFIG.googleAnalytics) {
    ReactGA.initialize(APP_CONFIG.googleAnalytics.trackingId);
    history.listen(location => {
      ReactGA.set({ page: location.pathname + location.search });
      ReactGA.pageview(location.pathname + location.search);
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
