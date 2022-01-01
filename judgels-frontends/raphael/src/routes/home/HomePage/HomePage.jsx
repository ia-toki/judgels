import { Intent } from '@blueprintjs/core';
import { connect } from 'react-redux';

import { APP_CONFIG, Mode } from '../../../conf';
import { FullPageLayout } from '../../../components/FullPageLayout/FullPageLayout';
import { ButtonLink } from '../../../components/ButtonLink/ButtonLink';
import { selectIsLoggedIn } from '../../../modules/session/sessionSelectors';

import ActiveContestsWidget from '../widgets/activeContests/ActiveContestsWidget/ActiveContestsWidget';
import TopRatingsWidget from '../widgets/topRatings/TopRatingsWidget/TopRatingsWidget';
import TopScorersWidget from '../widgets/topScorers/TopScorersWidget/TopScorersWidget';

import './HomePage.scss';
import { HtmlText } from '../../../components/HtmlText/HtmlText';

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
            {APP_CONFIG.mode !== Mode.PRIVATE_CONTESTS && (
              <ButtonLink to="/register" intent={Intent.PRIMARY} large className="home-banner__button">
                Register and start training for free
              </ButtonLink>
            )}
            <ButtonLink to="/login" intent={Intent.NONE} large className="home-banner__button">
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
