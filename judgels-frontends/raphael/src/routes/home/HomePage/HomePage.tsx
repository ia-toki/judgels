import { Intent } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';

import { APP_CONFIG, Mode } from '../../../conf';
import { FullPageLayout } from '../../../components/FullPageLayout/FullPageLayout';
import { ButtonLink } from '../../../components/ButtonLink/ButtonLink';
import { AppState } from '../../../modules/store';
import { selectIsLoggedIn } from '../../../modules/session/sessionSelectors';

import ActiveContestsWidget from '../widgets/activeContests/ActiveContestsWidget/ActiveContestsWidget';
import HallOfFameWidget from '../widgets/hallOfFame/HallOfFameWidget/HallOfFameWidget';

import './HomePage.css';

interface HomePageProps {
  isLoggedIn: boolean;
}

class HomePage extends React.Component<HomePageProps> {
  render() {
    return (
      <FullPageLayout>
        {this.renderBanner()}
        {this.renderWidgets()}
      </FullPageLayout>
    );
  }

  private renderBanner = () => {
    if (this.props.isLoggedIn) {
      return null;
    }

    const bannerImage = require('../../../assets/images/welcome-banner.jpg');
    const overlayImage = require('../../../assets/images/welcome-overlay.png');

    return (
      <div className="home-banner">
        <div>
          <img src={bannerImage} alt="banner" className="home-banner__image" />
        </div>
        <div>
          <img src={overlayImage} alt="banner-overlay" className="home-banner__overlay" />
        </div>
        <div className="home-banner__contents">
          <div>
            <h1 className="home-banner__text">{APP_CONFIG.welcomeBanner.title}</h1>
            <p className="home-banner__text">{APP_CONFIG.welcomeBanner.description}</p>
          </div>
          <div className="home-banner__buttons">
            {APP_CONFIG.mode !== Mode.PRIVATE_CONTESTS && (
              <ButtonLink to="/register" intent={Intent.PRIMARY} className="home-banner__button">
                Register
              </ButtonLink>
            )}
            <ButtonLink to="/login" intent={Intent.NONE} className="home-banner__button">
              Log in
            </ButtonLink>
          </div>
        </div>
      </div>
    );
  };

  private renderWidgets = () => {
    return (
      <div className="home-widget-row">
        <div className="home-widget-row__two-thirds">
          <ActiveContestsWidget />
        </div>
        {APP_CONFIG.mode !== Mode.PRIVATE_CONTESTS && (
          <div className="home-widget-row__one-third">
            <HallOfFameWidget />
          </div>
        )}
        <div className="clearfix" />
      </div>
    );
  };
}

const mapStateToProps = (state: AppState) => ({
  isLoggedIn: selectIsLoggedIn(state),
});

export default connect(mapStateToProps)(HomePage);
