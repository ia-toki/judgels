import { Intent } from '@blueprintjs/core';
import { connect } from 'react-redux';

import { APP_CONFIG, Mode } from '../../../conf';
import { FullPageLayout } from '../../../components/FullPageLayout/FullPageLayout';
import { ButtonLink } from '../../../components/ButtonLink/ButtonLink';
import { selectIsLoggedIn } from '../../../modules/session/sessionSelectors';

import ActiveContestsWidget from '../widgets/activeContests/ActiveContestsWidget/ActiveContestsWidget';
import TopRatingsWidget from '../widgets/topRatings/TopRatingsWidget/TopRatingsWidget';
import TopScorersWidget from '../widgets/topScorers/TopScorersWidget/TopScorersWidget';

import './HomePage.css';

import bannerImage from '../../../assets/images/welcome-banner.jpg';
import overlayImage from '../../../assets/images/welcome-overlay.png';

function HomePage({ isLoggedIn }) {
  const renderBanner = () => {
    if (isLoggedIn) {
      return null;
    }

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

  const renderWidgets = () => {
    return (
      <div className="home-widget-row">
        <div className="home-widget-row__two-thirds">
          <ActiveContestsWidget />
        </div>
        <div className="home-widget-row__one-third">
          {APP_CONFIG.mode !== Mode.PRIVATE_CONTESTS && <TopRatingsWidget />}
          {APP_CONFIG.mode !== Mode.PRIVATE_CONTESTS && APP_CONFIG.apiUrls.jerahmeel && <TopScorersWidget />}
        </div>
        <div className="clearfix" />
      </div>
    );
  };

  return (
    <FullPageLayout>
      {renderBanner()}
      {renderWidgets()}
    </FullPageLayout>
  );
}

const mapStateToProps = state => ({
  isLoggedIn: selectIsLoggedIn(state),
});

export default connect(mapStateToProps)(HomePage);
