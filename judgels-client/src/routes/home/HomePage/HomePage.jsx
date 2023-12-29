import { Intent } from '@blueprintjs/core';
import { connect } from 'react-redux';

import { ButtonLink } from '../../../components/ButtonLink/ButtonLink';
import { FullPageLayout } from '../../../components/FullPageLayout/FullPageLayout';
import { HtmlText } from '../../../components/HtmlText/HtmlText';
import { APP_CONFIG, isTLX } from '../../../conf';
import { selectIsLoggedIn } from '../../../modules/session/sessionSelectors';
import ActiveContestsWidget from '../widgets/activeContests/ActiveContestsWidget/ActiveContestsWidget';
import TopRatingsWidget from '../widgets/topRatings/TopRatingsWidget/TopRatingsWidget';
import TopScorersWidget from '../widgets/topScorers/TopScorersWidget/TopScorersWidget';

import './HomePage.scss';

function HomePage({ isLoggedIn }) {
  const renderBanner = () => {
    if (isLoggedIn) {
      return null;
    }

    return (
      <div className="home-banner">
        <div className="home-banner__contents">
          <div>
            <div className="home-banner__text home-banner__title">
              <HtmlText>{APP_CONFIG.welcomeBanner.title}</HtmlText>
            </div>
            <div className="home-banner__text home-banner__description">
              <HtmlText>{APP_CONFIG.welcomeBanner.description}</HtmlText>
            </div>
          </div>
          <div className="home-banner__buttons">
            {isTLX() && (
              <ButtonLink to="/register" intent={Intent.PRIMARY} large className="home-banner__button-register">
                Register and start training for free
              </ButtonLink>
            )}
            <ButtonLink to="/login" intent={Intent.NONE} large className="home-banner__button-login">
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
          {isTLX() && <TopRatingsWidget />}
          {isTLX() && <TopScorersWidget />}
        </div>
        <div className="clearfix" />
      </div>
    );
  };

  return (
    <>
      {renderBanner()}
      <FullPageLayout>{renderWidgets()}</FullPageLayout>
    </>
  );
}

const mapStateToProps = state => ({
  isLoggedIn: selectIsLoggedIn(state),
});

export default connect(mapStateToProps)(HomePage);
